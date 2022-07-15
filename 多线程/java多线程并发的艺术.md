# Java多线程并发

## 一 并发编程的挑战

### 1.1 上下文切换

上下文切换：任务从保存到再加载的过程。

> CPU通过时间片分配算法来循环执行任务，当前任务执行一个时间片后会切换到下一个 任务。但是，在切换前会保存上一个任务的状态，以便下次切换回这个任务时，可以再加载这个任务的状态。

#### 多线程一定会快吗？

- 不一定。因为任务创建和上下文切换，并发执行次数少的时候，执行速度会比串行慢。

#### 如何减少上下文切换？

1. 无锁并发编程

   - 多线程处理数据时，可以用一些办法来避免使用锁。

     - 将数据的ID按照Hash算法取模分段，不同的线程处理不同段的数据。

     - （实习项目）不同线程去读取kafka不同topic里的数据，再去做相应的操作。

2. CAS算法

3. 使用最少线程

   - 避免创建不需要的线程，导致大量线程处于等待状态。

4. 协程

   - 在单线程中实现多任务的调度，并在单线程中维持多个任务的切换。

#### 如何避免死锁？

1. 避免一个线程同时获得多个锁。
2. 避免一个线程在锁内同时占用多个资源，尽量保证每个锁只占用一个资源。
3. 尝试使用定时锁， 使用`lock.tryLock(timeout)`来代替内部锁。
4. 对于数据库锁，加锁和解锁必须在一个数据库连接里，否则会出现锁失败的情况。





## 二 Java并发机制底层原理

java程序编译成字节码，字节码被类加载器加载到JVM里执行，最终变为CPU可运行的汇编指令，所以java并发与JVM实现和CPU指令息息相关。

### 2.1 volatile

- volatile是轻量级的synchronized，它不会引起上下文的切换和调度。
- 特性：
  - volatile修饰符可以保证变量的<u>**==可见性==**</u>。
    - 如果一个字段被声明成volatile，Java线程内存模型确保所有线程看到这个变量的值是一致的。

  - 任意<u>单个</u>volatile修饰的变量的读/写具有个==<u>**原子性**</u>==。
    - <u>volatile++这种符合操作不具有原子性</u>


#### 2.1.1 定义和实现原理

- cpu术语：

  ![image-20220629134043258](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206291340359.png)



##### volatile如何保证可见性？

1. 处理器不直接和内存通信，而是先将内存数据读到内部缓存，然后进行操作。

2. volatile修饰的变量在进行**写**操作时，JVM会向处理器发送 LOCK 指令，将这个数据所在的缓存行写到系统内存中进行修改。一个处理器的缓存回写到内存里会导致其他处理器的缓存无效。

3. 根据缓存一致性原则来确保修改的原子性。

   > 缓存一致性：处理器会嗅探系统总线上的数据来判断缓存中的数据是否过期，如果过期，则将缓存中的数据设为无效状态。当要对该数据进行修改时，会先去内存中读取数据。



##### volatile读/写的内存语义

1. 当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量值刷新到主内存。
2. 当读一个volatile变量时，JMM会把该线程对应的本地内存置为无效。接下来将从主内存中读取共享变量。

> 线程A写一个volatile变量，随后线程B读这个volatile变量，这个过程实质上是线程A通过主内存向线程B发送消息
>
> <img src="https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207041050178.png" height="650px"/>



##### volatile内存语义的实现

为了实现volatile的内存语义，编译器在生成字节码时，会在指令序列中插入内存屏障来禁止特定类型的处理器重排序。



### 2.2 synchronized 

synchronized实现同步的基础：Java中每个对象都可以作为锁：

- 对于普通同步方法，锁是当前实例对象

  ```java
  public synchronized void add() {...}
  ```

- 对于静态同步方法，锁是当前类的Class对象

  ```java
  public synchronized static void add() {...}
  ```

- 对于同步方法块，锁是Synchronized括号里配置的对象。

  ```java
  synchronized (ObjectA.class) {...}
  ```



