# MySQL

![image-20220721215416137](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207212154201.png)

## 基础

### Mysql存储特点

- Mysql存储数据以**数据页**为最小单位。
- 在同一个数据页中，数据按照**主键**，**连续**存储；如果没有主键，则按照Mysql维护的 **ROW_ID** 来连续存储。
- 数据页和数据页之间以双向链表关联。
- 数据和数据时间之间以单向链表关联。



### SQL 执行流程

![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207220930572.png)

查询



### SQL 语句执行顺序

![image-20220414232251002](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204142322054.png)

```sql
(8) SELECT 
	(9) DISTINCT<Select_list>
(1) FROM <LEFT_TABLE>
(3) <join type> JOIN <right_table>
(2)	ON <join_condition>
(4) WHERE <where_condition>
(5) GROUP BY <grou_by_list>
(6) WITH {CUBE|ROLLUP}
(7) HAVING <having_condition>
(10) ORDER BY <order_by_list>
(11) LIMIT <limit_number>
```





### Group By





### 左连接和右连接

比如A表为左表，B表为右表

1. 左连接
   - left join是以左表为准的，是以A表的记录为基础的。
     换句话说,左表(A)的记录将会全部表示出来,而右表(B)只会显示符合搜索条件的记录(例子中为: A.aID = B.bID)。B表记录不足的地方均为NULL。
2. 右连接
   - 和left join相反



### drop、delete、truncate的区别

1. delete
   - DELETE语句执行删除的过程是每次从表中删除一行，并且同时将该行的删除操作作为事务记录在日志中保存以便进行进行回滚操作。DELETE操作不会减少表或索引所占用的空间。
   - delete可根据条件删除表中满足条件的数据，如果不指定where子句，那么删除表中所有记录。

2. truncate
   - TRUNCATE TABLE 则一次性地从表中删除所有的数据并不把单独的删除操作记录记入日志保存，删除行是不能恢复的。
   - 并且在删除的过程中不会激活与表有关的删除触发器。执行速度快。
   - 当表被TRUNCATE 后，这个表和索引所占用的空间会恢复到初始大小，

3.   drop
   - drop语句删除表结构及所有数据，并将表所占用的空间全部释放。
   - 不能回滚，不会触发触发器。



### 不同的count用法

首先你要弄清楚count()的语义。count()是一个聚合函数，**对于返回的结果集，一行行地判断，如果count函数的参数不是NULL，累计值就加1**，否则不加。最后返回累计值。

#### count(*)

- MyISAM引擎把一个表的总行数存在了磁盘上，因此执行count(*)的时候会直接返回这个数，效率很高，但是不支持事务；
- 而 InnoDB 执行count(*)的时候，需要把数据一行一行地从引擎里面读出来，然后累积计数，虽然结果准确，但会导致性能问题。
- show table status 命令虽然返回很快，但是不准确；

#### count(1)

- InnoDB 会遍历整张表，但不取值。server 对于返回的每一行，放一个数字"1"进去，判断不可能为空的，按行累加。

#### count(主键id)

- InnoDB 会遍历整张表，然后把每一行的 id 取出，返回给 server。server 对于返回的每一行，判断不为空的，然后按行累加。

#### count(字段)

- InnoDB 会遍历整张表，然后 server 把不为 null 的字段取出来，逐行累加。



##### 为什么InnoDB不跟MyISAM一样，也把数字存起来呢？

![image-20220722174642728](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221746811.png)





## 存储引擎

### 1.MySIAM



### 2.InnoDB







#### (1) mySIAM 和 innoDB 的区别

> 存储引擎：是表级别的，形容数据表

#### 存储文件的区别

- mySIAM: .frm, .MYD, .MYI, 三个文件

- innoDB: .frm, .ibd

#### 索引的区别

##### mySIAM 索引

mySIAM索引底层数据结构是B+树

- mySIAM 的索引和数据保存在不同的文件中（.MYI 和 .MYD）

- 叶子节点保存的是数据**在表中的地址** （非聚集的）

##### InnoDB 索引

InnoDB索引底层数据结构是B+树

- 索引和数据保存在相同的文件中（.ibd）
- 叶子节点保存的是**表的数据** （聚集的：数据都保存在叶子节点）





### 3.Memory





## 索引

==**索引**是帮助 MySQL 高效获取数据的**排好序**的**数据结构**。==

### 1.为什么要索引？

**索引**是帮助 MySQL 高效获取数据的**排好序**的**数据结构**。

- 如果没有索引，查找表就是从第一行数据开始找，会使得数据库和磁盘直接进行大量的 IO 操作。降低效率。

  比如：`select * from t where t.col = 89;` 会一条一条取出来作比较。



### 2.什么是B+树？

![image-20220721205751913](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207212057065.png)

- B+ 树的非叶子节点，**只保存索引，而不保存数据**，因此 B+ 树比 B 树更加矮壮。这就意味着，B+ 树检索速度会更快
- B+ 树叶子节点是一个**<u>有序</u>双向链表**，遍历查询更方便。

