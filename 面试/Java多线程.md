# Java多线程

## 线程安全的实现方式

![image-20220313113006714](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203131130783.png)



### ThreadLocal

![image-20220313120733643](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202203131207691.png)

#### 定义

线程变量。线程私有的本地变量，它对其他线程是隔离的，其他线程不能访问到该线程的线程本地变量。

线程会维护一个ThreadLocalMap结构，key是ThreadLocalHashCode，即一个ThreadLocal对象，value是本地变量的值，而访问入口就是这个ThreadLocal对象。

#### 使用场景

数据库连接管理。一个线程维护一个连接，线程会保存连接副本，该连接副本就是线程的一个本地变量，其他线程不可以访问该连接副本。

#### 可能出现的问题

内存泄漏问题。线程池中得线程在使用完后，回归到线程池，因为该线程没有被回收掉，所以他引用的ThreadLocal对象也不会被回收掉。这种情况下，如果没有把该线程的ThreadLocal对象remove()掉，那该ThreadLocal对象会一直存在，占用着内存。

解决：线程池的线程任务结束前，手动remove掉ThreadLocal对象。