> synchronized在JVM实现原理：
>
> - monitorenter 指令是在编译后插入到同步代码块的开始位置。
> - 而 monitorexit 是插入到方法结束处和异常处。
> - JVM要保证每个monitorenter 必须有对应的 monitorexit 与之配对。
> - 任何对象都有一个 monitor 对象与之关联，当且一个 monitor 被持有后，它将处于锁定状态。线程执行到 monitorenter 指令时，将会尝试获取对象所对应的 monitor 的所有权，即尝试获得对象的锁。

![image-20220705112808040](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207051128150.png)



#### 2.2.1 Java对象头

synchronized用的锁是存在Java对象头的MarkWord里。

MarkWord：

![image-20220630172925084](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206301729141.png)



#### 2.2.2 锁的升级

- 锁的升级：无锁 —> 偏向锁 —> 轻量级锁 —> 重量级锁

##### 1 偏向锁

因为大多数情况下， 锁都是由同一个线程获得的，所以发明了偏向锁。

![image-20220630175026133](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206301750214.png)

- 偏向锁的初始化
  1. 当一个线程访问同步代码块并且获得锁时，会在锁对象头和栈帧的锁记录里存储锁偏向的<u>线程ID</u>。
  2. 以后，该线程进入同步代码块时，不需要进行CAS操作来加锁和解锁，而是简单测试一下对象头里Mark Word是否存储指向当前线程的偏向锁。
     - 如果测试成功，则表示该线程获得锁
     - 如果测试失败，则查看偏向锁标识是否为1
       - 为1，则将对象头的偏向锁指向当前线程
       - 没有设置，则使用CAS竞争锁

- 偏向锁的撤销

  - 当其他线程尝试竞争偏向锁，持有该偏向锁的线程就会释放锁。

    > 偏向锁的撤销，需要等到全局安全点。全局安全点：当前没有执行的字节码

    1. 暂停拥有偏向锁的线程
    2. 检查持有偏向锁的线程是否活着
       - 不存活：将对象头设置为无锁状态
       - 存活：栈中的锁记录和对象头的Mark Word要么重新偏向于其他线程，要么恢复到无锁或者标记对象不适合作为偏向锁，最后唤醒暂停的线程。



##### 2 轻量级锁

![image-20220701110451078](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207011104192.png)

==锁只能升级不能降级==，原因：

- 因为**自旋会消耗CPU**，为了避免无用的自旋（比如获得锁的线程被阻塞住了），一旦锁升级成重量级锁，就不会再恢复到轻量级锁状态。
- 当锁处于这个状态下，其他线程试图获取锁时， 都会被<u>阻塞</u>住，当持有锁的线程释放锁之后会<u>唤醒</u>这些线程，被唤醒的线程就会进行新一轮的夺锁之争



#### 2.2.3 锁的对比

![image-20220701111241785](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207011112891.png)



#### 2.2.4 锁的实现原理

##### 锁的释放和获取的内存语义

- 线程A释放锁，随后线程B获取这个锁，这个过程实质上是线程A通过主内存向线程B发送消息

##### 锁内存语义的实现

ReentrantLock为例

- ReentrantLock的实现依赖于Java同步器框架AbstractQueuedSynchronizer（**AQS**）。
- AQS使用一个整型的**volatile变量（命名为state）**来维护同步状态。



### 2.3  原子操作

#### 处理器实现原子操作

1. 总线锁
2. 缓存锁

#### Java实现原子操作

1. 锁
2. 循环CAS
   - 循环进行CAS直到成功为止
   - 除了偏向锁，JVM实现锁的方式都用了循环CAS：当一个线程想进入同步块的时候使用循环CAS的方式来获取锁，当它退出同步块的时 候使用循环CAS释放锁。
   - 问题：
     1. ABA问题
        - 解决：版本号
     2. 循环时间长开销大
     3. 只能保证一个共享变量的原子操作





## 三 Java内存模型

### 3.1 线程通信

#### 3.1.1 线程通信机制

1. 共享内存
2. 消息传递

#### 3.1.2 指令重排序

![image-20220701153700887](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207011537975.png)



