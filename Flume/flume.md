# Flume

## 概述

### 优点

- 输入的的数据速率大于写入目的存储的速率，`Flume`会进行缓冲。
- `Flume`中的事务基于`Channel`，使用了两个事务模型（`sender` + `receiver`），确保消息被可靠发送



### 组成

![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206081433179.png)

- `Flume`使用两个独立的事务分别负责从`soucrce`到`channel`，以及从`channel`到`sink`的事件传递。
- 一旦<u>事务</u>中所有的数据全部成功提交到`channel`，那么`source`才认为该数据读取完成。
- 同理，只有成功被`sink`写出去的数据，才会从`channel`中移除。



### 流式处理过程

![img](https://img-blog.csdnimg.cn/20200616195106155.png)

**Put事务流程**：

- `doPut`：将批数据先写入临时缓冲区`putList`
- `doCommit`：检查`channel`内存队列是否足够合并。
- `doRollback`：`channel`内存队列空间不足，回滚数据

**Take事务**：

- `doTake`：先将数据取到临时缓冲区`takeList`
- `doCommit`：如果数据全部发送成功，则清除临时缓冲区`takeList`
- `doRollback`：数据发送过程中如果出现异常，`rollback`将临时缓冲区`takeList`中的数据归还给`channel`内存队列。



### Flume Agent内部原理

![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206081442477.png)





## docker创建flume

```bash
docker run --name flume --restart always --net=host  \
                -v /home/etlgroup/flume/conf:/opt/flume-config/flume.conf  \
                -v /home/etlgroup/flume/flume_log:/var/tmp/flume_log   \
                -v /home/etlgroup/flume/logs:/opt/flume/logs  \
                -v /tmp/test_logs/:/tmp/test_logs/  \
                -e FLUME_AGENT_NAME="agent"  \
                -d probablyfine/flume
```



## Log-Flume-Kafka

log_flume_kafka.conf

```properties
# 自定义 source 的名字
agent1.sources = logSource
# 自定义 channel 的名字
agent1.channels = memoryChannel
# 自定义 sink 的名字
agent1.sinks = kafkaSource
 
# -------------- source-----------------
# 指定 source 使用的 channel
agent1.sources.logSource.type = exec
agent1.sources.logSource.command = tail -F /home/etlgroup/flume/test_logs/app.log
agent1.sources.logSource.channels = c1
 
# -----------------channel-----------------
# channel类型
agent1.channels.memoryChannel.type = memory
# channel存储的事件容量
agent1.channels.memoryChannel.capacity = 10000
# 事务容量
agent1.channels.memoryChannel.transactionCapacity = 10000
agent1.channels.memoryChannel.keep-alive = 60

# -----------------sink-----------------
# 指定 sink 使用的 channel
agent1.sinks.kafkaSource.channel = memoryChannel
agent1.sinks.kafkaSource.type = org.apache.flume.sink.kafka.KafkaSink
agent1.sinks.kafkaSource.topic = mytopic
agent1.sinks.kafkaSource.brokerList = 10.0.102.75:9093
agent1.sinks.kafkaSource.batchSize = 100
```



```
# Name the components on this agent
app.sources = r1
app.channels = c1

# Describe/configure the source
app.sources.r1.type = exec
app.sources.r1.command = tail -F /home/etlgroup/flume/test_logs/app.log
app.sources.r1.channels = c1


# Use a channel which buffers events in KafkaChannel
# 设置app.channels.c1的类型
app.channels.c1.type = org.apache.flume.channel.kafka.KafkaChannel
# 设置Kafka集群中的Broker      集群以逗号分割
app.channels.c1.kafka.bootstrap.servers = 10.0.102.75:9093    
# 设置app.channels.c1使用的Kafka的Topic
app.channels.c1.kafka.topic = mytopic
# 设置成不按照flume event格式解析数据,因为同一个Kafka topic可能有非flume Event类数据传入
app.channels.c1.parseAsFlumeEvent = false
# 设置消费者组,保证每次消费时能够获取上次对应的Offset
app.channels.c1.kafka.consumer.group.id = 01
# 设置消费过程poll()超时时间(ms)
app.channels.c1.pollTimeout = 1000
```



## Kakfa-Flume-Kafka

### Flume配置

kafka_flume_kafka.conf

```properties
# 自定义 source 的名字
agent1.sources = kafkaSource1
# 自定义 channel 的名字
agent1.channels = memoryChannel
# 自定义 sink 的名字
agent1.sinks = kafkaSink
 
# -------------- source-----------------
# 指定 source 使用的 channel
agent1.sources.kafkaSource1.channels = memoryChannel
# kafkaSource相关配置
agent1.sources.kafkaSource1.type = org.apache.flume.source.kafka.KafkaSource
# kafka 的 zookeeper 地址
# agent1.sources.kafkaSource1.zookeeperConnect = 10.0.102.75:2181
agent1.sources.kafkaSource1.topic = mytopic
# agent1.sources.kafkaSource1.groupId = flume
agent1.sources.kafkaSource1.kafka.bootstrap.servers = 10.0.102.75:9093
# 消费超时时间
agent1.sources.kafkaSource1.kafka.consumer.timeout.ms = 100
 
# -----------------channel-----------------
# channel类型
agent1.channels.memoryChannel.type = memory
# channel存储的事件容量
agent1.channels.memoryChannel.capacity = 10000
# 事务容量
agent1.channels.memoryChannel.transactionCapacity = 10000
agent1.channels.memoryChannel.keep-alive = 60

# -----------------sink-----------------
# 指定 sink 使用的 channel
agent1.sinks.kafkaSink.channel = memoryChannel
agent1.sinks.kafkaSink.type = org.apache.flume.sink.kafka.KafkaSink
agent1.sinks.kafkaSink.topic = topic001
agent1.sinks.kafkaSink.kafka.bootstrap.servers = 10.0.102.75:9093
agent1.sinks.kafkaSink.requiredAcks = 1
agent1.sinks.kafkaSink.batchSize = 100
```





## Kakfa-Flume-Mysql

```properties
#-------- kafkaSource相关配置-----------------
# 定义消息源类型
agent.sources.kafkaSource.type = org.apache.flume.source.kafka.KafkaSource
# 定义kafka所在zk的地址
agent.sources.kafkaSource.zookeeperConnect = ip:port
# 配置消费的kafka topic
agent.sources.kafkaSource.topic = mytopic
# 配置消费者组的id
agent.sources.kafkaSource.groupId = flume
# 消费超时时间,参照如下写法可以配置其他所有kafka的consumer选项。注意格式从kafka.xxx开始是consumer的配置属性
agent.sources.kafkaSource.kafka.consumer.timeout.ms = 100
```



### Flume配置

kafka_flume_mysql.conf

```properties
# 自定义 source 的名字
agent1.sources = kafkaSource
# 自定义 channel 的名字
agent1.channels = memoryChannel
# 自定义 sink 的名字
agent1.sinks = mysqlSink
 
# -------------- source-----------------
# 指定 source 使用的 channel
agent1.sources.kafkaSource.channels = memoryChannel
# kafkaSource相关配置
agent1.sources.kafkaSource.type = org.apache.flume.source.kafka.KafkaSource
# kafka 的 zookeeper 地址
agent1.sources.kafkaSource.zookeeperConnect = 10.0.102.75:2181
agent1.sources.kafkaSource.topic = mytopic
agent1.sources.kafkaSource.groupId = flume
agent1.sources.kafkaSource.kafka.bootstrap.servers = 10.0.102.75:9093
# 消费超时时间
agent1.sources.kafkaSource.kafka.consumer.timeout.ms = 100
 
# -----------------channel-----------------
# channel类型
agent1.channels.memoryChannel.type = memory
# channel存储的事件容量
agent1.channels.memoryChannel.capacity = 10000
# 事务容量
agent1.channels.memoryChannel.transactionCapacity = 10000
agent1.channels.memoryChannel.keep-alive = 60

# -----------------sink-----------------
# 指定 sink 使用的 channel
agent1.sinks.mysqlSink.channel = memoryChannel
agent1.sinks.mysqlSink.type = org.flume.sink.mysql.MysqlSink
agent1.sinks.mysqlSink.mysqlurl = jdbc:mysql://10.0.102.75:3306/flume
agent1.sinks.mysqlSink.username = root
agent1.sinks.mysqlSink.password = 123456
agent1.sinks.mysqlSink.tablename = test
```



### 启动Flume

```
flume-ng agent -c /opt/flume-config/flume.conf -f /opt/flume-config/flume.conf/kafka-flume-kafka.conf -n agent1 -Dflume.root.logger=INFO,console
```

windows

```
flume-ng agent -c E:/developer_tools/apache-flume-1.9.0-bin/conf -f E:/developer_tools/apache-flume-1.9.0-bin/conf/kafka-flume-kafka.conf -n agent1
```



### log4j.properties

如果 -c 指定的文件夹内没有log4j.properties， 则自己添加一份。

```properties
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# Define some default values that can be overridden by system properties.
#
# For testing, it may also be convenient to specify
# -Dflume.root.logger=DEBUG,console when launching flume.
#flume.root.logger=DEBUG,console
flume.root.logger=INFO,LOGFILE
flume.log.dir=/opt/flume/logs
flume.log.file=flume.log
log4j.logger.org.apache.flume.lifecycle = INFO
log4j.logger.org.jboss = WARN
log4j.logger.org.mortbay = INFO
log4j.logger.org.apache.avro.ipc.NettyTransceiver = WARN
log4j.logger.org.apache.hadoop = INFO
# Define the root logger to the system property "flume.root.logger".
log4j.rootLogger=${flume.root.logger}
# Stock log4j rolling file appender
# Default log rotation configuration
log4j.appender.LOGFILE=org.apache.log4j.RollingFileAppender
log4j.appender.LOGFILE.MaxFileSize=100MB
log4j.appender.LOGFILE.MaxBackupIndex=10
log4j.appender.LOGFILE.File=${flume.log.dir}/${flume.log.file}
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d{dd MMM yyyy HH:mm:ss,SSS} %-5p [%t] (%C.%M:%L) %x - %m%n
# Warning: If you enable the following appender it will fill up your disk if you don't have a cleanup job!
# This uses the updated rolling file appender from log4j-extras that supports a reliable time-based rolling policy.
# See http://logging.apache.org/log4j/companions/extras/apidocs/org/apache/log4j/rolling/TimeBasedRollingPolicy.html
# Add "DAILY" to flume.root.logger above if you want to use this
log4j.appender.DAILY=org.apache.log4j.rolling.RollingFileAppender
log4j.appender.DAILY.rollingPolicy=org.apache.log4j.rolling.TimeBasedRollingPolicy
log4j.appender.DAILY.rollingPolicy.ActiveFileName=${flume.log.dir}/${flume.log.file}
log4j.appender.DAILY.rollingPolicy.FileNamePattern=${flume.log.dir}/${flume.log.file}.%d{yyyy-MM-dd}
log4j.appender.DAILY.layout=org.apache.log4j.PatternLayout
log4j.appender.DAILY.layout.ConversionPattern=%d{dd MMM yyyy HH:mm:ss,SSS} %-5p [%t] (%C.%M:%L) %x - %m%n
# console
# Add "console" to flume.root.logger above if you want to use this
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d (%t) [%p - %l] %m%n
```





















