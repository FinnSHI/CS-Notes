如果要使用kafka，需要将这两个jar包上传到docker的flume下lib文件夹内。
![image-20220722162455419](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221624479.png)

docker启动flume的例子：
```bash
docker run --name flume --restart always --net=host  \
-v /home/etlgroup/flume/conf:/opt/flume-config/flume.conf  \
-v /home/etlgroup/flume/flume_log:/var/tmp/flume_log   \
-v /home/etlgroup/flume/logs:/opt/flume/logs  \
-v /tmp/test_logs/:/tmp/test_logs/  \
-e FLUME_AGENT_NAME="agent"  \
-d probablyfine/flume
```
flume启动的例子：
```bash
flume-ng agent -c /opt/flume-config/flume.conf -f /opt/flume-config/flume.conf/kafka-flume-kafka.conf -n agent1 -Dflume.root.logger=INFO,console
```
# 1.kafka=>logger
```properties
# example.conf: A single-node Flume configuration

# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources.r1.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.r1.batchSize = 1000
a1.sources.r1.batchDurationMillis = 2000
a1.sources.r1.kafka.bootstrap.servers = 10.0.102.75:9093
a1.sources.r1.kafka.topics = topic001
a1.sources.r1.kafka.consumer.group.id = test

# Describe the sink
a1.sinks.k1.type = logger

# Use a channel which buffers events in memory
a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 1000

# Bind the source and sink to the channel
a1.sources.r1.channels = c1
a1.sinks.k1.channel = c1
```

# 2.logger=>kafka
```properties
# example.conf: A single-node Flume configuration
  
# Name the components on this agent
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources = r1
a1.channels = c1
a1.sources.r1.type = exec
a1.sources.r1.command = tail -F /opt/flume/logs/app.log
a1.sources.r1.channels = c1

a1.sinks.k1.channel = c1
a1.sinks.k1.type = org.apache.flume.sink.kafka.KafkaSink
a1.sinks.k1.kafka.topic = topic001
a1.sinks.k1.kafka.bootstrap.servers = 10.0.102.75:9093
a1.sinks.k1.kafka.flumeBatchSize = 20
a1.sinks.k1.kafka.producer.acks = 1
a1.sinks.k1.kafka.producer.linger.ms = 1
a1.sinks.k1.kafka.producer.compression.type = snappy

a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 1000
```
注意：追踪的文件应该在容器内，因此，启动容器时，应该做文件夹挂载，使得宿主机和容器的文件夹能够同步。
# 3.logger=>mysql（自定义）
将下面的 jar 包放到 flume 的 lib 下。
![image-20220722162517616](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221625654.png)

```java
public class MysqlSinker extends AbstractSink implements Configurable {
    private static final Logger logger = LoggerFactory.getLogger(MysqlSinker.class);
    private Connection connect;
    private Statement stmt;
    private String url;
    private String username;
    private String password;
    private String tableName;
    
    // 在整个sink结束时执行一遍
    @Override
    public synchronized void stop() {
        // TODO Auto-generated method stub
        super.stop();
    }
    
    // 在整个sink开始时执行一遍，用来初始化数据库连接
    @Override
    public synchronized void start() {
        // TODO Auto-generated method stub
        super.start();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(url, username, password);
            // 连接URL为 jdbc:mysql//服务器地址/数据库名 ，后面的2个参数分别是登陆用户名和密码
            stmt = connect.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    // 不断循环调用，处理消息Event（本例就是插入数据库）
    public Status process() throws EventDeliveryException {
        
        // TODO Auto-generated method stub
        //事务，获取event什么的都是模板。仿照别的sink写就OK
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        Event event = null;
        Status status = null;
        txn.begin();
        try {
            event = ch.take();
            
            if (event != null) {
                //获取body中的数据
                String body = new String(event.getBody(), "UTF-8");
                
                //如果日志中有以下关键字的不需要保存，过滤掉
                if (body.contains("delete") || body.contains("drop") || body.contains("alert")) {
                    status = Status.BACKOFF;
                } else {
                    //存入Mysql
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String createTime = df.format(new Date());
                    
                    PreparedStatement stmt = connect.prepareStatement("insert into " + tableName + " (create_time, content) values (?, ?)");
                    stmt.setString(1, createTime);
                    stmt.setString(2, body);
                    stmt.execute();
                    stmt.close();
                    status = Status.READY;
                }
            } else {
                status = Status.BACKOFF;
            }
            
            txn.commit();
        } catch (Throwable t) {
            txn.rollback();
            t.getCause().printStackTrace();
            status = Status.BACKOFF;
        } finally {
            txn.close();
        }
        
        return status;
    }
    //从配置文件中读取各种属性，并进行一些非空验证
    public void configure(Context context) {
        url = context.getString("url");
        Preconditions.checkNotNull(url, "url must be set!!");
        username = context.getString("username");
        Preconditions.checkNotNull(username, "username must be set!!");
        //我的mysql没有密码。所以这里不检查密码为空
        password = context.getString("password");
        // Preconditions.checkNotNull(password, "password must be set!!");
        tableName = context.getString("tableName");
        Preconditions.checkNotNull(tableName, "tableName must be set!!");
    }
}
```

