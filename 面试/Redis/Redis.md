# Redis

![202203232139349](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252130280.jpeg)

## 缓存

1. **本地缓存**：缓存在内存中。比如LRUMap
   - 优点：访问快
   - 缺点：内存空间有限，缓存内容少
2. **分布式缓存**：
   - 优点：解决了内存空间有限的问题
   - 缺点：远程交互耗时
3. **多级缓存**：本地只保存需要高频率访问的热点数据，其他数据保存在分布式缓存中。



## 缓存淘汰机制

- FIFO: First-In-First-Out，删除最先缓存的。

- LRU: Least-Recently-Used，最近最少使用。删除掉最近不太可能访问到的数据。如果缓存中有些数据最近都没有被访问过，那就认为它之后被访问的概率低。

  - 用LinkedHashMap实现

  - 如果自己写节点的话，

    LinkedNode

    - int key;
    - int value;
    - LinkedNode pre;
    - LinkedNode next;

- LFU: Least-Frequency-Used：最不经常使用。删除掉最近访问频率很低的数据。如果缓存中有数据最近被访问的次数很少，那就认为它以后被访问的概率也低。



## Redis数据类型

### 基本数据类型

#### String

- 常用指令：
  - SET key value
  - GET key
  - INCR key：key保存的值+1
  - DECR key：key保存的值-1

- 使用场景：

  - 做用户的 Session 管理。
  - 博客访问量情况

- 底层原理：

  - SDS（Simple Dynamic String）

    ```c
    struct sdshdr{
    	int len; // 字符串长度
    	int free; // 未使用字符数组长度
    	char[] buf; // 字符数组
    }
    ```

    ![202203232342526](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252130628.png)

  - 避免缓冲区溢出：String进行修改时，Redis会检查剩余空间是否满足要求，不满足的话会扩展到足够的空间来保存String。

  - 空间预分配：String扩容时，Redis会给予足够多的内存空间。

  - 惰性释放：字符串需要缩短时，所需空间不会直接缩短，而是用free保存剩余空间，以便再次分配。

  - String长度不能超过512m



#### Hash

- 常用指令：
  
  - HSET key field value
  
  - HGET key field
  
  - HDEL key field
  
  - HEXISTS key field
  
  - HKEYS key
  
- 使用场景：保存一个对象，一个对象有多个属性。

  - 我项目中用来保存用户分布地域情况

- 底层原理：

  - 当Hash中键值对少于512对，且每个键值对大小不超过64字节时，用ziplist保存

    ziplist:

    - 内存中连续的存储空间

    - 元素用entry表示

      ![202203241432093](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252130138.png)

      zlbytes: ziplist长度

      zltail：ziplist尾部偏移量

      zllen：entry个数

      entry：元素

      zlend：0xFF，标识结尾

      

  - 否则用HashTable保存。

    HashTable：

    - 一个dictht指向一个数组
    - 每个数组上的键值对用链表形式保存

    ![202203241353787](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252131617.png)


- 扩容
  - 基于原Hash表的2倍创建一个新的哈希表，然后把旧哈希表的内容转移到新哈希表里。
  - 渐进式rehash：
    - 旧哈希表的内容不会一下子转移到新哈希表，而是渐进式转移过去。用一个rehashidx字段来记录，rehashidx为0表示开始迁移。
    - 当所有数据都从旧哈希表里转移过去后，释放旧哈希表的空间。rehashidx置为-1。
- 收缩
  - 类似于扩容，但是收缩成原哈希表1/2倍的空间。

- 负载因子
  - 当redis没有执行持久化操作（BGSAVE or BGREWAITEAOF），负载因子是1
  - 当redis执行持久化操作（BGSAVE or BGREWAITEAOF），负载因子是5



#### List

有序列表

- 常用指令
  - LPUSH
  - RPUSH
  - LPOP
  - RPOP
  - LRANGE key start stop

- 使用场景：可以用来实现分页查询，或者读取用户评论数

