# Redis

![img](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203232139349.jpeg)

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

    ![image-20220323234232474](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203232342526.png)

  - 避免缓冲区溢出：String进行修改时，Redis会检查剩余空间是否满足要求，不满足的话会扩展到足够的空间来保存String。

  - 空间预分配：String扩容时，Redis会给予足够多的内存空间。

  - 惰性释放：字符串需要缩短时，所需空间不会直接缩短，而是用free保存剩余空间，以便再次分配。

  - String长度不能超过512m



#### Hash

- 使用场景：保存一个对象，一个对象有多个属性。
  - 我项目中用来保存用户分布地域情况
  
- 底层原理：

  - 当Hash中键值对少于512对，且每个键值对大小不超过64字节时，用ziplist保存

    ziplist:

    - 内存中连续的存储空间

    - 元素用entry表示

      ![img](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241432093.png)

      zlbytes: ziplist长度

      zltail：ziplist尾部偏移量

      zllen：entry个数

      entry：元素

      zlend：0xFF，标识结尾

      

  - 否则用HashTable保存。

    HashTable：

    - 一个dictht指向一个数组
    - 每个数组上的键值对用链表形式保存

    ![image-20220324135352680](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241353787.png)


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

- 使用场景：可以用来实现分页查询，或者读取用户评论数

- 底层原理：
  
  **3.0以前**：用ziplist + linkedlist保存
  
  - 当列表长度小于512，且所有元素都小于64字节时，用ziplist保存
  
  - 否则，用LinkedList保存
  
    LinkedList保存了头节点head，尾节点tail，列表长度
  
    LinkedList：
  
    ![image-20220324142217267](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241422356.png)
  
    **3.0以后**：用**<u>quicklist</u>**保存。这样可以节省双向列表保存pre和next指针的空间。
  
    - quicklist是ziplist和linkedlist的组合
  
      ![img](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241438435.png)



#### Set

- 使用场景：Set可以做并集、交集、差集。可以用来查看好友的共同列表。

- 底层原理：

  - set元素少于512个，用intset来存储

    ![image-20220324144301337](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241443378.png)

  - 否则，存储类似hashtable，只是value赋null



#### Zset

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
  
    ![img](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241443204.jpeg)
  
    



### 高级数据类型

#### Bitmap

按bit位存储。

#### HyperLogLog

基数统计。

#### Geospatial

提供地理位置信息。

#### pub/sub

订阅发布通信模式。

![img](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203232302569.png)

- client1，client2，client5都订阅了channel1。

![img](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203232302723.png)

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

![image-20220322215820222](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203222158314.png)

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

![image-20220324154528732](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241545779.png)

#### 特点

- **写读分离**：主服务器写，从服务器读，从服务器不能写

- **从服务器挂掉**：

  - master服务器下的一个 slave1 服务器 s1 挂掉，s1 重启后，会自动变成一个 master 服务器。

    把挂掉的服务器重新赋成master服务器的slave，slave仍然能读到master中的数据。

- **主服务器挂掉**：

  - master服务器挂掉后，它的slave服务器仍然是它的slave服务器。

    master重启后，仍然是原来的master。



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

![image-20220324163534966](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241635024.png)



1. slave开启主从复制：设置主服务器master的ip和port。主从复制开启全是由从服务器slave发起。

   有三种实现方式：

   1. conf文件中写入：`slaveof master_ip master _port`
   2. `redis-server --slaveof master_ip master_port`
   3. `redis-cli`后，输入`slaveof master_ip master _port`

2. 建立连接：开启主从复制后，master 和 slave 之间建立 socket 连接。

3. 检查连接：slave 向 master 发送一个 ping 请求，来检查两者的连接状态。

   - 如果 slave 收到 pong，证明连接正常。
   - 如果 slave 没有收到 pong，或者收到错误信息，则 m 和 s 断开socket连接，并重连。

   ![image-20220324211458486](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203242114536.png)

4.  身份验证：如果 master 和 slave 都没有设置密码或者密码相同，则可以进行同步。否则，断开socket，并且重连。

   ![image-20220324211716468](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203242117544.png)

5. 同步：连接正常并且完成身份验证后，slave 向 master 发送 `psync` 命令，然后 master 和 slave 之间同步数据。slave 把数据库状态同步到和 master 相同。

6. 命令传播：同步完成后，master 进行的新操作都要传播给 slave。

   - 延迟传播：master 积攒多个 tcp 包后，再发送给 slave，通常是40ms发一次。提高了性能，损失了一致性。
   - 立即传播：master 每进行一次操作，立即把新操作发送给 slave。保证了一致性，降低了性能。

   以上通过 master 配置中的 repl-disable-tcp-nodelay 来设置。yes为延迟传播，no为立即传播。



### 心跳检测





### 全量复制和部分复制

#### 全量复制

![image-20220324225346762](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203242253806.png)

1. slave 给 master 发送 sync 命令，请求全量复制
2. master 将执行 bgsave，将写操作写成 RDB 文件，并且使用一个缓存，缓存放入写完 RDB 之后的新操作。
3. master 给 slave 发送 RDB 文件，slave 收到以后，删除旧数据，执行 RDB，与 master 进行同步。
4. master 给 slave 发送缓存区的写操作，slave 执行并保持和 master 同步的状态。
5. 如果 slave 开启了 AOF，则会执行 bgrewriteaof，更新AOF至最新的状态。



#### 部分复制

Redis 2.8 以后，引入部分复制。slave 给 master 发送 psync 命令。然后决定是使用全量复制还是部分复制。

- **offset**

- **复制挤压缓冲区**

  ![image-20220324230423385](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203242304435.png)

- **runid**

  





### 集群

![image-20220324154834805](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203241548857.png)