### 3.2 final域的内存语义

### 3.3 happens-before

### 3.4 双重检查锁定与延迟初始化

- 在Java多线程程序中，有时候需要采用延迟初始化来降低初始化类和创建对象的开销。


#### 3.4.1 双重检查锁定

```java
public class DoubleCheckedLocking { // 1
    
	private static Instance instance; // 2
    
	public static Instance getInstance() { // 3
		if (instance == null) { // 4:第一次检查
			synchronized (DoubleCheckedLocking.class) { // 5:加锁
				if (instance == null) // 6:第二次检查
					instance = new Instance(); // 7:问题的根源出在这里
				} // 8
		} // 9
		
        return instance; // 10
	} // 11
}
```



##### 双重检查锁定的问题

- 双重检查锁定看起来似乎很完美，但这是一个错误的优化！在线程执行到第4行，代码读取到instance不为null时，**instance引用的对象有可能还没有完成初始化**（对象根据构造函数进行初始化）。

- 原因：

  - `instance = new Instance();`创建对象的过程是：

    ```java
    memory = allocate(); // 1：分配对象的内存空间
    ctorInstance(memory); // 2：初始化对象
    instance = memory; // 3：设置instance指向刚分配的内存地址
    ```

    - 上面3行伪代码中的2和3之间，可能会被重排序

      ![image-20220704132759873](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207041327933.png)

    - 如果发生重排序，另一个并发执行的线程B就有可能在第4行判断instance不为null。



##### 1. 基于volatile的解决方案

```java
public class DoubleCheckedLocking { 
    
	private volatile static Instance instance; // 添加volatile
    
	public static Instance getInstance() { 
		if (instance == null) { 
			synchronized (DoubleCheckedLocking.class) { 
				if (instance == null) 
					instance = new Instance(); // 现在没问题了
				} 
		} 
		
        return instance; 
	} 
}
```

- 当声明对象的引用为volatile后，上面伪代码中的2和3之间的重排序，在多线程环境中将会被禁止。



##### 2. 基于类初始化的解决方案

- JVM在类的初始化阶段（即在Class被加载后，且被线程使用之前），会执行类的初始化。
- 在执行类的初始化期间，JVM会去获取一个锁。**这个锁可以同步多个线程对同一个类的初始化**。

```java
public class InstanceFactory {
    
    // 私有静态类
	private static class InstanceHolder {
		public static Instance instance = new Instance();
	}
    
	public static Instance getInstance() {
		return InstanceHolder.instance ; // 这里将导致InstanceHolder类被初始化
	}
}
```

假如发生重排序：

![image-20220704135434783](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207041354848.png)

> 根据Java语言规范，在首次发生下列任意一种情况时，一个类或接口类型T将被立即初始化。
>
> 1）T是一个类，而且一个T类型的实例被创建。
>
> 2）T是一个类，且T中声明的一个静态方法被调用。 
>
> 3）T中声明的一个静态字段被赋值。 
>
> 4）T中声明的一个静态字段被使用，而且这个字段不是一个常量字段。 
>
> 5）T是一个顶级类（Top Level Class，见Java语言规范的§7.6），而且一个断言语句嵌套在T 内部被执行。







## 四 Java并发编程基础

### 4.1 线程

#### 4.1.1 线程的概念

- 进程是**<u>资源分配</u>**的基本单位，线程是**<u>CPU调度和分派</u>**的基本单位。

- 现代操作系统在运行一个程序时，会为其创建一个进程。

  例如，启动一个Java程序，操作 系统就会创建一个Java进程。

- 在一个进程里可以创建多个线程，这些线程都拥有各自的<u>计数器</u>、<u>堆栈</u>和<u>局部变量</u>等属性，并且能够访问共享的内存变量。

- 处理器在这些线程上高速切换，让使用者感觉到这些线程在同时执行。

#### 4.1.2 线程的状态

