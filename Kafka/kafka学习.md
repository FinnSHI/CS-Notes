# Kafka学习

## 1 kafka消息过期时间

### 全局消息过期时间设置

./kafka/config/server.properties

```xml
log.retention.hours=168 (配置该参数即可)
log.cleanup.policy=delete （默认，可不配置）
```

- kafka消息默认过期时间是168h（7天）
- 对所有topic有效



### 特定topic消息过期时间设置

./kafka/bin

```shell
./kafka-configs.sh --zookeeper localhost:2181 --alter --entity-name mytopic --entity-type topics --add-config retention.ms=86400000
```

- 对 `mytopic` 这个topic设置过期时间为86400000ms（1天）



### kafka过期消息删除过程

> kafka 的消息存储在 partition 的多个 segment 中，segment 大小由配置决定：
>
> - 如：log.segment.bytes=1073741824  【1GB】

#### 删除过程

1. kafka 会定期扫描非工作的 segment 文件，如果过期，则会对该segment文件（一个log文件和两个index文件）打上 .delete 标签

   ```shell
   -rw-r--r-- 1 root root 1073740353 Nov 13 03:02 00000000000108550131.log.deleted
   -rw-r--r-- 1 root root 526304 Nov 13 03:02 00000000000108550131.index.deleted
   -rw-r--r-- 1 root root 697704 Nov 13 03:02 0000000000108550131.timeindex.deleted
   ```

2. 最后kafka会有专门的删除日志定时任务来扫描，把 .delete 文件从硬盘上删除

#### 原理

- kafka删除消息是以 segment 为维度，一个segment包含了一段时期的全部消息并存储在一个文件中，比如上文提到的 00000000000108550131.log

- 删除时是一次性把这个过期的文件包含所有消息全部删除，效率非常高。

- 由于kafka至少要保留一个segment用于存取消息，所以也不会去删除里面过期的消息。

  > 实际上，也存在着设置了消息7天过期，但是kafka里面仍存在着10天前的数据，这就是由kafka的删除特性决定的。