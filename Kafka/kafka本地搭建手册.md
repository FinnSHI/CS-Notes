# kafka本地搭建手册

本手册 ZooKeeper 版本：3.6.3
本手册 Kafka 版本：2.8.1

## 一、ZooKeeper（单机版）安装

### 下载

下载地址：https://archive.apache.org/dist/zookeeper/zookeeper-3.6.3/

![image-20220513155409705](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205131554791.png)

- 注意：apache-zookeeper-3.6.3.tar.gz 是未编译的版本，apache-zookeeper-3.6.3-bin.tar.gz  才是编译好的版本。

### 配置

1. 文件解压到指定目录，本手册使用目录为 E:\developer_tools\apache-zookeeper-3.6.3-bin

2. apache-zookeeper-3.6.3-bin\conf 下复制一份 zoo_sample.cfg文件

3. 在 E:\developer_tools\apache-zookeeper-3.6.3-bin 目录下新建文件夹 data 和 logs

4. 修改和添加配置

   ```
   # 修改配置
   # 存放内存数据库快照的目录
   dataDir=E:\\developer_tools\\apache-zookeeper-3.6.3\\data
   
   # 添加配置
   # 存放事务日志目录
   dataLogDir=E:\\developer_tools\\apache-zookeeper-3.6.3\\logs
   # AdminServer端口
   admin.serverPort=7070
   ```

   > zookeeper服务启动时会启动一个AdminServer的服务，端口会占用8080，如果你有启动别的项目占了8080端口就会报错无法启动，所以在这添加配置 admin.serverPort=7070 来将启动端口修改（7070随便填的，不冲突就行）。

   ![image-20220513155902659](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205131559741.png)

### 启动服务

进入bin目录下,双击zkServer.cmd

![image-20220513160445552](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205131604686.png)

### 验证是否安装成功

在 bin 目录下双击 zkCli.cmd，打开客户端（此时的服务端 zkServer 的 dos窗口不要关闭），出现"Welcome"字样，说明安装成功!

![image-20220513160527319](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205131605448.png)



## 二、安装 Kafka

### 下载

下载地址：https://kafka.apache.org/downloads

![image-20220513160749844](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205131607952.png)

> 注意：Kafka 3.0.0 已弃用 Java 8
>
> - The deprecation of support for Java 8

### 安装

1.  编辑文件Kafka配置文件， E:\developer_tools\kafka_2.13-2.8.1\config\server.properties
2.  找到并编辑`log.dirs=E:\\developer_tools\\kafka_2.13-2.8.1\\logs`  (logs为自定义文件夹)
3.  找到并编辑 zookeeper.connect=localhost:2181。表示本地运行(默认的可以不改)
4.  Kafka会按照默认，在9092端口上运行，并连接zookeeper的默认端口：2181。



## 三、启动 Kafka 服务

1. 进入 E:\developer_tools\kafka_2.13-2.8.1\bin\windows

2. 执行：

   ```cmd
   kafka-server-start.bat ..\..\config\server.properties
   ```

![image-20220513161833104](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205131618190.png)



## 四、测试

### 创建 TOPIC

新建cmd窗口，进入kafka的windows目录下，输入以下命令，创建一个叫topic001的主题

```cmd
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topic001
```

- 分区名 topic001
- 分区数为1

### 创建生产者

新建cmd窗口,进入kafka的windows目录下，输入以下命令

```
kafka-console-producer.bat --broker-list localhost:9092 --topic topic001
```

### 创建消费者

新建cmd窗口,进入kafka的windows目录下，输入以下命令

```cmd
kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic topic001 --from-beginning
```

### 生产消息并消费

![image-20220513164121970](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205131641083.png)