1. New：创建线程
2. Runnable：Java把操作系统的运行和就绪统称为运行状态
3. Blocked：等待锁释放时候的阻塞状态
4. Waited：等待其他线程唤醒的等待状态
5. Timed_Waited：设置了超时时间的超时等待状态，超过时间后进入Runnable
6. Terminated：运行结束状态。

#### 4.1.3 Daemon线程

- 支持型线程，用于程序后台调度和支持性工作。
- 如果Java虚拟机中不存在非Daemon线程（即全是Daemon线程），Java虚拟机就会退出。





### 4.2 启动和终止线程

#### 4.2.1 构造线程

- 一个能够运行的线程对象就初始化好了，会在堆内存中等待着运行。
- 四种方式：
  - implements Runnable
  - extends Thread
  - implements Callable
  - 线程池



#### 4.2.2 启动线程

- `start()` 方法的含义：当前线程<u>通知Java虚拟机</u>，只要线程规划器空闲，则立即启动start()里的方法。



#### 4.2.3 线程中断

- 其他线程通过调用该线程的`interrupt()` 方法对其进行中断操作。

  ```java
  public static void main(String[] args) throws Exception {
      Runner one = new Runner();
      Thread countThread = new Thread(one, "CountThread");
      countThread.start();
      // 睡眠1秒，main线程对CountThread进行中断，使CountThread能够感知中断而结束
      TimeUnit.SECONDS.sleep(1);
      countThread.interrupt(); // main线程把CountThread线程中断
      ...
  ```

  

- 线程通过方法`isInterrupted()` 来进行判断是否 被中断，也可以调用静态方法 `Thread.interrupted()` 对当前线程的中断标识位进行复位。如



#### 4.2.4 优雅地终止线程

```java
public class Shutdown {
	public static void main(String[] args) throws Exception {
		Runner one = new Runner();
		Thread countThread = new Thread(one, "CountThread");
		countThread.start();
		// 睡眠1秒，main线程对CountThread进行中断，使CountThread能够感知中断而结束
		TimeUnit.SECONDS.sleep(1);
		countThread.interrupt();
		Runner two = new Runner();
		countThread = new Thread(two, "CountThread");
		countThread.start();
        // 睡眠1秒，main线程对Runner two进行取消，使CountThread能够感知on为false而结束
		TimeUnit.SECONDS.sleep(1);
		two.cancel();
	}
    
	private static class Runner implements Runnable {
		private long i;
		private volatile boolean on = true;
        
		@Override
		public void run() {
			while (on && !Thread.currentThread().isInterrupted()){
			i++;
		}
            
		System.out.println("Count i = " + i);
	}
            
	public void cancel() {
		on = false;
	}
}
```

- 这种通过<u>标识位</u>或者<u>中断</u>操作的方式能够使线程在终止时有机会去清理资源，而不是武断地将线程停止，因此这种终止线程的做法显得更加安全和优雅。





### 4.3 线程间通信

如果多个线程能够相互配合完成工作，这将会带来巨大的价值

#### 4.3.1 volatile和synchronized实现线程通信

> Java支持多个线程同时访问一个对象或者对象的成员变量，由于每个线程可以拥有这个变量的拷贝（虽然对象以及成员变量分配的内存是在共享内存中的，但是每个执行的线程还是可以**拥有一份拷贝**，这样做的目的是加速程序的执行，这是现代多核处理器的一个显著特性），所以程序在执行过程中，一个线程看到的变量并不一定是最新的。

##### volatile保证可见性

- 线程读取volatile修饰的变量，都必须从共享内存中获取
- 线程写volatile变量，都必须同步刷新到共享内存中。

举个例子：

1. 定义一个表示程序是否运行的成员变量boolean on=true
2. 那么另一个线程可能对它执行关闭动作（on=false）
3. 这里涉及多个线程对变量的访问，因此需要将其定义成为 volatile boolean on＝true
4. 这样其他线程对它进行改变时，可以让所有线程感知到变化，**因为所有对on变量的访问和修改都需要以共享内存为准**。
5. 但是，过多地使用volatile是不必要的，因为 它会降低程序执行的效率。



##### synchronized

![image-20220705112808040](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207051128150.png)