- B+ 树的一个节点占有一页，一页大概是16k。每次查询，把一页加载到内存中去查询（比如二分查询）。如下图，就是一个节点（一页）：

  ![image-20220721210440592](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207212104635.png)

  h=3 的 B+ 树差不多能放2千万的数据。



#### **B+ 树查询过程**

- RAM是内存

![image-20220721210341252](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207212103456.png)



#### 为什么不用二叉搜索树？

- 如果数据是递增（或递减）的，搜索二叉树就成了一个链表。就和全表查询一样了。



#### 为什么不用红黑树？

> 红黑树就是二叉平衡树。左右子树高度差不超过1。

- 红黑树也是一种二叉树，所以数据量大的时候，树的高度较高。

**b 树比红黑树强的地方**

- 红黑树是一种"二叉搜索树"，每个node节点只能保存一对key，value
- B 或 B+ 树是一种”多路搜索树“，每个node节点可以保存多个数据
- 因此，相比较而言，B 或者 B+ 树比红黑树高度更低，高度更低就意味着检索速度更快。



#### 为什么不用 Hash 表

- 只能满足 =， IN 的查询，不支持范围查询

  比如查询 col > 10

- hash 有冲突问题



#### 为什么不用B树？

> B 树
>
> ![image-20220721205612962](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207212056049.png)

- B+ 树相比较 B 树，有两个优势
  - 更矮壮：B+ 树的非叶子节点，只保存索引，而不保存数据，因此 B+ 树比 B 树更加矮壮。这就意味着，B+ 树检索速度会更快
  - 叶子节点有序且是双向链表：B+ 树叶子节点是一个**有序双向链表**，遍历查询更方便，尤其支持范围查找。而B树就不行。



### 3.为什么建议 innoDB 表必须建主键，并且是auto_increment

- 如果不使用自增的主键，B+ 树会选一列唯一不重复的列来建立数据，如果没有，会维护一个row_id，但是这样会让 MySQL 多维护一列数据，而这个数据本身可以避免出现。
- 主键要自增：B+树叶子节点是有序排列的，如果主键索引不是自增的，新插入的数据可能会插到中间节点之间，这样可能会导致树不平衡而花费时间重新平衡。



### 什么是回表

回表会基于非主键索引的查询后**回到主键索引树搜索**的过程。即当通过非主键索引找到索引列值以外的字段时，就会回表。

比如：

```sql
create table T (
	ID int primary key,
	k int NOT NULL DEFAULT 0, 
	s varchar(16) NOT NULL DEFAULT '',
	index k(k)) engine=InnoDB;

insert into T values(100,1, 'aa'),(200,2,'bb'),(300,3,'cc'),(500,5,'ee'),(600,6,'ff'),(700,7,'gg');
```

