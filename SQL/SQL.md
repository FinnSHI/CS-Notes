# 数据库

## 一、函数

### 函数分类

​		(1) 单行函数

​		(2) 分组函数



### 函数

#### 	a.字符函数

​		(1) **length**：**<u>字节</u>**长度。

​			i. utf-8: 1个字母一个字节，1个汉字**三**个字节 

​			ii. gdk: 1个字母一个字节，1个汉字**两**个字节

​		(2) **concat**: 拼接字符串  

​		(3) **upper(), lower()**

​		(4) **substr(), substring(str, num)**: 一个意思，获得string子串

​			i. substr(str, num)：从num到结尾

​			ii. substr(str, start, end):

​		(5) **instr(str, substr)**: 返回substr第一次出现在str的位置 

​		(6) **trim(str)**: 去掉str**前后**的空格

​			  **trim(substr from str)**: 去掉str前后的substr

​		(7) **lpad(str,num, substr)**: 左填充。用substr在str左边填空，使总**<u>字符</u>**长度为num个

​			  lpad('hhh', 5, '*'):  **hhh

​			  lpad('abc', 2, '*'): ab；str长度>num，就截断了

​		(8) **rpad(str,num, substr)**: 右填充。用substr在str右边填空，使总**<u>字符</u>**长度为num个

​		(9) **replace('ababa', 'a', 'c')**: cbcbc



#### 	b.数字函数

​		(1) **round()**：四舍五入。

​			 round(-1.65): -2；默认保留小数点1位

​			 round(1.567, 2): 1.57; 指定保留小数点2位

​		(2) **ceil()**: 向上取整 。返回>=该参数的最小整数

​			 ceil(-1.02): -1

​		(3) **floor()**: 向下取整。返回<=该参数的最大整数

​		(4) **truncate(num1, num2)**: 保留num1后面num2位，不四舍五入

​			 truncate(1.65, 1)**: 1.6; 保留小数点后面一位，不四舍五入

​		(5) **mod(num1, num2)**: num1%num2。计算方式：num1-num1/num2*num2



#### 	c.日期函数

​		(1) **now()**：当前日期+时间。

​		(2) **curdate()**: 当前日期。

​		(3) **curtime()**: 当前时间。

​		(4) **str_to_date(str, 格式):**  字符串转换为日期

​			 str_to_date('4-3 1992', '%c-%d %y'): 指定4是month，3是day，1992是year

​		(5) **date_format():** 日期转换成字符串

​			 date_format(now(), '%y年%m月%d日')：2021年9月11日



#### 	d.其他函数

​		(1) version()

​		(2) database()

​		(3) user()



#### 	e.流程控制函数

​		(1) **if(a, b, c)**: 实现三元运算符的效果。条件a满足则b，否则就c

​		(2)**case()**

```sql
SELECT *
CASE department_id
WHEN 30 THEN salary*1.1
WHEN 40 THEN salary*1.2
WHEN 50 THEN salary*1.3
ELSE salary*1.0
END
AS '新工资'
FROM employees;
#AS是把整个case语句结果作为值保存
```

```sql
SELECT salary,
CASE 
WHEN salary>10000 THEN 'A' 
WHEN salary>15000 THEN 'B' 
WHEN salary>20000 THEN 'C' 
ELSE 'D'
END 
AS '工资级别' 
FROM employees;
#AS是把整个case语句结果作为值保存
```



## 二、分组函数

***分组函数都忽略null值。***

##### a.sum()

sum()里放字符，结果是0

##### b.avg()

avg()里放字符，结果是0

##### c.max()

##### d.min()

##### e.count()

1. court(*): 统计行数。除非一行都是null，则不计数。

2. court(1):  统计行数。相当于新开了全是1的一列，计算多少个1。

   <u>效率</u>：

   MYISAM存储引擎下，COUNT(*) 效率高

   INNODB存储引擎下，COUNT(1)和COUNT(*) 效率差不多，比COUNT(字段)高



## 三、键

### 3.1 候选键

怎么计算候选键？

<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110281614059.PNG" alt="IMG_0170" style="zoom:50%;" />



<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110281614580.PNG" alt="IMG_0171" style="zoom:50%;" />



## 四、范式

### 1NF->2NF

消除非主属性对码的部分依赖

**举例：**

关系模式R：{A, B, C, D}

函数依赖F：{(A, B) -> D, A -> C}

- 候选码：A、B
- 是否是2NF？
  - 不是，因为存在非主属性对码的部分依赖：A->C，
- 如何变为2NF？
  - R->R1, R2
  - R1(A, B, D)
  - R2(A, C)



### 2NF->3NF

消除非主属性对码的传递依赖

**举例：**

关系模式R：{A, B, C, D}

函数依赖F：{A->BC, B -> D}

- 候选码：A
- 是否是3NF？
  - 不是，因为存在非主属性对码的传递依赖：A->B，B->D，则A->D
- 如何变为3NF？
  - 把传递依赖提取出来，R->R1, R2
  - R1(A, B, C)
  - R2(B, D)



### 3NF和BCNF

#### BCNF

不存在任何属性对码的传递依赖和部份依赖。即每个决定因素都包含码。

**举例：**

关系模式R：{A, B, C,}

函数依赖F：{(A, B) -> C, (C, B) -> A, C -> B)}

- 候选码：AB / CB
- 是否是BCNF？
  - 不是，因为C是决定因素，但C不是码（候选码）









# 零散知识整理

## 一. where和having的异同

都是用来筛选条件。

where: before the rows are summarized into groups by the <i>GROUP BY</i>, use <i>where</i>

having: after using <i>GROUP BY</i>，use <i>having</i>



## 二、<=>

安全等与 <=>

定义: 判断是否等于。可以判断是否是null。











# 错题整理

## 1

![](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110251631286.png)

B：'2012-12-31'是短日期，<i>between...and...</i>中的短日期默认为'2012-12-31 0:00:00'，所以<b>不能包括进2012年12月31日当天的数据</b>。

C：以上：>= 

D：COMM = 0