#### 4.3.2 等待/通知机制

- 生产者-消费者

  - 生产者线程修改一个对象的值。

  - 消费者线程能感知到，然后进行相应的操作。

- java等待/通知方法
  - `notify()`：通知一个在对象上等待的线程，从wait方法返回；**返回前提是获得了该对象的锁**
    - 将等待队列中的一个等待线程从等待队列中移到同步队列中
  - `notifyAll()`：通知所有等待在该对象上的线程
    - 将等待队列中所有的线程全部移到同步队列
  - `wait()`：使<u>持有当前对象锁的线程</u>进入等待状态，等待另外线程通知或者被中断。
    - 调用该方法会释放锁
  - `wait(long n)`：超时等待，等待n毫秒后，没有通知就超过返回。
  - `wait(long n, int m)`

- 当线程终止时，会调用线程自身的notifyAll()方法，会通知所有等待在该线程对象上的线程。



![image-20220708132608431](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207081326565.png)

在图4-3中，

1. WaitThread首先获取了对象的锁，然后调用对象的wait()方法，从而放弃了锁并进入了对象的等待队列WaitQueue中，进入等待状态。
2. 由于WaitThread释放了对象的锁，NotifyThread随后获取了对象的锁，并调用对象的notify()方法，将WaitThread从WaitQueue移到SynchronizedQueue中，此时WaitThread的状态变为**阻塞状态**。
3. NotifyThread释放了锁之后，WaitThread再次获取到锁并从wait()方法返回继续执行



#### 4.3.3 管道输入/输出流



#### 4.3.4 Thread.join()的使用

- 当前线程A等待thread线程终止之后才从thread.join()返回。
  - 每个线程终止的前提是前驱线程的终止，每个线程等待前驱线程终止后，才从join()方法返回。



#### 4.3.5 ThreadLocal

- 线程变量
  - 键是ThreadLocal对象
  - 值是任意对象



### 4.4 线程应用实例

#### 4.4.1 等待超时模式

```java
// 对当前对象加锁
public synchronized Object get(long mills) throws InterruptedException {
    
	long future = System.currentTimeMillis() + mills;
	long remaining = mills;
    
	// 当超时大于0并且result返回值不满足要求
	while ((result == null) && remaining > 0) {
		wait(remaining);
		remaining = future - System.currentTimeMillis();
	}
    
	return result;
}
```



#### 4.4.2  一个简单的数据库连接池示例

```java
public class ConnectionPool {
    
	private LinkedList<Connection> pool = new LinkedList<Connection>();
    
	public ConnectionPool(int initialSize) {
		if (initialSize > 0) {
		for (int i = 0; i < initialSize; i++) {
			pool.addLast(ConnectionDriver.createConnection());
		}
	}
}
    
// 释放连接
public void releaseConnection(Connection connection) {
	if (connection != null) {
		synchronized (pool) {
			// 连接释放后需要进行通知，这样其他消费者能够感知到连接池中已经归还了一个连接
			pool.addLast(connection);
			pool.notifyAll();
		}
	}
}
    
// 在mills内无法获取到连接，将会返回null
public Connection fetchConnection(long mills) throws InterruptedException {
    
	synchronized (pool) {
		// 完全超时
		if (mills <= 0) {
			while (pool.isEmpty()) {
				pool.wait(); // 拿着pool锁的线程进入等待队列
			}
		
            return pool.removeFirst();
		} else {
            // 超时等待机制
            long future = System.currentTimeMillis() + mills;
			long remaining = mills;
            
			while (pool.isEmpty() && remaining > 0) {
				pool.wait(remaining);
				remaining = future - System.currentTimeMillis();
			}
            Connection result = null;
            if (!pool.isEmpty()) {
                result = pool.removeFirst();
            }
            	
            return result;
       	 	}
   	 	}
	}
}
```





## 五 Java中的锁

### 5.1 Lock接口

1. 使用方式

```java
Lock lock = new ReentrantLock();
lock.lock();
// 不要将lock.lock() 写在try内，否则上锁时出现异常会导致锁无故释放
try {

} finally {
	lock.unlock();
}
```