- 底层原理：
  
  **3.0以前**：用ziplist + linkedlist保存
  
  - 当列表长度小于512，且所有元素都小于64字节时，用ziplist保存
  
  - 否则，用LinkedList保存
  
    LinkedList保存了头节点head，尾节点tail，列表长度
  
    LinkedList：
  
    ![202203241422356](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252131480.png)
  
    **3.0以后**：用**<u>quicklist</u>**保存。这样可以节省双向列表保存pre和next指针的空间。
  
    - quicklist是ziplist和linkedlist的组合
  
      ![202203241438435](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252131983.png)



#### Set

- 常用指令：

  - SADD：添加

  - SCARD：获取集合成员数

- 使用场景：Set可以做并集、交集、差集。可以用来查看好友的共同列表。

- 底层原理：

  - set元素少于512个，用intset来存储

    ![202203241443378](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252131570.png)

  - 否则，存储类似hashtable，只是value赋null



#### Zset

- 常用指令：
  - ZADD key score member
  - ZCARD key：获取成员数
  - ZCOUNT key min max: 获取指定分数区间（min到max）的成员数量

- 使用场景：
  - 做排行榜
    - 按照浏览量
    - 按照视频播放量
    - 按照点赞量
  - 微博热搜榜，名称+热力值

- 底层原理：
  - 当zset长度小于128，且每个元素大小小于64k，用ziplist
  
  - 否则，用跳跃表
  
    跳表：时间复杂度为**O(logN)**
  
    ![202203241443204](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252131804.jpeg)
  
    



### 高级数据类型

#### Bitmap

按bit位存储。

#### HyperLogLog

基数统计。

#### Geospatial

提供地理位置信息。

#### pub/sub

订阅发布通信模式。

![202203232302569](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252132737.png)

- client1，client2，client5都订阅了channel1。

![202203232302723](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252132071.png)

- channel1广播message，订阅了它的client都能收到。

#### Pipeline

可以批量执行一系列操作，再一起返回结果。避免频繁请求响应。

#### Lua

redis可以使用Lua解释器来执行脚本。

#### 事务

1. multi开启事务
2. 操作入队
3. exec按顺序执行事务

- 入队前的错误会使事务失败
- 入队后，如果发生错误，只有发生错误的事务会失败，其他事务仍然成功



## Redis数据持久化

### RDB

RDB是Redis Database，默认开启

#### Redis生成数据快照

##### 方式

Redis会fork一个子进程进行持久化，然后先将数据写入一个临时文件中，等数据全部写完后，再把临时文件替换掉上次持久化好的文件。

- 缺点：
  - 最后的数据可能会丢失。比如设置的是20秒内有至少4次操作则进行持久化
    - 第一次20秒内进行了5次操作，进行了一次持久化
    - 第二次20秒内只进行了1次操作，然后服务挂掉了，那这次操作就丢失了

![202203222158314](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252132463.png)

##### Fork

Fork是指复制一个和当前进程一样的子进程。新进程所有的数据（变量，环境变量，程序计数器等）和原来进程一样。一般来说，父进程和子进程共用一段物理内存。

##### 写时复制技术

属于Linux的一种技术，即数据同步之前先创建临时文件，将数据保存进临时文件，等同步完成后，再将临时文件替换掉同步的数据内容。

- 为什么要写时复制技术？
  - 如果不采用临时复制技术，在同步过程中，发生程序中断，那么同步的数据就不完整。



### AOF

Append only file，默认关闭。AOF以日志形式保存Redis每个写操作（不保存读操作）。并且只追加文件，不修改文件

1. 客户端的请求写命令会被append到AOF缓冲区。

2. AOF缓冲区会根据持久层策略（always, everysec, no）将写操作保存到磁盘的AOF文件。这里只追加文件，不修改文件。
3. 在Redis服务启动时，会读取这个AOF文件，并且执行上面的写操作。
4. 当文件大小大于server.aof_rewrite_min_size，并且当前AOF文件大小和上一次重写时AOF文件大小差的比例达到auto-aof-rewrite-percentage时，则重写文件。



