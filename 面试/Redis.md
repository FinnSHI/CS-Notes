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

#### List

有序列表

- 使用场景：可以用来实现分页查询，或者读取用户评论数

#### Hash

- 使用场景：保存一个对象，一个对象有多个属性。
  - 我项目中用来保存用户分布地域情况
- 底层原理：
  - 

#### Set

- 使用场景：Set可以做并集、交集、差集。可以用来查看好友的共同列表。
- 底层原理：
  - 

#### Zset

- 使用场景：
  - 做排行榜
    - 按照浏览量
    - 按照视频播放量
    - 按照点赞量
  - 微博热搜榜，名称+热力值

- 底层原理：
  - 



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