2. 比sychronized多的特性

   ![image-20220709134522085](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091345432.png)



3. 方法：
   - `lock()`
   - `tryLock()`：非阻塞的获取锁
   - `tryLock(long time, TimeUnit unit)`：超时非阻塞获取锁
   - `unLock()`
   - `Condition()`：获取等待通知组件，该组件和锁绑定，只有获取了锁，才能使用该组件中的wait()方法，而调用后，当前线程释放掉锁。



### 5.2 AQS

#### 5.2.1 AQS接口和示例

- 队列同步器，用于**构建锁**和其他同步组件
- 基于**模板方法**
- 使用了一个int成员变量表示同步状态，通过内置的FIFO队列来完成资源获取线程的排队工作

实现一个自定义独占锁：

```java
/*
 * @description: 独占锁
 * @author: Finn
 * @create: 2022/07/09 14:12
 */
public class MyLock implements Lock {
    // 用AQS实现一个排他锁
    private static class Sync extends AbstractQueuedSynchronizer {
        // 是否处于占用状态
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        // 当状态为0的时候获取锁
        public boolean tryAcquire(int acquires) {
            // 用CAS方法把状态码state设为1
            if (compareAndSetState(0, 1)) {
                // 设置排他锁的拥有者
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        // 释放锁，将状态设置为0
        protected boolean tryRelease(int releases) {
            if (getState() == 0) throw new IllegalMonitorStateException();
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
        // 返回一个Condition，每个condition都包含了一个condition队列
        Condition newCondition() { return new ConditionObject(); }
    }

    private Sync sync = new Sync();

    // 调用该方法的线程获取锁
    @Override
    public void lock() {
        if (!sync.isHeldExclusively()) {
            sync.tryAcquire(1);
        }
    }

    // 可中断的获取锁，该方法可以响应中断，在锁的获取中可以中断线程，然后抛出异常
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    // 尝试非阻塞的获取锁，调用该方法后立即返回。
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    // 尝试超时非阻塞的获取锁。
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    // 释放锁
    @Override
    public void unlock() {
        sync.release(1);
    }

    // 获取等待通知组件，该组件和锁绑定，只有获取了锁，才能使用该组件中的wait()方法，而调用后，当前线程释放掉锁。
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
```



#### 5.2.2 AQS的实现

1. **同步队列**

   ![image-20220709151847015](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207091518103.png)

   - **FIFO**双向链表
     - 首节点
       - 首节点是获取同步方法成功的节点，当首节点释放锁后，会唤醒后继节点，后继节点获取同步状态并把自己设置为首节点。
       - 设置首节点是获取锁成功的节点来完成的。
     - 尾节点
       - 当前线程获取锁失败后，会将当前线程以及等待状态等信息构造成为一个节点（Node）并将其加入同步队列的尾部；同时阻塞该线程。
       - 设置尾节点的方法通过**CAS**实现（因为加入队列必须要保证线程安全，否则会插乱掉），该方法传递当前线程认为的尾节点和当前节点：`compareAndSetTail(Node expect, Node update)`
     
- `acquire（int arg)` 方法对中断不敏感，如果线程被中断了，不会从同步队列中移除。




### 重入锁 ReentrantLock

- 支持一个线程对资源的重复加锁

  - 同一个线程调用`lock()`加锁以后，再次调用`lock()`，不会被自己阻塞

    > synchronzied 隐式的支持可重入：synchronzied 修饰过的方法在进行递归调用时，线程仍可以多次获得该锁

- 可以实现公平锁

  - 最先请求获得锁的线程优先获得锁（即等待时间最长的线程最先获得锁）



#### 实现可重入

1. 获取锁：线程在获得锁的时候，会判断是否是当前占据锁的线程，是的话，可以再次获得锁
2. 释放锁：锁维护一个计数器（`c=getState(); c + acquires;`），线程获得锁，计数自增，线程释放锁，计数自减。线程获得n次锁后，需要释放n次锁，其他线程才能再次获得这个锁。