> RDB和AOF同时开启，Redis选择哪一个？
>
> - Redis默认选取AOF数据



## 主从复制

### 一主多从

![202203241545779](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252132896.png)

#### 特点

- **写读分离**：主服务器写，从服务器读，从服务器不能写

- **从服务器挂掉**：

  - master服务器下的一个 slave1 服务器 s1 挂掉，s1 重启后，会自动变成一个 master 服务器。

    把挂掉的服务器重新赋成 master 服务器的 slave，slave 仍然能读到 master 中的数据。

- **主服务器挂掉**：

  - master 服务器挂掉后，它的 slave 服务器仍然是它的 slave 服务器。

    master 重启后，仍然是原来的 master。



### 主从复制原理

> CAP原理：
>
> - C - Consistent ，**一致性**
> - A - Availability ，**可用性**
> - P - Partition tolerance ，**分区容忍性**
>   分布式系统的节点往往都是分布在不同的机器上进行网络隔离开的，这意味着必然会有网络断开的风险，这个网络断开的场景的专业词汇叫着「**网络分区**」。
>
> 在网络分区发生时，两个分布式节点之间无法进行通信，我们对一个节点进行的修改操作将无法同步到另外一个节点，所以数据的「**一致性**」将无法满足，因为两个分布式节点的数据不再保持一致。除非我们牺牲「**可用性**」，也就是暂停分布式节点服务，在网络分区发生时，不再提供修改数据的功能，直到网络状况完全恢复正常再继续对外提供服务。
>
> 一句话概括 CAP 原理就是——网络分区发生时，一致性和可用性两难全。



#### 主从复制原理

![202203241635024](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252132949.png)



1. slave开启主从复制：设置主服务器master的ip和port。主从复制开启全是由从服务器slave发起。

   有三种实现方式：

   1. conf文件中写入：`slaveof master_ip master _port`
   2. `redis-server --slaveof master_ip master_port`
   3. `redis-cli`后，输入`slaveof master_ip master _port`

2. 建立连接：开启主从复制后，master 和 slave 之间建立 socket 连接。

3. 检查连接：slave 向 master 发送一个 ping 请求，来检查两者的连接状态。

   - 如果 slave 收到 pong，证明连接正常。
   - 如果 slave 没有收到 pong，或者收到错误信息，则 m 和 s 断开socket连接，并重连。

   ![202203242114536](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252132566.png)

4.  身份验证：如果 master 和 slave 都没有设置密码或者密码相同，则可以进行同步。否则，断开socket，并且重连。

   ![202203242117544](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252133760.png)

5. 同步：连接正常并且完成身份验证后，slave 向 master 发送 `psync` 命令，然后 master 和 slave 之间同步数据。slave 把数据库状态同步到和 master 相同。

6. 命令传播：同步完成后，master 进行的新操作都要传播给 slave。

   - 延迟传播：master 积攒多个 tcp 包后，再发送给 slave，通常是40ms发一次。提高了性能，损失了一致性。
   - 立即传播：master 每进行一次操作，立即把新操作发送给 slave。保证了一致性，降低了性能。

   以上通过 master 配置中的 repl-disable-tcp-nodelay 来设置。yes为延迟传播，no为立即传播。



### 心跳检测





### 全量复制和部分复制

#### 全量复制

![202203242253806](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252133702.png)

1. slave 给 master 发送 sync 命令，请求全量复制
2. master 将执行 bgsave，将写操作写成 RDB 文件，并且使用一个缓存，缓存放入写完 RDB 之后的新操作。
3. master 给 slave 发送 RDB 文件，slave 收到以后，删除旧数据，执行 RDB，与 master 进行同步。
4. master 给 slave 发送缓存区的写操作，slave 执行并保持和 master 同步的状态。
5. 如果 slave 开启了 AOF，则会执行 bgrewriteaof，更新AOF至最新的状态。