![img](https://static001.geekbang.org/resource/image/dc/8d/dcda101051f28502bd5c4402b292e38d.png)

此时执行 `select * from T where k between 3 and 5 `：

1. 先在 k 索引树找到 k = 3 的数据记录(一个数据页,保存若干id数据)，找到 id = 300
2. 然后去主键索引树，找到 id = 300 的数据记录
3. 这个过程就是在回表



**原因：**

- 因为非主键索引建立的B+树叶子节点的数据表保存的是索引列值和主键。（聚簇索引保存的是主键和其他所有列值）

- 所以，如果要查询索引值以外的值时，先要通过非主键索引找到相应的主键值，再通过主键值去**<u>聚簇索引B+树</u>**找到相应的数据行，再读取出要查询的数据。

- 比如，MySQL采用非主键索引name来作为索引，那么底层B+树存放的是name列值和主键id。如果此时用一下sql查询

  ```sql
  select * from student where name = "James"
  ```

  那么MySQL只能进行查询到name列值和相应的id，而其他的列值就必须通过这个id，再去聚簇索引保存的B+树，找到相应的数据并读取。这就是回表。



#### 怎样避免回表

通过覆盖索引的方式。

- 就是建立联合索引，使得查询时候，想要的结果已经在叶子节点上了，而不需要进行回表操作。

- 比如如果要查 orderId 和 orderName，那就以这两个字段作为联合索引。这样 orderId 和 orderName 就作为数据保存在B+树的叶子节点上了，就不需要回表操作了。



### 覆盖索引

将被查询的字段，建立到联合索引里去

**场景1：全表count查询优化**

![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204141640649.jpeg)

原表为：
user(PK id, name, sex)；

直接：
select count(name) from user;
不能利用索引覆盖。

添加索引：
alter table user add key(name);
就能够利用索引覆盖提效。



### 什么是联合索引？

![image-20220721221648726](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207212216806.png)

#### 最左前缀原则/最左匹配原则

**B+树这种索引结构，可以利用索引的“最左前缀”，来定位记录。**

![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221051546.jpeg)

(I) 比如上图索引为 `index(name, age)`

- 比如

  - 查询条件为 where name = '张三'，则找到 ("张三", 10), id=4 的记录，然后往后遍历所有满足条件的值
  - 查询条件为 where name like '张%'，则找到 ("张六", 30), id=3 的记录，然后往后遍历所有满足条件的值

- 所以，只要满足最左前缀，就可以利用索引来加速查找。

  因此，建立索引时，要考虑索引顺序。比如，居民身份信息索引就只需建立 `(card_id, name)` 和 `(name)` 就行了，不能再单独建立一个 `(card_id)`



(II) 在最左匹配原则中，有如下说明：

1. 最左前缀匹配原则，非常重要的原则，mysql会根据联合索引一直向右匹配直到遇到范围查询 (>、<、between、like) 就停止匹配

   - 比如a = 1 and b = 2 and c > 3 and d = 4 如果建立(a,b,c,d)顺序的索引，d是用不到索引的，如果建立(a,b,d,c)的索引则都可以用到，a,b,d的顺序可以任意调整。
   - 因为B+树的叶子节点中，数据页和数据页是按照联合索引第一个值来排序的，然后才是第二个，然后才是第三个。

2. = 和 in 可以乱序，比如a = 1 and b = 2 and c = 3 建立(a,b,c)索引可以任意顺序，mysql的查询优化器会帮你优化成查询效率最高的形式，也就是匹配联合索引的形式。

   

1. **匹配最左边的列**

   - 比如联合索引是(a1, a2, a3)
     - 那么(a1)， (a1, a2)，(a1, a3) 都会触启用联合索引(a1, a2, a3)的查询，到联合索引(a1, a2, a3)树中去查询。（a2, a1）等也行
     - 而（a2），（a2，a3）等都不会触发联合索引(a1, a2, a3)的查询。

2. **.匹配列前缀**

   - 如果id是字符型，那么前缀匹配用的是索引，中坠和后缀用的是全表扫描。

   ```
   select * from staffs where id like 'A%';//前缀都是排好序的，使用的都是联合索引
   select * from staffs where id like '%A%';//全表查询
   select * from staffs where id like '%A';//全表查询
   ```



3. **遇到范围**

   - 遇到范围查询 (>、<、between、like) 就会停止匹配。

   - 原因：

     - 联合索引底层B+树是通过最左边的列来构建的。

       ![image-20220722121947945](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221219020.png)

       可以看到，左边的列是有序的，为1，1，2，2，3，3。左边列相同，才会根据后一个列值排序。所以，遇到范围查询是没办法按序去查询的，只能退化到线性查询。

> by the way， 联合索引最多只能包含16列



### 索引下推

当有联合索引 (name, age) 时，如果我们执行以下语句：

```sql
select * from user_info where name="王%" and age=20 and ismale=1;
```

- 在mysql 5.6之前，该查询首先会通过name去联合索引 (name, age) 树里查询，找到多条符合name="王%"的数据，然后根据 id 触发回表操作，去主键索引里找到符合条件的数据，整个过程需要回表多次。

![img](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202204021435220.png)



- mysql 5.6 之后，有了索引下推。在索引内部就会先判断 age 是否等于 20，这样在 (name, age) 联合索引树中只会找到一个数据，然后拿着 id 回表找到数据。整个过程只需要回表一次。

![img](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202204021439902.png)



### 聚簇/非聚簇索引

**特点**：

1. 使用**主键**值的大小进行记录和页的排序。
   - 页内的记录按照主键大小排成==单向链表==
   - 各个数据页之间按照主键大小顺序排成==双向链表==
2. MySQL一个表只有一个聚簇索引。
3. 如果没有定义主键。
   - InnoDB 会选择一个非空的唯一索引来代替。
   - 如果没有这样的索引，则 InnoDB 会隐式的定义一个主键来作为聚簇索引。

**缺点**：

1. 插入速度严重依赖于插入顺序。所以最好<u>**自增ID为主键**</u>， 否则插入会带来B+树的分裂。
2. 更新主键代价很高，所以最好主键为不可更新。



### MySQL 怎么创建索引？

三种方式

1. `CREATE INDEX <index_name> ON TABLE <table_name> (<column_name>)`
2. `ALTER TABLE <table_name> ADD INDEX <index_name>(<column_name>);`

3. ```
   CREATE TABLE tableName(  
     id INT NOT NULL,   
     columnName  columnType,
     INDEX [indexName] (columnName(length))  
   );
   ```



### 索引原则

#### 适合创建索引

1. 字段的数值有唯一性的限制。业务上具有唯一特性的字段，即使是组合字段，页必须建成唯一索引。

   - 比如学生的学号，具有唯一性。而姓名有可能出现同名的情况，就不适合。

2. 频繁被 WHERE 查询条件的字段。

3. 针对GROUP BY，ORDER BY的2个字段，需要建立联合索引且GROUP BY字段在前, ORDER BY字段在后。

   - GROUP BY：建立索引后，相同类型的数据就排在一起了。
   - ORDER BY：建立索引后，数据就有序了。

4. UPDATE、DELETE 的 WHERE 条件列

   - 如果更新的字段不是索引字段，提升效率会更明显，因为不需要因为修改了索引而维护索引。

5. DISTINCT 字段需要创建索引。 

6. 多表 JOIN 连接操作时，创建索引注意事项

   - 连接表的数量不要超过3张
   - 对 WHERE 条件创建索引
   - 对用于连接的字段创建索引

7. 使用字符串前缀对varchar创建索引

   ```SQL
   #对于varchar上创建索引，要指定索引长度，即查询前一部分，但是导致的问题就是排序不准确
   create table shop(addrass varchar(120) not null);
   #指定字符串索引前缀部门
   alter table shop add index(addrass(12));
   #查看区分度，越高越好
   select count(distinct addrass) / count(*) from shop;
   ```


8. 索引尽量不要超过6个。索引太多会占用磁盘空间，CUD需要维护索引。优化器也要进行多次选择。



#### 不适合创建索引

1. 重复数据多的字段：超过10%就不适合创建索引。
2. 频繁更新的字段
3. 不建议对无序字段创建索引。（主键是id的话，尽量自增）





## InnoDB事务

### 什么是事务？

事务是一组操作，这一组操作要么同时成功，要么同时失败。



### 事务的特性

ACID

- **A**tomicity：原子性
- **C**onsistency：一致性
- **I**solation：隔离性
- **D**uration：持久性

#### 原子性

事务的操作要么成功，要么失败。

- MYSQL InnoDB的底层是通过 undo log 来实现的。undo log 记载着变化前的数据。所以，一旦事务操作失败，数据库就会根据 undo log 的值回滚成原来的数据。

#### 一致性

一致性是事务的目的。我们对数据库操作，就是要保证数据的一致性。一旦事务操作失败，就应该回滚到原先的数据。

#### 隔离性

事务与事务之间是隔离的，互不影响的。

- 数据库一共有四种隔离级别。
-  底层是用锁来实现的

#### 持久性

事务一旦commit，那么对数据库的改变应该是永久的。就是说，数据应该被持久化在硬盘上。

- 持久性是通过 redo log 来实现的。数据库在对数据进行修改的时，先是查询找到相应要修改的数据页，然后把数据页写入到内存中，进行修改。

  为了防止mysql 挂掉，所以 mysql 会维护一个 redo log，记载本次在内存中对数据的修改。如果 MySQL 挂掉，我们也可以通过这个 redo log 来恢复数据。





## MySQL的锁

### 1.全局锁

- 给整个数据库加锁

- MySQL 提供了加**全局读锁**的方法：`FLUSH TABLES WITH READ LOCK`

  这样，其他线程的 DDL 和 DML 语句都会被阻塞

  - 使用场景：做全库逻辑备份（把每个表都 select 出来存成文本）



### 2.表级锁

#### 有哪些表级锁？

##### 表锁

`lock tables <tablename> read/write`

- 不支持**行锁**的引擎才会用到，比如MyISAM这类不支持事务的引擎。

- 比如：lock tables t1 read, t2 write

  其他线程**写** t1 和 **读写** t2 都会被阻塞。



##### 元数据锁 MDL

MDL不需要显式使用

- 当对一个表做**增删改查**的时候，会自动加上**MDL读锁**
  - 读锁不互斥，可以多线程同时对一个表进行增删改查
- 当对一个表的结构进行变动时，会自动加上**MDL写锁**
  - 读锁和写锁、写锁和写锁是互斥的，所以两个线程如果要同时给表加字段，第二个线程必须等第一个线程执行完才可以继续加。

- 事务中的 MDL，会在语句执行开始时申请，但是语句执行结束时不会马上释放，而是在事务提交后释放。



元数据锁的问题：

![image-20220722141650326](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221416412.png)

解决：

- 在做 DDL 变更时，如果有长事务在执行，要考虑暂停 DDL，或者 kill 掉长事务。



#### 表锁锁的是什么？

**表锁的是整个索引树**



### 3.行锁

#### (1) 行锁是什么？

- InnoDB 支持。针对数据库行记录的锁。



#### (2) 什么是两阶段锁？

##### a. 两阶段锁

两阶段锁——将事务的获取锁和释放锁分为增长和缩减两个阶段

- 增长阶段：事务可以获得锁，但不能释放锁
- 缩减阶段：事务只可以释放锁，不可以获得锁

![image-20220723164523754](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231645816.png)

##### b. 严格两阶段锁

- 锁的释放只能发生在事务 commit 或者 rollback 的时候。



##### c. 好处

- 使得事务的并发调度可串行化。（事务并发调度的结果和事务串行调度结果保持相同。）

  如果事务没有 commit 或者 rollback 就释放锁，那么其他事务可能在 commit 或 rollback 之前就更新数据，导致错误。

  - READ UNCOMMITTED: 允许事务读取更新了但未提交的数据。脏读、不可重复读、幻读的问题均存在。

  

> 在 InnoDB 中，行锁是在需要的时候才加上的，但是只会在**事务提交**以后才会释放。
>
> 如果事务中需要锁多行，那要把最可能造成锁冲突和最可能影响并发度的锁放后面。
>
> - 比如：一个电影票在线交易业务，顾客A要在影院B购买电影票.
>
>   ![image-20220722164609594](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221646653.png)



#### (3) 行锁锁的是什么？

- 行锁锁的是索引。

  ![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231402851.png)



#### (4) 怎么减少行锁对性能的影响？







### 4.死锁

![image-20220722165942152](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221659222.png)

发生死锁后的策略：

1. **超时等待**：设置超时时间，超时后自动退出

   - 缺点：超时时间不好评估，太长会影响线程工作效率，太短可能会在简单的锁等待时，误判锁是死锁退出。

2. **主动死锁检测**：判断是否有死锁链产生。从事务A到申请的资源持有者事务B，再一路判断下去，如果最终形成了一个环，说明有死锁。

   - 有死锁：选择死锁环中一个最小代价化的事务回滚，打破死锁条件。

     - 但是可能某个事务一直被回滚，造成**饥饿**现象。

   - 缺点：如果有大量并发线程在操作，那么进行死锁检测会消耗大量 CPU 资源。

     ==怎么解决由这种热点行更新导致的性能问题呢？==

     

   > 产生死锁的四个必要条件：
   >
   > 1. 不可剥夺条件：线程占有的资源不会被其他线程抢占。
   > 2. 请求和等待条件：线程占有一部分资源后，又去申请其他的资源。而申请的资源被其他的线程占有，因此等待其他线程释放资源。
   > 3. 互斥条件：线程占有的资源不可被其他的线程访问，其他线程想要获得该资源，必须等待线程释放该资源。
   > 4. 循环等待条件：产生死锁循环链。




### 5.共享锁/读锁 Slock

- 读锁可以被多个事务共享。多个事务可以同时读取一个资源，但是不允许其他事务修改这个资源。
- 针对行锁

- 加锁：`select * from T where id=1 lock in share mode;`
- 释放锁：`commit`、`rollback`



### 6.排他锁/写锁 Xlock

- 事务在进行写操作时，不允许其他事务进行读或者修改
- 加锁：
  - DML 语句默认加锁
  - `select * from T for update`

- 释放锁：`commit`、`rollback`



### 7.意向锁

- 意向锁的含义是如果对一个结点加意向锁，则说明该结点的下层结点正在被加锁。
- 加锁时可以通过节点意向锁来判断，而不用逐行判断是否加了行锁。



## 日志

### 1.两阶段提交

![image-20220722101359022](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221013110.png)

1. 先写入redo log，然后写入binlog，然后commit redo log

2. 为什么要这样？

   由于redo log和binlog是两个独立的逻辑，如果不用两阶段提交，要么就是先写完redo log再写binlog，或者采用反过来的顺序，会导致数据库的状态和日志恢复出来的库状态不一致

   ![image-20220722102225536](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207221022589.png)



### 2.binlog

二进制日志。

- binlog 是 <u>**server** 层定义的</u>，所有引擎都可以使用。

- binlog会记录所有的逻辑操作，并且是采用“追加写”的形式。

- binlog 用于复制和恢复数据，在主从复制中，从库利用主库上的 binlog 进行重播，实现主从同步。



### 3.Undo log 和 Redo log 有什么区别？

#### (1) undo log

- undo log 是用于事务回滚和 MVCC 多版本控制。是逻辑日志。
- undo log 记录的是上一个版本的数据。



#### (2) redo log

- <u>**InnoDB**</u> 持有的日志，是物理日志。

- MySQL每次**更新**一条数据，innoDB 引擎会把更新操作写到 redo log 里，并更新内存，当空闲的时候，再把 redo log 的操作到磁盘上更新。redo log 里记录了在数据页上做了什么修改。

- 有了 redo log， 就算 MySQL 异常重启，之前的记录也不会丢失。

  ![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207220950501.jpeg)
  - innoDB 的 redo log 文件一共有四个，占4GB。
  - checkpoint 是要擦除的位置，擦除记录前把记录更新到数据文件中。
  - write pos 是当前写的位置，一边写一边后移。

​		

### 4.binlog 和 Redo log 有什么区别？

1. binlog 是 server 层定义的；

   redo log 是 InnoDB 层定义的；

2. binlog 先于 redo log 被记录（两阶段提交）

   ![image-20220723194116233](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231941304.png)

3. binlog 是逻辑日志；

   redo log 是物理日志；

4. binlog 是追加写；

   redo log 是循环写，日志大小固定；







## 数据库并发问题

### 1.并发问题

1. 脏写：事务A修改了事务B未提交的数据。
2. 脏读：事务A读取了事务B更新，但未提交的数据。之后事务B对数据库进行了回滚，那么事务A读到的就是无效的数据。
3. 不可重复读：事务A读取的数据，该数据之后被事务B修改并提交。事务A再次读取这个数据时，发生这个数据和之前读到的不一样。
4. 幻读：事务A读取一张表时，事务B在这个表中插入了几行新的数据。当事务A再次读这个表时，发现表和之前的表不一样。



### 2.四种隔离级别

1. READ UNCOMMITTED: 允许事务读取更新了但未提交的数据。脏读、不可重复读、幻读的问题均存在。
2. READ COMMITTED: 允许事务读取已经被其他事务提交了的数据。可以避免脏读，但是不可重复读、幻读问题不可避免。
3. REPETABLE READ: 事务读取一个字段时，不允许其他事务对该字段进行修改。可以避免脏读，不可重复读，但幻读问题不可避免。
4. SERILIZATION: 事务读取一个表时，不允许其他事务对这个表进行操作。可以避免脏读，不可重复读，幻读问题，但是性能低。



#### (1) READ UNCOMMITTED原理

![image-20220719213335396](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207192133503.png)

- 读不加锁，写加锁。会带来脏读问题。脏读问题不可接受。
- 但是如果给读加锁，那么数据库在进行更新时，就不可读了，会带来性能上的问题
- 解决办法：MVCC（多版本并发控制 Multi-Version Concurrency Control）



#### (2) 可重复读是怎么实现的



### 3.悲观并发控制

加锁。—— 数据库的各种锁



### 4.乐观并发控制

乐观锁。

#### (1) 基于时间戳的协议

- 每个事务都有全局唯一的读时间戳和写时间戳。
- 读和写操作按照时间戳串行执行。
- 小于当前时间戳的事务进行回滚，并且重新分配时间戳。

postgresql 就使用了该协议



#### (2) 基于验证的协议

- *乐观并发控制*其实本质上就是基于验证的协议

- 将事务执行分为三个阶段：

  ![image-20220723145846909](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231458985.png)

  1. 读阶段：执行事务**所有的读操作和写操作**。
     - 写操作的值存入临时变量中，而不是真的更新数据库。
  2. 验证阶段：验证更新是否合法
     - 判断是否有其他事务在读阶段更新了数据
  3. 写阶段：
     - 如果更新合法（读阶段要更新的数据没有被其他事务改动），则将临时变量中的数据写入数据库。
     - 否则事务被abort



### 5.多版本并发控制 MVCC

![image-20220723150706114](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231507192.png)

前提：大多事务是只读的，而少数事务是写操作。

- 每一个写操作都会创建一个新版本的数据
- 读操作从多个版本的数据中选一个最合适的返回。



#### (1) MySQL 和 MVCC

> **总结：**
>
> 1. **insert:** 创建版本号 ——> 当前系统版本号
> 2. **update**: 找到比事务当前版本号小的最大版本号的数据行，创建一行新的数据行，并且更新数据，然后把创建版本号写为当前系统版本号
> 3. **delete**: 把删除版本号置为当前系统版本号
> 4. **select**: 选出数据行创建版本号小于等于事务创建版本号的，并且删除版本号未定义或者删除版本号大于事务删除版本号的。



##### InnoDB 的 MVCC

![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231614313.webp)

![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231614868.webp)



##### 具体实现

- **插入**：InnoDB为新插入的每一行保存当前系统版本号作为行版本号。

  ![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231618676.webp)

  ![img](https://upload-images.jianshu.io/upload_images/26650633-024bbc7aa2abbffc.jpeg?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

  

- **更新**：获取最大版本号的数据，然后计算该数据更新后的结果，并**创建**一个当前系统版本号的数据。

  1. 用写锁锁定行

  2. 记录 redo log

  3. 把改行修改前的数据复制到 undo log

  4. 修改当前行的值，填写事务编号，使回滚指针指向 undo log 中的修改前的行。

     ![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231624594.webp)

  ![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231619525.webp)

  

- **删除**：InnoDB 将数据行的删除标志设置为当前系统版本号

  ![img](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231625612.webp)



- **读**：满足两个条件：
  1. 数据行的 create_version <= 事务的 create_version
  2. 数据行的 delete_version 未定义，或者 > 事务的 delete_version



- MySQL 会定期清除版本最低的数据。



#### (2) PostgreSQL 与 MVCC 

多版本时间戳排序协议。

- PostgreSQL 中都是使用乐观并发控制的
- PG 的读请求，数据库直接返回最新的消息。



## 范式

### 第一范式

- 列都是不可再分的。

  ![image-20220723170646035](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231706096.png)

  - 右表满足第二范式



### 第二范式

- 有主键，非主键列依赖主键。不存在非主键列对主键的部分依赖。

  - 一个表描述一件事情

  例子1：

![image-20220723170741509](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231707570.png)

​		例子2：

​		id, stu_name, course_id, course_name 拆分学生表和课程表



### 第三范式

- 非主键列之间不存在传递依赖。

  比如：**Student(<u>id</u>, 姓名, 年龄, 所在学院, 学院地点, 学院电话)**

  - 因为存在如下决定关系：

    (学号) → (姓名, 年龄, 所在学院, 学院地点, 学院电话)，这个数据库是符合2NF的，

  - 但是不符合3NF，因为存在如下决定关系：

    (学号) → (所在学院) → (学院地点, 学院电话)

  



## 分库分表

### 1.分库

#### a.垂直分库







#### b.水平分库





### 2.分表

#### a.垂直分表



#### b.水平分表





## MySQL优化方案

### 1.数据库服务器内核优化

### 2.my.cnf 配置，搭配压力测试进行调试

### 3.sql语句调优

1. 使用缓存优化查询（不推荐）

   - 进行多次相同查询，结果会放入缓存中

   - 后续再进行同样的查询，就会直接从缓存中提取，不会到表中提取。

   - 但是，

     - 缓存失效会很频繁，只要表更新了，这个表所有的查询缓存就会失效，经常好不容易把结果保存了，结果还没用就失效了。
     
     - sql 语句要完全一样才能命中缓存。
     
     > 第一条语句会因为函数每次时间不一样导致缓存失效。
     >
     > ![image-20220721195422947](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207211954004.png)
     

2. explain 检测 SQL 查询
3. 给搜索字段建立索引 
   - 比如，给 where 后面的字段添加索引

4. 使用覆盖索引避免回表操作

   - 比如索引保存的是居民身份证， 但是现在有个高频请求：需要通过居民身份证去找到居民姓名，可以通过简历联合索引 (居民身份证，姓名) 来提高搜索效率。
5. limit 1（明确只有一行数据时）
6. 永久连接？
7. 选择正确的数据库引擎（mySIAM, innoDB）
8. 大量的 delete、insert 进行拆分
9. 数据类型尽量使用小的
10. 固定字段长度
11. 尽量不要给 null
12. 明确的固定的字段上使用 enum （性别、国家、市） varchar
13. id 主键每张表都要建立集群分区
14. 避免使用 select *
15. rand() 计算是在 cpu 上进行的
16. 连接两表的时候， join 尽量保持两个字段的类型一致
17. 垂直分割



### 四个层面

1. 查询语句优化（逻辑层面）
2. 索引优化（物理层面）
3. 数据库参数设置优化——**调整my.cnf**
4. 分库分表



### 查询语句优化

1. **切分查询**

   - 将大查询切分成小查询。每个小查询功能一样，只完成一小部分，每次返回一小部分的查询结果
   - 比如：删除大量的数据改成一次删除xxx行数据。

2. **分解关联查询**

   ![image-20220414225954918](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204142259978.png)



#### count

1. 统计某个列值的数量（不包括NULL）
   - COUNT(name)
   - COUNT(1)：统计主键列值的数量，而主键列肯定非空。因此COUNT(1)和COUNT(*)差不多
2. 统计结果集的行数（包括NULL）
   - COUNT(*)，忽略所有的列，直接统计所有的行数



### 索引查询优化

#### 最左前缀原则

在最左匹配原则中，有如下说明：

1. 最左前缀匹配原则，非常重要的原则，mysql会根据联合索引一直向右匹配直到遇到范围查询 (>、<、between、like) 就停止匹配，比如a = 1 and b = 2 and c > 3 and d = 4 如果建立(a,b,c,d)顺序的索引，d是用不到索引的，如果建立(a,b,d,c)的索引则都可以用到，a,b,d的顺序可以任意调整。
   - 因为B+树的叶子节点中，数据页和数据页是按照联合索引第一个值来排序的，然后才是第二个，然后才是第三个。
2. = 和 in 可以乱序，比如a = 1 and b = 2 and c = 3 建立(a,b,c)索引可以任意顺序，mysql的查询优化器会帮你优化成查询效率最高的形式，也就是匹配联合索引的形式。



```sql
CREATE TABLE `user` (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL DEFAULT '',
  `password` varchar(20) NOT NULL DEFAULT '',
  `usertype` varchar(20) NOT NULL DEFAULT '',
  PRIMARY KEY (`userid`),
  KEY `a_b_c_index` (`username`,`password`,`usertype`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
```

现在有一个user表，联合索引为(username, password, usertype)

```sql
explain select * from user2 where username = '1' and password = '1';
```

- 当查询条件有username，则会使用联合索引a_b_c_index查询。

- 当查询条件没有username，则不会使用。

  

```sql
explain select * from user2 where password = '1' and username = '1';
```

- 查询条件有username，乱序也是可以用上索引的

1. 



#### 索引失效情况

1. **计算导致索引失效**

   ```sql
   #用了索引
   EXPLAIN SELECT SQL_NO_CACHE id, stuno, NAME FROM student WHERE stuno = 900000;
   ```

   ```sql
   #计算导致索引失效
   EXPLAIN SELECT SQL_NO_CACHE id, stuno, NAME FROM student WHERE stuno+1 = 900001;
   ```

   

2. **函数导致索引失效**

   ```sql
   #创建索引
   CREATE INDEX idx_name ON student(NAME);
   
   # 索引起作用
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE student.name LIKE 'abc%';
   # 函数导致索引失效
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE LEFT(student.name,3) = 'abc'; 
   
   ```

   

3. **类型转换导致索引失败**

   ```sql
   # name是varchar型
   # 索引有用
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE NAME = '123'; 
   # 索引失效
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE NAME = 123; 
   ```

   

4. **范围条件右边的列索引失效**

   ```sql
   # 创建索引
   CREATE INDEX idx_age_classId_name ON student(age,classId,NAME);
   
   # 索引idx_age_classId_name不能正常使用
   EXPLAIN SELECT SQL_NO_CACHE * FROM student
   WHERE student.age=30 AND student.classId>20 AND student.name = 'abc' ;
   ```

   ```sql
   # 创建索引
   CREATE INDEX idx_age_name_classid ON student(age,NAME,classid);
   
   # 索引idx_age_name_classid可以正常使用
   EXPLAIN SELECT SQL_NO_CACHE * FROM student
   WHERE student.age=30 AND student.classId>20 AND student.name = 'abc' ;
   ```

   > 应用开发中范围查询，例如:金额查询，日期查询往往都是范围查询。应将查询条件放置where语句最后。（创建的联合索引中，务必把范围涉及到的字段写在最后）



5. **!= 或者 <> 导致索引失效**

   ```sql
   # 创建索引
   CREATE INDEX idx_name ON student(NAME);
   
   # 索引失效
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE student.name <> 'abc' ;
   
   # 但这个用了主键索引
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE student.id != 55 ;
   ```

​		

6. **is null可以用索引，is not null 无法使用索引**

   ```sql
   # 用了索引
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE age IS NULL; 
   
   # 没用索引
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE age IS NOT NULL; 
   ```

   

7. **like以通配符%开头索引失效**

   ```sql
   # 使用索引
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE NAME LIKE 'ab%';
   
   # 未使用索引
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE NAME LIKE '%ab%';
   ```

   > **拓展：Alibaba《Java开发手册》**
   > 【强制】页面搜索严禁**左模糊**或者**全模糊**，如果需要请走搜索引擎来解决。



8. **or 前后存在非索引的列，索引失效**

   ```sql
   #未使用到索引
   EXPLAIN SELECT SQL_NO_CACHE * FROM student WHERE age = 10 OR classid = 100;
   #在age字段上创建索引，发现还是没有用到索引
   CREATE INDEX idx_age ON student(age);
   #在classid上创建索引，这时候用上了索引，因为OR的前后两个字段都加上了索引。
   CREATE INDEX idx_cid ON student(classid);
   ```




9. **字符集不同导致索引失效**

 	





#### 索引查询优化

##### 关联查询优化

1. 确保ON或者USING子句上的列上有索引。
   - 一般来说，只需要在关联顺序中第二个表的相应列上加索引。

2. 内连接：有索引的数据量大的表作为被驱动表供没有索引的数据量小的驱动表查询。（给关联字段添加索引）
3. Join查询时，使用查询结果集(行 * 单行容量)小的驱动表嵌套大的驱动表

##### 子查询优化

1. 禁止使用not in，not exists子查询，改用`left join … where b.x is null;` 或者 `left join … where b.x is null = ''`;

##### 排序索引

1. 对应索引顺序不能错，否则不会使用索引 
2. 对于排序数据优化器会综合考虑全加载到内存进行fileSort更快还是使用索引排序更好。尽量使用上索引排序 
3. 当使用where ... order by时，也能用上索引

##### Group by 优化

1. 使用group by，order by，distinct时，尽量保证where过滤结果集在1000以内

##### 分页查询优化

1. select … from … limit 20000, 10改为select …from where id > 20000 limit 10。保证往聚簇索引上靠

   > - select* from article LIMIT 1,3
   > - select * from article LIMIT 3 OFFSET 1
   >
   > 上面两种写法都表示取第2,3,4三条数据。
   >
   > 
   >
   > **LIMIT用法**：
   >
   > ①如果后接一个参数，如select* from article LIMIT 10。表示取前10个数据。
   >
   > ②如果后接两个参数，如LIMIT 1,3 。第一个数表示要跳过的数量，后一位表示要取的数量，例如：select* from article LIMIT 1,3 就是跳过1条数据，从第2条数据开始取，取3条数据，也就是取2,3,4三条数据。
   >
   > **OFFSET用法**：
   >
   > ③当limit和offset组合使用的时候，limit后面只能有一个参数，表示要取的数量，offset表示要跳过的数量。例如：select * from article LIMIT 3 OFFSET 1 表示跳过1条数据,从第2条数据开始取，取3条数据，也就是取2,3,4三条数据。

   

##### 覆盖索引

##### 索引下推（ICP）

##### 主键设计方案

1. 淘宝：订单id可能是时间 +去重字段 + 用户id尾号6位
2. mysql8.0有改进的主键id







## MySQL主从同步



