#### 实现公平锁

公平锁在尝试获得锁的时候，会调用`hasQueuedPredecessors()`来判断当前线程在同步队列中，是否有前驱节点，如果有的话，则有更早请求的线程



### 读写锁 ReentrantReadWriteLock

- 读写锁在同一时刻可以允许多个**读**线程访问，而**写锁**会阻塞所有读锁和其他写锁

- 特性：
  - 支持非公平和公平的锁获取方式
  - 重进入
    - 读锁可再次获取读锁
    - 写锁可再次获取写锁，也可以再次获取读锁
  - 锁降级
    - 写锁可以降级为读锁



![image-20220711215022197](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207112150313.png)





## 六 Java并发容器和框架



## 七 Java中的13个原子操作类



## 八 Java中的并发工具类

### 8.1 CountDownLatch

> Latch：门闩
>
> DownLatch：下锁
>
> CountDownLatch：计数器锁存器（三二一，芝麻开门）

- CountDownLatch允许一个或多个线程等待其他线程完成操作。


```java
public class CountDownLatchTest {
    
    // 2表示计数器
    staticCountDownLatch c = new CountDownLatch(2);
    
    public static void main(String[] args) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(1);
                // 完成一个任务，计数器-1：此时计数器为1
                c.countDown();
                System.out.println(2);
                // 完成一个任务，计数器-1：此时计数器为0，主线程被唤醒
                c.countDown();
            }
        }).start();
        // 阻塞当前线程
        c.await();
        System.out.println("3");
    }
}
```

- 实现原理

   CountDownLatch是基于`AbstractQueuedSynchronizer`实现的，在`AbstractQueuedSynchronizer中`维护了一个volatile类型的整数**state**，volatile可以保证多线程环境下该变量的修改对每个线程都可见，并且由于该属性为整型，因而对该变量的修改也是原子的。创建一个CountDownLatch对象时，所传入的整数n就会赋值给state属性，当countDown()方法调用时，该线程就会尝试对state减一，而调用await()方法时，当前线程就会判断state属性是否为0，如果为0，则继续往下执行，如果不为0，则使当前线程进入等待状态，直到某个线程将state属性置为0，其就会唤醒在await()方法中等待的线程。



### 8.2CyclicBarrier

> Cyclic：可循环
>
> Barrier：屏障

#### 8.2.1 同步屏障CycliBarrier

- CyclicBarrier默认的构造方法是CyclicBarrier（int parties），其参数表示屏障拦截的线程数 量，每个线程调用await方法高速CyclicBarrier我已经到达了屏障，然后当前线程被阻塞。
- 直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续运行

```java
public class CyclicBarrierTest {
    staticCyclicBarrier c = new CyclicBarrier(2);
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 告诉屏障该线程已经到达，该线程阻塞
                    c.await();
                } catch (Exception e) {
                }
                System.out.println(1);
            }
        }).start();
        try {
            c.await();
        } catch (Exception e) {
        }
        System.out.println(2);
    }
}
```

- 因为主线程和副线程都有可能先执行：所以结果可能是：1，2，也可能是2，1




#### 8.2.2 应用场景

例如，用一个Excel保 存了用户所有银行流水，每个Sheet保存一个账户近一年的每笔银行流水，现在需要统计用户的日均银行流水，先用多线程处理每个sheet里的银行流水，都执行完之后，得到每个sheet的日均银行流水，最后，再用barrierAction用这些线程的计算结果，计算出整个Excel的日均银行流水

