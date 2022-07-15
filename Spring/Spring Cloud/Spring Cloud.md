# Spring Cloud

## 分布式

> 我的910便利网已经部署到两台服务器去了，但是越来越多的人去访问。现在也逐渐承受不住啦。那现在怎么办啊？？那继续充钱变强？？作为一个理智的我，肯定得想想是哪里有问题。现在910便利网的模块有好几个，全都丢在同一个Tomcat里边。
>
> 其实有些模块的访问是很低的(比如后台管理)，那我可不可以这样做：将每个模块**抽取独立**出来，访问量大的模块用好的服务器装着，没啥人访问的模块用差的服务器装着。这样的好处是：一、**资源合理利用了**(没人访问的模块用性能差的服务器，访问量大的模块**单独提升性能**就好了)。二、**耦合度降低了**：每个模块独立出来，各干各的事(专业的人做专业的事)，便于扩展

- 将业务功能拆分，模块之间独立。大业务拆分成小业务，部署在不同的服务器上。
- 访问量低的模块放在差的服务器
- 访问量高的模块放在好的服务器



## CAP理论

### CAP

- C：数据一致性(consistency)

- - **所有**节点拥有数据的最新版本

- A：可用性(availability)

- - 数据具备高可用性

- P：分区容错性(partition-tolerance)

- - **容忍网络出现分区**，分区之间网络不可达。



### 举例

面有三个节点(它们是集群的)，此时三个节点都能够相互通信：

![图片](https://mmbiz.qpic.cn/mmbiz_png/2BGWl1qPxib3LiaOIcUWPt6eU6UmOJhnk0sywl0k9oteicfTrJEJyHibMJ2hF9mIykyTrnCNgQfJaO7OOfV8aj876A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

由于我们的系统是分布式的，节点之间的通信是通过网络来进行的。**只要是分布式系统**，那很有可能会出现一种情况：因为一些**故障**，使得有些**节点之间不连通**了，整个网络就分成了**几块区域**。

- 数据就散布在了这些不连通的区域中，这就叫**分区**

![图片](https://mmbiz.qpic.cn/mmbiz_png/2BGWl1qPxib3LiaOIcUWPt6eU6UmOJhnk0ePdtv4K0F39YTStF2icicOIVNXgNB11H3cI63HNX2ZG0CFiaLZoS6lKDA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

现在出现了网络分区后，此时有一个请求过来了，想要注册一个账户。

![图片](https://mmbiz.qpic.cn/mmbiz_png/2BGWl1qPxib3LiaOIcUWPt6eU6UmOJhnk0awjlYGDKVia3RZa61QznX0WmKibL7xyXibfrXMTXAoaTus0vkoX4r1TvA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1)

此时我们**节点一和节点三是不可通信的**，这就有了抉择：

- 如果**允许**当前用户注册一个账户，此时注册的记录数据只会在节点一和节点二或者节点二和节点三**同步**，因为节点一和节点三的记录不能同步的。

- - 这种情况其实就是选择了可用性(availability)，抛弃了数据一致性(consistency)

- 如果**不允许**当前用户注册一个账户(就是要**等到**节点一和节点三恢复通信)。节点一和节点三一旦恢复通信，我们就可以**保证节点拥有的数据是最新版本**。

- - 这种情况其实就是抛弃了可用性(availability)，选择了数据一致性(consistency)





## Spring Cloud

### 功能

#### 基础功能

- 服务治理： Spring Cloud Eureka
- 客户端负载均衡： Spring Cloud Ribbon
- 服务容错保护： Spring  Cloud Hystrix  
- 声明式服务调用： Spring  Cloud Feign
- API网关服务：Spring Cloud Zuul
- 分布式配置中心： Spring Cloud Config



#### 高级功能

- 消息总线： Spring  Cloud Bus
- 消息驱动的微服务： Spring Cloud Stream
- 分布式服务跟踪： Spring  Cloud Sleuth



### Eureka



> 服务间如果通过 ip 地址进行通信，要是一个服务的 ip 变了，需要对其他所有服务进行 ip 的手动维护，这非常麻烦。

Eureka 用于服务治理。服务都注册到 Eureka 上，由 Eureka 来管理所有的信息。

![image-20220615180242079](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206151802209.png)