# KafkaClient用户手册

## 一.接口文档

### 不指定分区生产和消费消息

#### 生产消息

-  目的：指定一个主题，并生产消息 
-  路由：`http://10.0.102.75:18098/api/kafka/produce/topic/message`
-  请求参数 

```json
{
    "brokers":[{
        "brokerHost": "10.0.102.75",
        "brokerPort": 9093
    }],
    "topic": "test_0621",
    "message": {"message": "hello"}
}
```

- 返回参数

```json
{
    "code": 200,
    "message": "执行成功",
    "result": "生产消息成功！",
    "detail": null
}
```

#### 消费消息

-  目的：指定一个主题，并消费消息，limit 是接收到的消息的最大数量。
-  路由：`http://10.0.102.75:18098/api/kafka/consume/topic/message`
-  请求参数 

```json
{
    "brokers":[{
        "brokerHost": "10.0.102.75",
        "brokerPort": 9093
    }],
    "topic": "test_0621",
    "limit": 3
}
```

- 返回参数

```json
{
    "code": 200,
    "message": "执行成功",
    "result": [
        "{\"message\":\"hello\"}",
        "{\"message\":\"hello\"}",
        "{\"message\":\"hello\"}"
    ],
    "detail": null
}
```



### 指定分区生产和消费消息

#### 生产消息

-  目的：指定一个主题和**分区**，并生成消息。
-  路由：`http://10.0.102.75:18098/api/kafka/produce/topic/partition/message`
-  请求参数 

```json
{
    "brokers":[{
        "brokerHost": "10.0.102.75",
        "brokerPort": 9093
    }],
    "topic": "test_0621",
    "partition": 1,
    "message": {"message": "这里是分区1"}
}
```

- 返回参数

```json
{
    "code": 200,
    "message": "执行成功",
    "result": "生产消息成功！",
    "detail": null
}
```

#### 消费消息

-  目的：指定一个主题和**分区**，并指定 **offset** 和 limit，然后消费消息。
-  路由：`http://10.0.102.75:18098/api/kafka/consume/topic/partition/message`
-  请求参数 

```json
{
    "brokers":[{
        "brokerHost": "10.0.102.75",
        "brokerPort": 9093
    }],
    "topic": "test_0621",
    "partition": 1,
    "offset": 2,
    "limit": 2
}
```

- 返回参数

```json
{
    "code": 200,
    "message": "执行成功",
    "result": [
        "{\"message\":\"这里是分区1_2\"}",
        "{\"message\":\"这里是分区1_3\"}"
    ],
    "detail": null
}
```



### 主题相关

#### 创建主题

-  目的：创建一个主题，并指定分区数量 
-  路由：`http://10.0.102.75:18098/api/kafka/topic/create`
-  请求参数 

```json
{
    "brokers":[{
        "brokerHost": "10.0.102.75",
        "brokerPort": 9093
    }],
    "topic": "test_0621",
    "partition": 5
}
```

- 返回参数

```json
{
    "code": 200,
    "message": "执行成功",
    "result": "创建主题成功",
    "detail": null
}
```

#### 统计一个主题内分区的数量

-  目的：统计一个主题内分区的数量 
-  路由：`http://10.0.102.75:18098/api/kafka/consume/topic/partition/total`
-  请求参数 

```json
{
    "brokers":[{
        "brokerHost": "10.0.102.75",
        "brokerPort": 9093
    }],
    "topic": "test_0621"
}
```

- 返回参数

```json
{
    "code": 200,
    "message": "执行成功",
    "result": 5,
    "detail": null
}
```



### 分区相关

#### 统计一个分区内消息的数量

-  目的：统计一个分区内消息的数量 
-  路由：`http://10.0.102.75:18098/api/kafka/consume/topic/partition/message/total`
-  请求参数 

```json
{
    "brokers":[{
        "brokerHost": "10.0.102.75",
        "brokerPort": 9093
    }],
    "topic": "test_no_part",
    "partition": 2
}
```

- 返回参数

```json
{
    "code": 200,
    "message": "执行成功",
    "result": 3,
    "detail": null
}
```

