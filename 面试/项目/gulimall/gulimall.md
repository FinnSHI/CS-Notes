# 谷粒商城

## 可能会问的问题

![image-20220720152034874](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207201520920.png)

![image-20220720152050758](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207201520803.png)



## pdf

[谷粒商城-分布式高级-图.pdf](file:///D:/谷粒商城/Guli Mall/课件和文档(老版)/高级篇/资料图片/谷粒商城-分布式高级-图.pdf)

## 性能提升

使用 jmeter 进行压力测试

### 测试报告

|            压测内容            | 压测线程数 | 吞吐量 | 90%响应时间 | 99%响应时间 |
| :----------------------------: | :--------: | :----: | :---------: | :---------: |
|             Nginx              |    100     |  1221  |     100     |     154     |
|            Gateway             |    100     | 18200  |     14      |     33      |
|            简单服务            |    100     | 22972  |      6      |     10      |
|        首页一级菜单渲染        |    100     |        |             |             |
| 三级分类数据获取（写法优化过） |    100     |        |             |             |
|        首页全量数据获取        |    100     |        |             |             |
|        gateway+简单服务        |    100     |  4155  |     36      |     53      |
|     nignx+gateway+简单服务     |    100     |  278   |     396     |     960     |
|             全链路             |    100     |        |             |             |

- 吞吐量：接口每秒的并发能力

中间件越多，性能损失越大，大多损失在网络交互。



### 优化方法

#### 1 mysql 添加索引

1. 对 pms_category 的 parent_cid 添加索引

   - 检测查出 parent_cid 为 1 的语句的查询时间对比：

     - 没添加索引时：

       查询时间：12-16 ms

     - 添加索引后

       查询时间：7 ms



#### 2 接口优化

优化分类目录

1. 将数据库多次查询变为1次



#### 3 Nginx 动静分离

1. 静态资源放在nginx里面，静态资源直接由nginx返回。
2. 这样页面的静态资源由 nginx 返回，数据由 tomcat 返回。



#### 4 缓存

将部分数据放入缓存，db只承担数据持久化工作（数据保存到硬盘）



## 缓存&分布式锁

哪些数据适合放在缓存里？

1. 即时性、数据一致性不高的
2. 访问量大且更新频率不高的数据

### 本地缓存

使用 Map 将数据保存在jvm内存里。

缺点：

1. 对于分布式项目，因为nginx有负载均衡，所以每次请求的服务器可能会不一样，那么可能对于没缓存过的服务器，都要查询一次数据库。

   ![image-20220626213738687](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206262137829.png)

2. 数据一致性问题：对于数据修改的请求，如果负载均衡到服务器A，服务器A修改了mysql数据和缓存数据，但是服务器B和C的缓存数据没有修改，这样，请求到B和C中拿到的缓存数据就是不对的。



### 分布式缓存

![image-20220626214213953](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206262142023.png)





|               压测内容               | 压测线程数 | 吞吐量 | 90%响应时间 | 99%响应时间 |
| :----------------------------------: | :--------: | :----: | :---------: | :---------: |
|    三级分类数据获取（写法优化过）    |    100     |  53.6  |    2118     |    9104     |
| 三级分类数据获取（写法优化过+redis） |    100     |  78.6  |    1401     |    7781     |



#### redis 堆外异常的错误

1. Springboot 2.0以后默认使用 Lettuce 作为操作 redis 的客户端，它使用 netty 进行网络通信。
2. netty 默认使用 -Xmx300m 。Letture 的 bug 会导致 netty 堆外内存溢出。

解决办法：

1. 升级 lettuce 客户端
2. 使用 jedis



#### redis缓存问题和解决

1. 缓存穿透

   - 返回null值

2. 缓存雪崩

   - 过期时间不设置成一样的，有一个波动过期时间

3. 缓存击穿

   - 加锁

     > ```java
     > /*
     > * 1.空结果缓存：解决缓存穿透问题
     > * 2.设置过期时间：解决缓存雪崩问题
     > * 3.加锁：解决缓存击穿问题
     > *   a. 在单机时，使用本地锁，如synchronized或者JUC(Lock)
     > *       （1）查询数据库的方法使用 synchronized (this) 或者查询数据库的方法设置为 synchronized: SpringBoot所有组件在容器中都是单例的，
     > * 这样在单台服务器是可以的，但是在分布式时就不行了， 就要使用分布式锁。
     > *   b. 在分布式场景下，必须使用分布式锁。
     > * */
     > ```

     **<u>单机模式</u>**，注意放入缓存的步骤也应该放在锁里，不然会出现，线程A释放锁后，执行放入缓存的操作时，线程B进入锁去读取缓存，结果读取结果返回的比放入快，所以线程B没有读到数据，则会去查数据库，但是查询了两次数据库：

     ```java
         //  springboot 2.0以后默认使用Letture作为操作redis的客户端，它使用netty进行网络通信。
         @Override
         public Map<String, List<CatelogVO>> getCatalogJson() {
             /*
             * 1.空结果缓存：解决缓存穿透问题
             * 2.设置过期时间：解决缓存雪崩问题
             * 3.加锁：解决缓存击穿问题
             *   a. 在单机时，使用本地锁，如synchronized或者JUC(Lock)
             *       （1）查询数据库的方法使用 synchronized (this) 或者查询数据库的方法设置为 synchronized: SpringBoot所有组件在容器中都是单例的，
             * 这样在单台服务器是可以的，但是在分布式时就不行了， 就要使用分布式锁。
             *   b. 在分布式场景下，必须使用分布式锁。
             * */
             // 1.查询缓存
             String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
             // 2.加入缓存
             if (StringUtils.isEmpty(catalogJSON)) {
                 Map<String, List<CatelogVO>> catalogJsonFromDb = getCatalogJsonFromDb();
     
             }
     
             // 3. 反序列化
             return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<CatelogVO>>>() {});
         }
     
         public Map<String, List<CatelogVO>> getCatalogJsonFromDb() {
             synchronized (this) {
                 // 1.查询缓存
                 String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
                 if (!StringUtils.isEmpty(catalogJSON)) {
                     return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<CatelogVO>>>() {});
                 }
     
                 // 2. 查询数据库
     			DO();
                 // 2.1 序列化
                 String catalogString = JSON.toJSONString(catalogJsonFromDb);
                 redisTemplate.opsForValue().set("catalogJSON", catalogString);
         }
     ```



#### redis分布式锁

==核心：加锁保证原子性，解锁也保证原子性==

初级代码：

```java
// redis 抢占分布式锁
Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1");
if (Boolean.TRUE.equals(lock)) {
    // 加锁成功
    Map<String, List<CatelogVO>> dataFromDb = getDataFromDb();
    redisTemplate.delete("lock"); // 删除锁
    return dataFromDb;
} else {
    // 加锁失败..重试
    return getCatalogJsonFromDb();
}
```

1. 锁加上过期时间

![image-20220628215729190](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206282157266.png)

```java
// 1.redis 抢占分布式锁
Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1");
if (Boolean.TRUE.equals(lock)) {
    // 加锁成功
    // 2.设置锁的过期时间
    redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
    Map<String, List<CatelogVO>> dataFromDb = getDataFromDb();
    redisTemplate.delete("lock"); // 删除锁
    return dataFromDb;
} else {
    // 加锁失败..重试
    return getCatalogJsonFromDb();
}
```



2. 占锁的同时，去设置过期时间。（即加锁和设置过期时间为原子的）

![image-20220628215745905](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206282157976.png)

```java
// 1.redis 抢占分布式锁并设置锁的过期时间
Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1", 30,TimeUnit.SECONDS);
if (Boolean.TRUE.equals(lock)) {
    // 加锁成功
    Map<String, List<CatelogVO>> dataFromDb = getDataFromDb();
    redisTemplate.delete("lock"); // 删除锁
    return dataFromDb;
} else {
    // 加锁失败..重试
    return getCatalogJsonFromDb();
}
```



3. 假如线程A执行业务过程中，锁过期了，线程B抢占了锁，然后执行任务；线程A删除了锁，又有线程C抢占了锁，执行任务........
   - 解决办法：指定uuid

![image-20220628215755606](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206282157673.png)

```java
// 1.指定uuid，每个线程只删除自己的锁
String uuid = UUID.randomUUID().toString();
// 2.redis 抢占分布式锁并设置锁的过期时间
Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock",uuid,30,TimeUnit.SECONDS);
if (Boolean.TRUE.equals(lock)) {
    // 加锁成功
    Map<String, List<CatelogVO>> dataFromDb = getDataFromDb();
    String curUuid = redisTemplate.opsForValue().get("lock");
    if (uuid.equals(curUuid)) {
        redisTemplate.delete("lock"); // 删除锁
    }
    return dataFromDb;
} else {
    // 加锁失败..重试
    return getCatalogJsonFromDb();
}
```



4. 删除锁的时候，锁过期了，导致删除了别人的锁。
   - 解决办法：删除锁必须保证原子性，使用redis+Lua脚本

![image-20220628215807061](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206282158133.png)





5. 最终形态![image-20220628215814231](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206282158294.png)

```java
    public Map<String, List<CatelogVO>> getCatalogJsonFromDb() {
        // 1.指定uuid，每个线程只删除自己的锁
        String uuid = UUID.randomUUID().toString();
        // 2.redis 抢占分布式锁并设置锁的过期时间
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)) {
            // 加锁成功
            Map<String, List<CatelogVO>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                // Lua 脚本
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 删除锁
                redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            
            return dataFromDb;
        } else {
            // 加锁失败..重试
            return getCatalogJsonFromDb();
        }
    }
```



#### Redisson

##### Redisson看门狗机制

1. 设置了锁的自动续期。如果业务时间长，看门狗会自动续30s的过期时间。不用担心业务时间长，导致锁被自动删除。
2. 只要加锁业务运行完成，就不会给当前锁自动续期，锁默认30s后自动删除，这样就不会出现死锁的问题。

原理：

```java
// 加锁
myLock.lock();      //阻塞式等待。默认加的锁都是30s
// myLock.lock(10,TimeUnit.SECONDS);   //10秒钟自动解锁,自动解锁时间一定要大于业务执行时间
// 问题：在锁时间到了以后，不会自动续期
// 1、如果我们指定了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是 我们制定的时间
// 2、如果我们没有指定锁的超时时间，就使用 lockWatchdogTimeout = 30 * 1000 【看门狗默认时间】
//    只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10秒都会自动的再次续期，续成30秒（internalLockLeaseTime【看门狗时间】 / 3 = 10s）

// 一般设置一个30s时间，业务30s内肯定能执行结束了。
```



##### 读写锁

- Redisson的读写锁能保证读到的数据一定是最新的。
- 写锁：写锁是排他锁（互斥锁）
  - 写锁未释放，读必须等待。
- 读锁：读锁是共享锁

> 写 + 读：阻塞等待锁释放
>
> 写 + 写：阻塞等待锁释放
>
> 读 + 写：阻塞等待锁释放
>
> 读 + 读：不会阻塞，相当于无锁。并发读，会同时加锁成功。



##### 信号量

信号量为存储在redis中的一个数字，当这个数字大于0时，即可以调用`acquire()`（信号量小于0会阻塞）使信号量 - 1，`tryAcquire()`（信号量小于0不阻塞）使信号量 - 1，也可以调用release()方法使信号量 + 1。

1. 秒杀系统。
2. 分布式限流。

```java
/**
 * 信号量可以做分布式限流
 *
 * @return
 * @throws InterruptedException
 */
@GetMapping("/park")
@ResponseBody
public String park() throws InterruptedException {

    RSemaphore park = redisson.getSemaphore("park");
    park.acquire();//获取一个信号量（redis中信号量值-1）,如果redis中信号量为0了，则在这里阻塞住，直到信号量大于0，可以拿到信号量，才会继续执行。
    //业务代码...
    return "ok" + b;

}

@GetMapping("/go")
@ResponseBody
public String go() throws InterruptedException {
    RSemaphore park = redisson.getSemaphore("park");
    park.release();//释放一个信号量（redis中信号量值+1）
    return "走了";

}
```



##### 闭锁 CountDownLatch

在要完成某些运算时，只有其它线程的运算全部运行完毕，当前运算才继续下去。

```java
@GetMapping("/lockdoor")
@ResponseBody
public String lockDoor() throws InterruptedException {

    RCountDownLatch door = redisson.getCountDownLatch("door");
    door.trySetCount(5);//设置计数为5个
    door.await();//5个都走完了才会继续往下走
    return "都走完了";
}

@GetMapping("/gogogo/{id}")
@ResponseBody
public String gogogo(@PathVariable int id){

    RCountDownLatch door = redisson.getCountDownLatch("door");
    door.countDown();//每执行一次，锁-1（redis中的door去-1）
    return id+"号走了";
}
```



#### 缓存数据一致性

1. 双写模式

   - 更新完数据库，就更新一下缓存

   - 问题：

     ![image-20220630212546868](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206302125952.png)

   

2. 失效模式

   - 更新完数据库，就删掉缓存

   - 问题：

     ![image-20220630212719449](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206302127512.png)

## 解决方案：用 canal 或者 debezium ，做一下



## 异步

### 线程池

### 异步编排

1. 查询商品详情页的逻辑非常复杂，数据的获取都需要[远程调用](https://so.csdn.net/so/search?q=远程调用&spm=1001.2101.3001.7020)，必然需要花费更多的时间。

   ```
   // 1. 获取sku的基本信息	0.5s
   
   // 2. 获取sku的图片信息	0.5s
   
   // 3. 获取sku的促销信息 TODO 1s
   
   // 4. 获取spu的所有销售属性	1s
   
   // 5. 获取规格参数组及组下的规格参数 TODO	1.5s
   
   // 6. spu详情 TODO	1s
   
   ```

```java
    /*
    * @Description: 异步编排优化
    * @Param: [skuId]
    * @return: com.finn.gulimall.product.vo.SkuItemVO
    * @Author: Finn
    * @Date: 2022/07/06 21:17
    */
    @Override
    public SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException {

        SkuItemVO skuItemVO = new SkuItemVO();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // 1. sku基本信息的获取 pms_sku_info
            SkuInfoEntity info = this.getById(skuId);
            skuItemVO.setInfo(info);
            return info;
        }, executor);

        // infoFuture完成后就开始
        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            // 3. spu销售属性组合
            skuItemVO.setSaleAttr(skuSaleAttrValueService.getSaleAttrBySpuId(res.getSpuId()));
        }, executor);

        // infoFuture完成后就开始
        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            // 4. spu的介绍
            skuItemVO.setDesc(spuInfoDescService.getById(res.getSpuId()));
        }, executor);

        // infoFuture完成后就开始
        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            // 5. spu的规格参数信息
            skuItemVO.setGroupAttrs(attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(),
                    res.getCatalogId()));
        }, executor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            // 2. sku的图片信息 pms_sku_imgs
            List<SkuImagesEntity> images = skuImagesService.getImageBySkuId(skuId);
            skuItemVO.setImages(images);
        }, executor);

        // 等待所有任务都完成
        CompletableFuture.allOf(saleAttrFuture, descFuture, baseAttrFuture, imageFuture).get();

        return skuItemVO;
    }
```



2. 添加购物车时候，要远程调用gulimall-product模块，拿到商品sku信息，同时要拿到商品的sku的销售属性信息

```java
//开启第一个异步任务
CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
    //1、远程查询当前要添加商品的信息
    R productSkuInfo = productFeignService.getInfo(skuId);
    SkuInfoVO skuInfo = productSkuInfo.getData("skuInfo", new TypeReference<SkuInfoVO>() {});
    //数据赋值操作
    cartItemVo.setSkuId(skuInfo.getSkuId());
    cartItemVo.setTitle(skuInfo.getSkuTitle());
    cartItemVo.setImage(skuInfo.getSkuDefaultImg());
    cartItemVo.setPrice(skuInfo.getPrice());
    cartItemVo.setCount(num);
}, executor);

//开启第二个异步任务
CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
    //2、远程查询skuAttrValues组合信息
    List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
    cartItemVo.setSkuAttrValues(skuSaleAttrValues);
}, executor);

//等待所有的异步任务全部完成
CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();
```



## 登录功能

登录时，用session保存用户信息

### 登录流程

1. gulimall-auth-server模块把账号密码发送给gulimall-member模块，gulimall-member模块去数据库查询用户信息，比对账号和密码。
   - 比对成功，返回用户详细信息
   - 比对失败，返回null

2. 拿到用户详细信息后，gulimall-auth-server模块将用户详细信息data保存到key为userLogin的session中

### session

session是保存在服务器端的

![image-20220709194201464](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091942624.png)

### session的共享问题

![image-20220709194447813](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091944862.png)

1. 不同域名下的cookie不能共享

   ![image-20220709194603861](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091946919.png)

2. 不同服务的session不能共享

3. 同一服务的不同服务器上的session不能共享



##### 对于2、3的解决方案

**方案一** 

![image-20220709194905189](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091949270.png)

**方案二** 

![image-20220709194958394](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091949477.png)

**解决方案一**

![image-20220709195159256](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091951335.png)



**解决方案二**

![image-20220709195354668](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091953748.png)



##### 对于1的解决方案

![image-20220709202020888](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207092020952.png)



### SpringSession核心原理



![image-20220709205241798](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207092052888.png)

==装饰者模式==

> 动态的将新功能附加到对象上。在对象功能扩展方面，它比继承更有弹性，装饰者模式也体现了开闭原则。
>
> 通常可以使用继承来实现功能的扩展，如果这些需要扩展的功能的种类很繁多，那么势必生成很多子类，增加系统的复杂性，同时使用继承实现功能拓展，我们必须可预见这些拓展功能，这些功能是编译时就确定了，是静态的。
>
> 装饰者与被装饰者拥有共同的超类，**继承的目的是继承类型，而不是行为**
>
> - 较差方案，继承了太多类
>
> ![image-20220709221204649](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207092212733.png)
>
> - 使用装饰者模式
>
>   ![image-20220709221239914](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207092212987.png)
>
>   装饰器decorator的构造器中拿到Drink的对象，然后根据添加的内容（choolate、milk、soy）去动态地增加price和desc。换句话说，choolate、milk、soy对drink的功能进行了拓展，但却没有直接继承Drink

SpringSession用**装饰者模式**包装成了原生的request和response：

1. 包装原生Request

   SessionRepositoryFilter<S.>.SessionRepositoryRequestWrapper wrappedRequest = new SessionRepositoryFilter.SessionRepositoryRequestWrapper(request, response, this.servletContext);

2. 包装原生Response

   SessionRepositoryFilter.SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryFilter.SessionRepositoryResponseWrapper(wrappedRequest, response);

3. 包装后的对象放到filterChain，执行filterChain.doFilter(wrappedRequest, wrappedResponse);

原生的request和response都被SpringSession用**装饰者模式**包装成了SessionRepositoryRequestWrapper、SessionRepositoryResponseWrapper，因此以后调用`request.getSession()`，就会去调用`wrappedRequest.getSession()`



### 单点登录 SSO

![image-20220710134527376](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207101345613.png)

希望一处登录，处处登录

![image-20220710135048376](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207101350463.png)





## 购物车

![image-20220710135329688](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207101353763.png)

最终数据结构：gulimall:cart:userId — 商品id — sku数据

### 购物车功能

1. 未登录添加到临时购物车
2. 登录后，将临时购物车的东西加入到购物车内



```java
	/* 
    * 1. 浏览器有个cookie：user-key：标识用户身份，该cookie一个月后过期
    *    如果第一次使用购物车，会给一个临时身份，浏览器保存该cookie，每次访问都会带上
    *
    * 使用拦截器完成功能：
    *   登录：使用session
    *   未登录：使用key为user-key的cookie
    *   第一次登录，创建临时用户
    */
```

使用**拦截器**完成



### 拦截器

#### 业务执行之前

```java
    public static ThreadLocal<UserInfoTO> threadLocal = new ThreadLocal<>();

    /*
    * 在目标方法之前拦截
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTO userInfoTO = new UserInfoTO();
        HttpSession session = request.getSession();
        // session保存的是用户详细信息MemberResponseVO
        MemberResponseVO member = (MemberResponseVO) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (!Objects.isNull(member)) {
            // session里有用户信息，用户已经登录
            userInfoTO.setUserId(member.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (!Objects.isNull(cookies) && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                // 如果有临时身份（key为user-key的cookie）x
                if (CartConstant.TEMP_USER_COOKIE_NAME.equals(name)) {
                    // 保存user-key的cookie的value
                    userInfoTO.setUserKey(cookie.getValue());
                    // 标记为已是临时用户
                    userInfoTO.setTempUser(true);
                }
            }
        }

        //如果没有临时用户一定分配一个临时用户
        if (StringUtils.isEmpty(userInfoTO.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTO.setUserKey(uuid);
        }

        // 将信息保存在ThreadLocal里
        threadLocal.set(userInfoTO);

        return true;
    }
```



#### 业务执行之后

分配临时用户，让浏览器保存

```java
    /*
    * @Description: 业务执行之后的拦截器
    * @Param: [request, response, handler, modelAndView]
    * @return: void
    * @Author: Finn
    * @Date: 2022/07/10 14:55
    */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        // 获取当前用户的值
        UserInfoTO userInfoTO = threadLocal.get();

        // 如果没有临时用户身份，就保存一个临时用户身份
        if (!userInfoTO.getTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTO.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
```



### ThreadLocal

![image-20220710144713621](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207101447691.png)

一个线程对应一个请求















