```java
public class BankWaterService implements Runnable {
    /**
     * 创建4个屏障，处理完之后执行当前类的run方法
     */
    private CyclicBarrier c = new CyclicBarrier(4, this);
    /**
     * 假设只有4个sheet，所以只启动4个线程
     */
    private Executor executor = Executors.newFixedThreadPool(4);
    /**
     * 保存每个sheet计算出的银流结果
     */
    private ConcurrentHashMap<String, Integer> sheetBankWaterCount = new ConcurrentHashMap<String, Integer>();
    
    private void count() {
        for (int i = 0; i< 4; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
					// 计算当前sheet的银流数据，计算代码省略
                    sheetBankWaterCount.put(Thread.currentThread().getName(), 1);
					// 银流计算完成，插入一个屏障
                    try {
                        c.await();
                    } catch (InterruptedException |
                            BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    
    @Override
    public void run() {
        
        int result = 0;
        
		// 汇总每个sheet计算出的结果
        for (Entry<String, Integer>sheet : sheetBankWaterCount.entrySet()) {
            result += sheet.getValue();
        }
        
		// 将结果输出
        sheetBankWaterCount.put("result", result);
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        BankWaterService bankWaterCount = new BankWaterService();
        bankWaterCount.count();
    }
}
```





#### 8.2.3 CyclicBarrier和CountDownLatch的区别

1. CountDownLatch计数器只能使用一次；而CyclicBarrier的计数器可以使用reset()方法重置。
2. CyclicBarrier还提供其他有用的方法
   - 比如`getNumberWaiting`方法可以获得Cyclic-Barrier阻塞的线程数量。
   - `isBroken()`方法用来了解阻塞的线程是否被中断。





### 8.3 信号量

- 信号量（Semaphore）是用来控制访问特定资源的线程数量

```java
public class SemaphoreTest {
    
    private static final int THREAD_COUNT = 30;
    // 30个线程的线程池
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    // 10个信号量控制线程数
    private static Semaphore s = new Semaphore(10);
    
    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 获取信号量
                        s.acquire();
                        System.out.println("save data");
                        s.release();
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        threadPool.shutdown();
    }
}
```

上面虽然有30个线程，但是只允许10个并发执行。



### 8.4 Exchanger

- Exchanger工具类用于线程间交换数据





## 九 Java线程池

使用线程池的好处：

1. **降低资源消耗**：线程可以重复利用，降低线程创建和销毁的消耗
2. **提高响应速度**：任务不用等待线程创建就可以执行
3. **提高线程可管理性**：使用线程池可以进行统一分配、调优和监控。



### 9.1 线程池的工作流程

![image-20220712143812695](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207121438799.png)





### 9.2 创建线程池

```java
new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, taskQueue, threadFactory, handler);
```

1. **corePoolSize**：核心线程数
   - 当提交一个任务到线程池时，线程池会创建一个线程来执行任务，即使其他空闲的基本线程能够执行新任务也会创建线程，等到需要执行的任务数大于线程池基本大小时就不再创建。如果调用了线程池的`prestartAllCoreThreads()`方法，线程池会提前创建并启动所有基本线程。
2. **maximumPoolSize**：最大线程数。
3. **keepAliveTime**：空闲线程的存活时间。
4. **timeUnit**：时间单位。
5. **taskQueue**：任务队列。
   - ArrayBlockingQueue：基于数组的FIFO队列
   - LinkedBlockingQueue：基于链表的FIFO队列
   - PriorityBlockingQueue：一个具有优先级的无限阻塞队列。
   - SynchronousQueue：一个不存储元素的阻塞队列。每个插入操作必须等到另一个线程调用 移除操作，否则插入操作一直处于阻塞状态
6. **threadFactory**：设置创建线程工厂，可以通过线程工厂来给线程设置有意义的名字。
7. **handler**：拒绝策略
   - AbsortPolicy：直接抛出异常。
   - CallerRunsPolicy：用调用者所在线程来执行任务（谁调用，谁处理）。
   - DiscardOldestPolicy：把最早加入队列的任务丢弃，然后加入新任务。
   - DiscardPolicy：直接丢弃任务。



### 9.3 向线程池提交任务

1. execute
   - 没有返回值
2. submit
   - 有返回值，返回一个Future类型的对象。通过`future.get()`可以判断任务是否成功



### 9.4 线程池关闭

原理：遍历工作线程，然后调用 `interrupt()` 方法来中断线程，所以无法响应中断的任务 可能永远无法终止。

- shutdown()
- shutdownNow()
  - 先将线程池状态设为 STOP，再去中断线程