#### 部分复制

Redis 2.8 以后，引入部分复制。slave 给 master 发送 psync 命令。然后决定是使用全量复制还是部分复制。

- **offset**

  - master 和 slave 都维护了一个 offset 字段，保存的是现在数据的偏移量。如果 slave 的 offset 和 master 的 offset 一致，则不需要进行数据复制。如果 slave 的 offset 和 master 的 offset 不一致，则需要进行数据同步。

- **复制挤压缓冲区**

  - master 维护了一个复制积压缓冲区，这个复制积压缓冲区是一个先进先出的队列。master 在进行命令传播的时候，会把新的写操作写入这个复制挤压缓冲区。旧的写操作会在对头弹出。当 slave 要求进行部分复制的时候，如果需要复制的内容存在于复制积压缓冲区，那么 master 会进行部分复制，如果要复制的内容已经不在复制积压缓冲区，或者已经不完整，那么 master 会进行全量复制。

  ![202203242304435](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252133181.png)

- **runid**

  - master 和 slave 会各自维护一个runid，来决定是进行全量复制还是部分复制。slave 断线重连后，会将自己的 runid 和master 的 runid 进行比较，如果两者的 runid 一致，说明现在的 master 是 slave 断线重连前的 master，则可以进行部分复制。如果 runid 不一致，说明 slave 断线之前连接的 master 和现在的 master 不是一个，那应该进行全量复制。



**部分复制过程**：

![202203251542968](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203252133096.png)

如果 master 返回 -err，说明 master 版本是2.8之前，无法识别 psync 命令。



### 哨兵

![image-20220325182202354](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203251822438.png)



## 缓存穿透

当缓存和数据库里都没有一个数据时，用户恶意请求该数据，导致 redis 频繁请求数据库，给数据库造成极大的压力。比如，产品id不可能为-1，但用户频繁请求产品id为-1的数据，导致缓存穿透问题。

解决办法：

1. 业务层增加filter接口，对请求进行合法性检查，过滤不合法的请求，比如请求id为-1的数据。

2. 缓存中设置一个 null 值，并设置较小的过期时间。当请求没有的数据时，就返回空。当数据库存入该数据后，及时更新缓存。

3. 布隆过滤器

   - bitmaps

     ![img](https://img-blog.csdnimg.cn/img_convert/8b083d79d53cd748f50b1d322b14cbb0.png)


		1. 对于一个数key，通过多个hash函数，算出多个值，每个值都在布隆过滤器对应的位置上置1。
  		2. 当进来一个数，通过多个hash计算，去找对应位置上的值，如果该位置上有0，则该数一定不存在
       - 如果位置上都是1，该数==<u>不一定</u>==存在。

​	优点：

​		1. 能说明一个数一定不存在

​	缺点：

​		1. 不能说明一个数一定存在

​		2. 不能删除数据。

​		3. 数据量大的时候，会出现误判



## 缓存击穿

数据库中有数据，缓存中的该数据过期了。大量用户请求该数据，导致redis频繁读取数据库，给数据库造成极大的压力，甚至崩溃。

解决办法：

1. 给热点数据设置成永不过期。
2. 对于<u>缓存中没有的数据，如果收到大量请求，则进行加锁</u>，只放一条请求进来，然后去读取数据库，并加载到内存中。接着，再放开这个锁。其他请求需要等待解锁，并且要设置一个阈值，如果等待时间过长，则直接返回空。



## 缓存雪崩

数据库里有数据，而缓存中正好有大量的数据过期。这时，大量用户请求这些数据，导致redis频繁读取数据库，给数据库造成极大的压力，甚至奔溃。

解决办法：

1. 给热点数据设置成永不过期。
2. 可以考虑给缓存的时间设置波动过期值，避免大量数据一起过期。
3. 也可以考虑使用双缓存模式，A缓存中热点数据永不过期，B缓存中热点数据可以过期，当数据过期后，去A缓冲中读取数据。









