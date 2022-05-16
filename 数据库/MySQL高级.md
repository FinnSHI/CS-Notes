# MySQL高级部分

## MYSQL语句执行顺序

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



## SQL执行流程

### MySQL中SQL的执行流程

![image-20220414154135064](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204141541135.png)



**整体执行流程：**

![image-20220414154202501](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204141542546.png)

#### 1. 查询缓存

- 查询缓存中以key - value形式保存SQL语句和查询结果。Server 如果在查询缓存中找到 <u>**SQL 语句**</u>，则直接将结果返回给客户端，否则，进入解析器阶段。
- 查询缓存效率不高，MySQL 8.0以后，抛弃了这个功能。因为必须相同的查询才能命中缓存，任何字符上的不同（包括空格、注释、大小写）都会被认为不一样。



#### 2.解析器

- 词法分析

- 语法分析

- 生成语法树

  ![image-20220414154216178](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204141542243.png)

#### 3.优化器

- 确定SQL执行路径。比如**全表检索**，还是**索引检索**。
- 生成执行计划。
  - 物理查询优化：索引 + 表连接方式
  - 逻辑查询优化：SQL 等价变换（就是换一种查询方式）



#### 4.执行器

1. 执行之前判断是否有执行权限。
2. 返回结果

![image-20220414154232601](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204141542673.png)





## 引擎

### InnoDB

支持**外键**的事务存储引擎。InnoDB是为处理巨大数据量的最大性能而设计的。

优点：

- **支持事务**。可以确保事务的 commit 和 rollback。
- 除了增加和查询外，还需要更新、删除操作，则优先选择 InnoDB 存储引擎。
- **支持行级锁**。

缺点：

- 效率低
- 对内存要求高



### MyISAM

不支持事务、行级锁、外键

优点：

1. 访问速度快，适合以SELECT、INSERT为主的应用

缺点：

- 不支持事务
- 不支持行级锁



### InnoDB和MyISAM区别

![image-20220414154246624](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204141542712.png)



### 其他引擎

#### Archive引擎

- 用于数据存档
- 仅支持插入和查询功能
- MYSQL5.5以后支持索引。
- 采用行级锁

#### CSV引擎

- 可以将普通CSV文件作为MySQL表来处理。
- 不支持索引。

#### Memory

- ①表结构存储在磁盘中，类型为frm类型。②数据存储在内存里。③索引和数据存储分开存储
- 响应速度快。
- 守护进程崩溃的时候，数据会丢失。
- 支持hash索引和B+树索引





## 索引

### B+树

一般B+树都不会超过4层：

![image-20220414154314446](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202204141543513.png)



### 常见索引

#### 聚簇索引

**特点**：

1. 使用**主键**值的大小进行记录和页的排序。
   - 页内的记录按照主键大小排成==单向链表==
   - 各个数据页之间按照主键大小顺序排成==双向链表==
2. MySQL一个表只有一个聚簇索引。
3. 如果没有定义主键。
   - InnoDB会选择一个非空的唯一索引来代替。
   - 如果没有这样的索引，则InnoDB会隐式的定义一个主键来作为聚簇索引。

**缺点**：

1. 插入速度严重依赖于插入顺序。所以最好<u>**自增ID为主键**</u>， 否则插入会带来B+树的分裂。
2. 更新主键代价很高，所以最好主键为不可更新。



#### 非聚簇索引

也叫二级索引。

##### 回表

要查询2颗B+树。从非聚簇索引—>聚簇索引去找。



### sql对于索引的操作

1. 创建索引

   ```sql
   CREATE TABLE mytable(
          ID INT NOT NULL, 
          username VARCHAR(16) NOT NULL, 
          INDEX [indexName] (username(length))
    );
   ```

   

   ```sql
   # 创建一个主键
   ALTER TABLE tb_name ADD PRIMARY KEY (column_list);
   # 创建唯一索引
   ALTER TABLE tb_name ADD UNIQUE index_name(column_list);
   # 创建普通索引
   ALTER TABLE tb_name ADD INDEX index_name(column_list);
   # 创建全文索引
   ALTER TABLE tb_name ADD FULLTEXT index_name(column_list);
   ```

   

   ```sql
   CREATE INDEX stu_n ON TABLE stu_info(student_name)
   ```

   



### 索引设置原则

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









## 数据库优化

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
