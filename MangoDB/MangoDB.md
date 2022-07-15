# MongoDB

## 概述

### 概念

MongoDB 是一个基于分布式文件存储的开源数据库系统。

- 面向文档存储
- 如果负载的增加（需要更多的存储空间和更强的处理能力） ，它可以分布在计算机网络中的其他节点上。（这就是所谓的**<u>分片</u>**）

### 术语

| SQL术语/概念 | MongoDB术语/概念 | 解释/说明                           |
| :----------- | :--------------- | :---------------------------------- |
| database     | database         | 数据库                              |
| table        | collection       | 数据库表/集合                       |
| row          | document         | 数据记录行/文档                     |
| column       | field            | 数据字段/域                         |
| index        | index            | 索引                                |
| table joins  |                  | 表连接,MongoDB不支持                |
| primary key  | primary key      | 主键,MongoDB自动将_id字段设置为主键 |

- 数据库 database
- 结合 collection
- 文档 document

#### 数据库

- MongoDB 可以建立多个数据库，默认的是db，存储在data下： `./data/db`
- 保留数据库：
  - admin：如果将用户添加到这个数据库，该用户就自动继承所有数据库的权限
  - local：这个数据永远不会被复制，可以用来存储限于本地单台服务器的任意集合
  - config：当Mongo用于分片设置时，config数据库在内部使用，用于保存分片的相关信息。
- 常用命令：
  - `dbs`：查看所有数据库
  - `db`：查看当前数据库
  - `use xxx`：连接或创建数据库xxx

#### 集合

文档的集合。

- 集合可以插入不同格式和类型的数据

#### 文档

- 一组 key-value

  比如：

  ```json
  {"site":"www.baidu.com", "name":"百度"}
  ```

- 文档中的键值对是有序的

- MongoDB文档不能有重复的键



#### ObjectId

ObjectId 类似唯一主键，可以很快的去生成和排序，包含 12 bytes

![img](https://www.runoob.com/wp-content/uploads/2013/10/2875754375-5a19268f0fd9b_articlex.jpeg)



#### Bson

JSON是把对象序列化成字符串

Bson(Binary JSON) 是把对象序列化成二进制



## MongoDB 数据类型

下表为MongoDB中常用的几种数据类型。

| 数据类型           | 描述                                                         |
| :----------------- | :----------------------------------------------------------- |
| String             | 字符串。存储数据常用的数据类型。在 MongoDB 中，UTF-8 编码的字符串才是合法的。 |
| Integer            | 整型数值。用于存储数值。根据你所采用的服务器，可分为 32 位或 64 位。 |
| Boolean            | 布尔值。用于存储布尔值（真/假）。                            |
| Double             | 双精度浮点值。用于存储浮点值。                               |
| Min/Max keys       | 将一个值与 BSON（二进制的 JSON）元素的最低值和最高值相对比。 |
| Array              | 用于将数组或列表或多个值存储为一个键。                       |
| Timestamp          | 时间戳。记录文档修改或添加的具体时间。                       |
| Object             | 用于内嵌文档。                                               |
| Null               | 用于创建空值。                                               |
| Symbol             | 符号。该数据类型基本上等同于字符串类型，但不同的是，它一般用于采用特殊符号类型的语言。 |
| Date               | 日期时间。用 UNIX 时间格式来存储当前日期或时间。你可以指定自己的日期时间：创建 Date 对象，传入年月日信息。 |
| Object ID          | 对象 ID。用于创建文档的 ID。                                 |
| Binary Data        | 二进制数据。用于存储二进制数据。                             |
| Code               | 代码类型。用于在文档中存储 JavaScript 代码。                 |
| Regular expression | 正则表达式类型。用于存储正则表达式。                         |



## MongoDB 启动服务

mongo.exe



## MongoDB 连接

