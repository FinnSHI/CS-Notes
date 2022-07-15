# Debezium

## CDC

### 概念

变化数据捕捉：Change Data Capture

### 分类

#### 侵入式

对源系统带来了性能的影响，即对源库执行了SQL语句。

- 时间戳
- 快照
- 触发器

#### 非侵入式

对源系统没有侵入性。

- 日志

### 方法

1. 时间戳或版本号追踪：源系统要有对应的数据列表示最后的时间变化

2. 快照方法：比较两个表中的数据，去发现不匹配的行。

3. 触发器方式：创建触发器，如INSERT触发器，UPDATE触发器，DELETE触发器

4. 日志方式：源库变更写进事务日志中，然后使用kafka去捕获变更，并将变更推送到目标数据库中。

   

## Debezium

### 概念

- 一种CDC工具，通过捕获数据库日志变更实现。
- Debezium构建在kafka上，可以用kafka消费每一个行级别 (row-level) 的更改。
- 只有已提交的更改才是可见的，不需要担心 transaction 或者 roll back
- Debezium 为所支持的数据库更改提供了统一的模型。
- Debezium用持久化、有副本备份的日志来记录数据库数据的变化。

### 抽取原理

![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206211415245.webp)

### 搭建测试环境

#### 1 运行Zookeeper

```bash
docker run -it --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 debezium/zookeeper:1.6
```

#### 2 运行Kafka

```bash
docker run -it --name kafka -p 9092:9092 --link zookeeper:zookeeper debezium/kafka:1.6
```

#### 3 运行PostgreSQL

```bash
docker run -it --rm --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres debezium/example-postgres:1.6
```

> 上面代码中使用的是：debezium/example-postgres:1.6，查看Debezimu官方文档以及其它示例都是这个。实际上Debezimu对PostgreSQL 9~13都进行了Docker封装，可以根据自己的需要在Docker Hub中选择响应的PostgreSQL版本。

> debezium/postgres很小，使用也比较方便，而且也进行了必要的设置，无须再进行额外的配置就可以直接使用。

#### 4 运行Debezium Connect

```bash
docker run -it --rm --name connect1 -p 8083:8083 -e GROUP_ID=1 -e CONFIG_STORAGE_TOPIC=my_connect_configs -e OFFSET_STORAGE_TOPIC=my_connect_offsets -e STATUS_STORAGE_TOPIC=my_connect_statuses --link zookeeper1:zookeeper --link kafka1:kafka --link postgres1:postgres debezium/connect
```

Debezium的container启动时需要传入如下环境变量：

- GROUP_ID: 分组ID，若需要启动多个Debezium的实例组成集群，那么它们的GROUP_ID必须被设置为一样
- CONFIG_STORAGE_TOPIC：下面需要调用Debezium提供的RestFUL API管理connector，connector的信息就是保存在CONFIG_STORAGE_TOPIC指定的kafka topic下。
- OFFSET_STORAGE_TOPIC: connector监控数据流的offset，若我们使用的是PostgreSQL Connector，那么OFFSET_STORAGE_TOPIC指定的topic中存的就是PostgreSQL的lsn。



### Docker-compose

```yml
version: "1"
services:
  postgres:
    image: debezium/postgres
    container_name: postgres
    hostname: postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - 5432:5432

  zookeeper:
    image: debezium/zookeeper
    container_name: zookeeper
    hostname: zookeeper
    environment:
      ZOOKEEPER_SERVER_ID: 1
    ports:
      - 2181:2181
      - 2888:2888
      - 3888:3888

  kafka:
    image: debezium/kafka
    container_name: kafka
    hostname: kafka
    ports:
      - 9093:9093
    environment:
      BROKER_ID: 1
      ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENERS: LISTENER_INNER://kafka:29092,LISTENER_OUTER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: LISTENER_INNER://kafka:29092,LISTENER_OUTER://10.0.102.75:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: LISTENER_INNER:PLAINTEXT,LISTENER_OUTER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: LISTENER_INNER
      KAFKA_ALLOW_PLAINTEXT_LISTENER: 'yes'
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    depends_on:
      - zookeeper

  connect:
    image: debezium/connect
    container_name: connect
    hostname: connect
    ports:
      - 8083:8083
    environment:
      GROUP_ID: 1
      CONFIG_STORAGE_TOPIC: herodotus_connect_configs
      OFFSET_STORAGE_TOPIC: herodotus_connect_offsets
      STATUS_STORAGE_TOPIC: herodotus_connect_statuses
      BOOTSTRAP_SERVERS: kafka:9093
    depends_on:
      - kafka
```



#### 5 创建Connector

经过上面4个步骤后，Debezium的测试环境就搭建好了，现在需要调用Debezium提供的API创建connector，使Debezium与数据库之间建立关系。我们把下面的payload POST到`http://<ip addr of debezium>:8083/connectors/`。

```json
{
  "name": "fulfillment-connector",  
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector", 
    "database.hostname": "192.168.99.100", 
    "database.port": "5432", 
    "database.user": "postgres", 
    "database.password": "postgres", 
    "database.dbname" : "postgres", 
    "database.server.name": "fulfillment", 
    "table.include.list": "public.inventory"
  }
}
```

1. "name"：注册到Kafka Connect服务的Connector名称
2. "connector.class"：PostgreSQL connector class名称
3. "database.hostname"：PostgreSQL 数据库地址
4. "database.port"：PostgreSQL 数据库端口
5. "database.user"：PostgreSQL 数据库用户名
6. "database.password"：PostgreSQL数据密码
7. "database.dbname"：连接的PostgreSQL数据库
8. "database.server.name"：虚拟的数据库Server名称，可以根据实际需求定义，消费Kafka数据时要使用该值
9. "table.include.list"：监听的数据表列表，以","分割。PostgreSQL要将表名写全，格式"<schema-name>.<table-name>"。如果没有特定的Schema，那么就是默认的`public`