```properties
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources.r1.channels = c1
a1.sources.r1.type = exec
a1.sources.r1.command = tail -F /opt/flume/logs/app.log

a1.sinks.k1.channel = c1
a1.sinks.k1.type = com.aiit.sink.MysqlSink
a1.sinks.k1.url = jdbc:mysql://10.0.102.75:3306/flume
a1.sinks.k1.username = root
a1.sinks.k1.password = 123456
a1.sinks.k1.tableName = mysql_sink_test

a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
```
# 4.kafka=>mysql（自定义）
```java
public class MysqlSinker extends AbstractSink implements Configurable {
    private static final Logger logger = LoggerFactory.getLogger(MysqlSinker.class);
    private Connection connect;
    private Statement stmt;
    private String url;
    private String username;
    private String password;
    private String tableName;
    
    // 在整个sink结束时执行一遍
    @Override
    public synchronized void stop() {
        // TODO Auto-generated method stub
        super.stop();
    }
    
    // 在整个sink开始时执行一遍，用来初始化数据库连接
    @Override
    public synchronized void start() {
        // TODO Auto-generated method stub
        super.start();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect = DriverManager.getConnection(url, username, password);
            // 连接URL为 jdbc:mysql//服务器地址/数据库名 ，后面的2个参数分别是登陆用户名和密码
            stmt = connect.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    // 不断循环调用，处理消息Event（本例就是插入数据库）
    public Status process() throws EventDeliveryException {
        
        // TODO Auto-generated method stub
        //事务，获取event什么的都是模板。仿照别的sink写就OK
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        Event event = null;
        Status status = null;
        txn.begin();
        try {
            event = ch.take();
            
            if (event != null) {
                //获取body中的数据
                String body = new String(event.getBody(), "UTF-8");
                
                //如果日志中有以下关键字的不需要保存，过滤掉
                if (body.contains("delete") || body.contains("drop") || body.contains("alert")) {
                    status = Status.BACKOFF;
                } else {
                    //存入Mysql
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String createTime = df.format(new Date());
                    
                    PreparedStatement stmt = connect.prepareStatement("insert into " + tableName + " (create_time, content) values (?, ?)");
                    stmt.setString(1, createTime);
                    stmt.setString(2, body);
                    stmt.execute();
                    stmt.close();
                    status = Status.READY;
                }
            } else {
                status = Status.BACKOFF;
            }
            
            txn.commit();
        } catch (Throwable t) {
            txn.rollback();
            t.getCause().printStackTrace();
            status = Status.BACKOFF;
        } finally {
            txn.close();
        }
        
        return status;
    }
    //从配置文件中读取各种属性，并进行一些非空验证
    public void configure(Context context) {
        url = context.getString("url");
        Preconditions.checkNotNull(url, "url must be set!!");
        username = context.getString("username");
        Preconditions.checkNotNull(username, "username must be set!!");
        //我的mysql没有密码。所以这里不检查密码为空
        password = context.getString("password");
        // Preconditions.checkNotNull(password, "password must be set!!");
        tableName = context.getString("tableName");
        Preconditions.checkNotNull(tableName, "tableName must be set!!");
    }
}
```

```properties
a1.sources = r1
a1.sinks = k1
a1.channels = c1

a1.sources.r1.channels = c1
a1.sources.r1.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.r1.batchSize = 1000
a1.sources.r1.batchDurationMillis = 2000
a1.sources.r1.kafka.bootstrap.servers = 10.0.102.75:9093
a1.sources.r1.kafka.topics = topic001
a1.sources.r1.kafka.consumer.group.id = test

a1.sinks.k1.channel = c1
a1.sinks.k1.type = com.aiit.sink.MysqlSink
a1.sinks.k1.url = jdbc:mysql://10.0.102.75:3306/flume
a1.sinks.k1.username = root
a1.sinks.k1.password = 123456
a1.sinks.k1.tableName = mysql_sink_test

a1.channels.c1.type = memory
a1.channels.c1.capacity = 1000
a1.channels.c1.transactionCapacity = 100
```

