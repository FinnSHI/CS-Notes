# Spring 5

## 1 Spring

### 1.1 Spring 概述

1. Spring 是一个<u>开源</u>框架
2. Spring 为简化企业级开发而生，使用Spring，JavaBean 就可以实现很多以前要靠EJB才能实现的功能。同样的功能，在EJB 中要通过繁琐的配置和复杂的代码才能够实现，而在Spring 中却非常的优雅和简洁。
3. Spring 是一个**IOC**(DI)和**AOP**容器框架。
4. Spring 的优良特性：
  - 非侵入式：基于Spring 开发的应用中的对象可以不依赖于Spring 的API
  - 依赖注入：DI——Dependency Injection，反转控制(IOC)最经典的实现。
  - 面向切面编程：Aspect Oriented Programming——AOP
  - 容器：Spring 是一个容器，因为它包含并且管理应用对象的生命周期
  - 组件化：Spring 实现了使用简单的组件配置组合成一个复杂的应用。在Spring 中可以使用XML 和Java 注解组合这些对象。
5. **一站式：**在IOC 和AOP 的基础上可以整合各种企业应用的开源框架和优秀的第三方类库（实际上Spring 自身也提供了表述层的SpringMVC 和持久层的Spring JDBC）。

6) Spring 模块：

<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110221531849.png" alt="image-20211022153112777" style="zoom:67%;" />

### 1.2 HelloWorld

1）创建User对象：

```java
public class User {
    public void add(){
        System.out.println("add...");
    }
}
```

2）在src文件内添加xml文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/Spring-beans.xsd">

    <bean id="user" class="com.finn.hello.User"></bean>
</beans>
```

3）测试：

```java
public class TestSpring5 {
    @Test
    public void testAdd(){
        ApplicationContext context = new ClassPathXmlApplicationContext("bean1.xml");
        User user = context.getBean("user", User.class);
        user.add();
    }
}
```

<div style="page-break-after:always;"></div>

## 2 IOC 容器和Bean的配置

### 2.0 容器

> 什么是容器？
>
> - 容器为某种特定组件的运行提供必要支持的软件环境；
>
>   > 例如，Tomcat就是一个Servlet容器，它可以为Servlet的运行提供运行环境。类似Docker这样的软件也是一个容器，它提供了必要的Linux环境以便运行一个特定的Linux进程。
>
> - 容器还提供底层服务。
>
>   > 例如：Servlet容器底层实现了TCP连接，解析HTTP协议等非常复杂的服务，如果没有容器来提供这些服务，我们就无法编写像Servlet这样代码简单，功能强大的组件。

使用Spring的IoC容器，实际上就是通过类似XML这样的配置文件，把我们自己的Bean的依赖关系描述出来，然后让容器来创建并装配Bean。一旦容器初始化完毕，我们就直接从容器中获取Bean使用它们。



### 2.1 IOC和DI

#### 2.1.1 IOC 

> ​		在应用程序中的组件需要获取资源时，传统的方式是组件主动的从容器中获取所需要的资源，在这样的模式下开发人员往往需要知道在具体容器中特定资源的获取方式，增加了学习成本，同时降低了开发效率。
> ​		反转控制的思想完全颠覆了应用程序组件获取资源的传统方式：反转了资源的获取方向——改由容器主动的将资源推送给需要的组件，开发人员不需要知道容器是如何创建资源对象的，只需要提供接收资源的方式即可，极大的降低了学习成本，提高了开发的效率。这种行为也称为查找的被动形式。

简单说来，就是我们开发的时候，只需要把容器里的资源（比如DataSource）注入进组件就行了，不用关心怎么去new一个资源（比如一个组件不需要再单独去new一个DataSource）。

组件的资源由IOC容器来注入。



##### **1.IOC**

- **Inversion of Control**：控制反转，把<u>对象创建</u>和<u>对象之间的调用</u>过程，交给Spring来管理。
- 目的：降低耦合度



##### **2.IOC底层原理**

- XML解析
- 工厂模式
- 反射

**<u>底层原理图解</u>**：

![image-20211022153957501](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110221539568.png)

![image-20211022154442137](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110221544231.png)

![image-20211022154829511](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110221548601.png)



##### **3.IOC容器**

- IOC思想基于IOC容器完成，IOC容器底层就是对象工厂

- Spring提供两种IOC容器：

  - **`BeanFactory`** 接口：

    `BeanFactory` 在第一次获取 Bean 的时候，才会创建这个 Bean。
  
    ```java
    /* 加载配置文件的时候不会创建Bean，在获取Bean时才创建Bean。*/
    BeanFactory context = new ClassPathXmlApplicationContext("bean1.xml");//不创建对象
    User user = context.getBean("user", User.class);//获取的时候创建对象
    
    ```

  - **`ApplicationContext`** 接口：BeanFactory的子接口
  
    `ApplicationContext` 就是 Spring 接口，它会一次性创建所有的Bean。并且`ApplicationContext` 提供更多的功能。
    
    ```java
    /* 加载配置文件的时候就会创建所有的Bean */
    ApplicationContext context = new ClassPathXmlApplicationContext("bean1.xml");
    User user = context.getBean("user", User.class);
    ```



> 加载配置文件的时候就创建对象的好处是什么？
>
> - 创建对象是个耗时间耗资源的事情，这种事情在服务器启动的时候就完成更好，而不应该在与web响应的时候再去做。



**4.ApplicationContext接口的实现类**

- `FileSystemXmlApplicationContext`：全路径
- `ClassPathXmlApplicationContext`：默认就是src文件夹下的路径

<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110221559947.png" alt="image-20211022155924834" style="zoom: 67%;" />



#### 2.1.2 DI

- **Dependency Injection**：依赖注入

- IOC 的另一种表述方式：即组件以一些预先定义好的方式（例如：setter 方法）接受来自于容器的资源注入。相对于IOC而言，这种表述更直接。

  比如：BookService 接受 DataSource 的注入

  ```java
  public class BookService {
      private DataSource dataSource;
  
      public void setDataSource(DataSource dataSource) {
          this.dataSource = dataSource;
      }
  }
  ```

  

#### 2.1.3 **IOC操作Bean管理**

1. **IOC操作Bean管理：**
   
   - Spring创建对象
   - Spring注入属性
   
2. **Bean管理两种方式：**

   - 基于xml配置文件的方式
   - 基于注解的方式

   

### 2.2 基于xml管理Bean

#### 2.2.1 依赖注入的方式

- ==通过Setter来注入属性==

  - 创建对象：

    ```xml
    <!-- 
    	Spring根据bean标签来创建对象，bean标签里添加相应的属性。 
    		常用：
        	1) id：唯一标识
        	2) class：创建对象的路径
        创建对象的时候，默认执行无参数构造器
        	如果类里面声明了有参构造器，却没有声明无参构造器，则运行会报错。
    -->
    
    <bean id="book" class="com.finn.object.Book">
        <!--通过setter来注入属性-->
        <property name="bookName" value="天龙八部"></property>
    </bean>
    ```

  - 注入属性：

    ```java
    public class Book {
        private String bookName;
    
        //方法一：通过setter方法来注入属性
        public void setBookName(String bookName) {
            this.bookName = bookName;
        }
    }
    ```

- ==通过有参构造器来注入属性==

  - 创建对象：

    ```xml
        <bean id="book" class="com.finn.object.Book">
            <!--通过有参构造器来注入属性-->
            <constructor-arg name="bookName" value="资本论"></constructor-arg>
        </bean>
    ```

  - 注入属性：

    ```java
    public class Book {
        private String bookName;
    
        //方法二：通过有参构造器注入属性
        public Book(String bookName) {
            this.bookName = bookName;
        }
    }
    ```

  > 了解：p 名称空间
  >
  > > 为了简化XML 文件的配置，越来越多的XML文件采用属性而非子元素配置信息。Spring从2.5 版本开始引入了一个新的p 命名空间，可以通过<bean>元素属性的方式配置Bean的属性。
  > > 使用p命名空间后，基于XML 的配置方式将进一步简化：
  > >
  > > ```xml
  > > <!--第一步：在xml配置中添加p名称空间的地址（google一下）-->
  > > 
  > > <!--第二步：配置bean标签-->
  > > <bean id="book" class="com.finn.object.Book" p:bookName="浪潮之巅" />
  > > ```
  > >
  > > 



#### 2.2.2 xml注入其他类型的值

##### 注入字面量

```java
public class Book {
    private String address;

    public void setAddress(String address) {
        this.address = address;
    }
}
```

- null

  ```xml
  <bean id="book" class="com.finn.object.Book">
      <!-- 设置null值 -->
      <property name="address"><null /></property>
  </bean>
  ```

- 包含特殊符号

  ```xml
  <bean id="book" class="com.finn.object.Book">
      <!-- 特殊符号
           1.转义
           2.CDATA
       -->
      <property name="address">
          <value><![CDATA[<<南京>>]]></value>
      </property>
  </bean>
  ```

##### 注入外部bean

- 创建两个类`service`类和`dao`类

- 在`service`类中调用`dao`的方法

  ```java
  public class UserService {
      //创建UserDao类型属性，生成setter方法
      private UserDao userDao;
  
      public void setUserDao(UserDao userDao) {
          this.userDao = userDao;
      }
  
      public void add(){
          System.out.println("service add...........");
          userDao.update();
      }
  }
  ```

  ```java
  public interface UserDao {
      public void update();
  }
  ```

- 在Spring配置文件中进行配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.Springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.Springframework.org/schema/beans http://www.Springframework.org/schema/beans/Spring-beans.xsd">

    <!-- 创建service和dao接口实现类对象 -->
    <bean id="userService" class="com.finn.Spring5.service.UserService">
        <!--注入userDao对象-->
        <property name="userDao" ref="userDaoImpl"></property>
    </bean>
    <bean id="userDaoImpl" class="com.finn.Spring5.dao.UserDaoImpl"></bean>
</beans>
```

##### 注入内部bean

- 一对多关系：部门和员工
- 在实体类之间表示一对多的关系

```java
/* 部门类 */
public class Dept {
    private String deptName;

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
```

```java
/* 员工类 */
public class Emp {
    private String empName;
    private String gender;

    //对象表示员工所属部门
    private Dept dept;
    
    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
```

```xml
    <!--内部bean-->
    <bean id="emp" class="com.finn.Spring5.bean.Emp">
        <property name="empName" value="James"></property>
        <property name="gender" value="Man"></property>
        <!--设置对象类型的属性-->
        <property name="dept">
            <bean id="dept" class="com.finn.Spring5.bean.Dept">
                <property name="deptName" value="设计部"></property>
            </bean>
        </property>
    </bean>
```

##### 级联赋值

部门类不变，员工类中添加dept对象的`Getter()`方法：

```java
public Dept getDept() {
    return dept;
}
```

xml配置文件这样写：

```xml
    <bean id="emp" class="com.finn.Spring5.bean.Emp">
        <property name="empName" value="Amy"></property>
        <property name="gender" value="Woman"></property>
        <!--级联赋值 -->
        <property name="dept.deptName" ref="dept"></property>
    </bean>
    <bean id="dept" class="java.lang.String"/>
```



### 2.3 集合属性

```java
public class Student {
    //1.数组类型
    private String[] courses;

    //2.list集合类型
    private List<String> list;
    
    //3.Map类型
    private Map<String, String> map;
    
    //4.Set类型
    private Set<String> set;
    
    public void setCourses(String[] courses) {
        this.courses = courses;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setMap(Map<String, String> map) {
        this.mas = map;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }
}
```

#### 2.3.1 注入集合

- 注入数组类型属性
- 注入List类型属性
- 注入Map类型属性
- 注入Set类型属性

```xml
    <bean id="student" class="com.finn.Spring5.collectiontype.Student">
        <!--数组类型注入-->
        <property name="courses">
            <array>
                <value>Java</value>
                <value>C++</value>
            </array>
        </property>
        <!--List类型注入-->
        <property name="list">
            <list>
                <value>James</value>
                <value>Allen</value>
            </list>
        </property>
        <!--Map类型注入-->
        <property name="map">
            <map>
                <entry key="Python" value="py"></entry>
                <entry key="C++" value="cpp"></entry>
            </map>
        </property>
        <!--List类型注入-->
        <property name="set">
            <set>
                <value>MySQL</value>
                <value>Redis</value>
            </set>
        </property>
    </bean>
```

#### 2.3.2 注入存放对象的集合

创建Teacher类：

```java
public class Teacher {
    private List<Course> courseList;

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public void test(){
        System.out.println(courseList);
    }
}
```

xml配置文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.Springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.Springframework.org/schema/beans http://www.Springframework.org/schema/beans/Spring-beans.xsd">
    <bean id="teacher" class="com.finn.Spring5.collectiontype.Teacher">
        <property name="courseList">
            <list>
                <ref bean="course1" />
                <ref bean="course2" />
            </list>
        </property>
    </bean>

    <!--创建多个course对象-->
    <bean id="course1" class="com.finn.Spring5.collectiontype.Course">
        <property name="cName" value="JavaSE"/>
    </bean>
    <bean id="course2" class="com.finn.Spring5.collectiontype.Course">
        <property name="cName" value="Python基础"/>
    </bean>
</beans>
```

#### 2.3.3 提取集合属性注入的部分

- xml中将Spring名称标签引入
   - xmlns:util="http://www.Springframework.org/schema/util"
   - xsi:schemaLocation="http://www.Springframework.org/schema/util http://www.Springframework.org/schema/beans/Spring-util.xsd"

```java
public class Book {
    private List<String> bookList;

    public void setBookList(List<String> bookList) {
        this.bookList = bookList;
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.Springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.Springframework.org/schema/util"
       xsi:schemaLocation="http://www.Springframework.org/schema/beans http://www.Springframework.org/schema/beans/Spring-beans.xsd
                           http://www.Springframework.org/schema/util http://www.Springframework.org/schema/beans/Spring-util.xsd">

    <!--提取List集合类型属性注入-->
    <util:list id="bookList">
        <value>钢铁是怎样炼成的</value>
        <value>资本论</value>
        <value>诗经</value>
    </util:list>

    <!--注入提取出来的属性-->
    <bean id="book" class="com.finn.Spring5.collectiontype.Book">
        <property name="bookList" ref="bookList" />
    </bean>
</beans>
```

<br>

### 2.4 FactoryBean

#### 2.4.1 Bean概述

- Spring中有两种bean：
  - bean：返回指定类的一个实例
  - FactoryBean：生产bean的工厂。返回`getObject()`方法中定义返回值的对象。

#### 2.4.2 实现FactoryBean

- 步骤：

  1. 创建类，实现FactoryBean接口
  2. 实现接口中方法，返回指定的bean类型

  ```java
  public class MyBean implements FactoryBean {
      //指定返回的bean
      @Override
      public Object getObject() throws Exception {
          Course course = new Course();
          course.setcName("Java");
          return course;
      }
  
      @Override
      public Class<?> getObjectType() {
          return null;
      }
  
      @Override
      public boolean isSingleton() {
          return false;
      }
  }
  
  ```

  ```xml
  <bean id="myBean" class="com.finn.Spring5.factorybean.MyBean"></bean>
  ```

<br>

### 2.5 bean 的高级配置

#### 2.5.1 配置信息的继承

```xml
<!-- 以emp01作为父bean，继承后可以省略公共属性值的配置-->
<bean id="emp02" parent="emp01">
	<property name="empId" value="1002"/>
	<property name="empName" value="Jerry"/>
	<property name="age" value="25"/>
</bean>
```

- 继承：
  - 子bean从父bean 中继承配置，包括bean的属性配置
  - 子bean 也可以覆盖从父bean继承过来的配置

- 补充：
  - 父bean可以作为配置模板，也可以作为bean实例。
    - 若只想把父bean 作为模板，可以设置`<bean abstract="true">`，这样Spring 将不会实例化这个bean。
    - 如果一个bean 的class 属性没有指定，则必须是抽象bean。
  - 并不是`<bean>`里的所有属性都会被继承。比如：autowire，abstract 等。
  - 也可以忽略父bean的class属性，==让子bean指定自己的类，而共享相同的属性配置==。但此时父bean必须设置`<bean abstract="true">`。

#### 2.5.2 bean 之间的依赖

- bean之间的依赖：有的时候创建bean1的时候需要保证bean2也被创建，这时我们称bean1对bean2有依赖。

- 例如：要求创建Employee对象的时候必须创建Department；

  ```xml
  <bean id="emp03" class="com.finn.Spring5.bean.Employee" depends-on="dept">
  	<property name="empId" value="1003"/>
  	<property name="empName" value="Kate"/>
  	<property name="age" value="21"/>
  </bean>
  ```

<br>

### 2.6 Bean的作用域和生命周期(⭐)

#### 2.6.1 Bean作用域

在Spring里面，可以设置bean实例是**单实例**(singleton)还是**多实例**(prototype)。

- ==默认bean是<u>单实例</u>== (`<bean scope="singleton">`)
- 可以改成多实例 (`<bean scope="prototype">`)

> 注意：
>
> > - singleton，在Spring加载配置文件的时候，就会创建单实例对象。
> > - prototype，在Spring调用`getbean()`方法的时候，创建多实例对象。

#### 2.6.2 Bean的生命周期

##### 一般生命周期(5)

5步：

1. 通过无参构造器创建bean实例

2. 调用`setter()`注入bean属性值和对其他bean的引用

3. 调用bean的初始化方法（需要通过`<bean init-method="">`来指定相应的初始化方法）

4. 可以获取bean的对象

5. 容器关闭的时候，调用bean销毁的方法 

   - `context.close()`时，bean就销毁了

   - 可以通过`<bean destroy-method="">`来指定bean销毁时调用方法

<br>

##### 加上后置处理器后的生命周期(7)

加上bean的后置处理器后，bean生命周期一共7步

1. **创建实例：**通过<u>无参构造器</u>创建bean实例

2. **注入属性和引用其他bean：**调用`setter()`注入bean属性值和对其他bean的引用

3. **后置处理器：**bean实例传递给bean后置处理器的方法

4. **初始化：**调用bean的初始化方法（需要通过`<bean init-method="">`来指定相应的初始化方法）

5. **后置处理器：**bean实例传递给bean后置处理器的方法

6. **获取对象：**可以获取bean的对象

7. **销毁：**容器关闭的时候，调用bean销毁的方法 

   - `context.close()`时，bean就销毁了

   - 可以通过`<bean destroy-method="">`来指定bean销毁时调用方法

<br>

##### bean的后置处理器

1. bean 后置处理器允许在调用初始化方法前后对bean 进行额外的处理
2. bean 后置处理器对IOC 容器里的所有bean 实例逐一处理，而非单一实例。其典型应用是：检查bean 属性的正确性或根据特定的标准更改bean 的属性。
3. bean 后置处理器时需要实现接口：`BeanPostProcessor`
   - 在初始化方法被调用前后，Spring 将把bean实例分别传递给以下两个方法：
     - 初始化前：`postProcessBeforeInitialization(Object, String)`
     - 初始化后：`postProcessAfterInitialization(Object, String)`

　<br>

### 2.7 xml自动装配

#### 2.7.1 什么是自动装配

根据指定装配规则（属性名称或属性类型），Spring自动注入匹配的属性值

#### 2.7.2 怎样自动装配

员工类：

```java
public class Emp {
    private Dept dept;

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    @Override
    public String toString() {
        return "Emp{" +
                "dept=" + dept +
                '}';
    }

    public void test(){
        System.out.println(dept);
    }
}
```

部门类：

```java
public class Dept {}
```

xml：

```xml
    <!--
        实现自动装配
            autowire
                byName：根据属性名称注入。注意：注入bean的id值和类属性名称要一样
                byType：根据属性类型注入
    -->
    <bean id="emp" class="com.finn.spring5.autowire.Emp" autowire="byName">
        <!--<property name="dept" ref="dept"></property>-->
    </bean>
    <bean id="dept" class="com.finn.spring5.autowire.Dept" />
```

<br />

### 2.8 引入外部bean属性

1. 直接配置数据库信息

   - 配置Druid连接池
   - 引入Druid连接池依赖jar包

2. 引入外部属性文件配置数据库连接池

   - 创建properties 属性文件

     ```
     prop.userName=root
     prop.password=root
     prop.url=jdbc:mysql:///test
     prop.driverClass=com.mysql.jdbc.Driver
     ```

   - 引入context 名称空间

     ![image-20211023130554709](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110231308127.png)

   - 指定properties 属性文件的位置

     ```xml
     <!-- 指定properties属性文件的位置-->
     <!-- classpath:xxx 表示属性文件位于类路径下-->
     <context:property-placeholder location="classpath:jdbc.properties"/>
     ```

   - 从properties 属性文件中引入属性值

     ```xml
     <!-- 从properties属性文件中引入属性值-->
     <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
     <property name="user" value="${prop.userName}"/>
     <property name="password" value="${prop.password}"/>
     <property name="jdbcUrl" value="${prop.url}"/>
     <property name="driverClass" value="${prop.driverClass}"/>
     </bean>
     ```

<br>

### 2.9 基于注解管理bean

#### 2.9.1 什么是注解

- 注解：@注解名称(属性名称=属性值，属性名称=属性值…)
- 注解可以作用在类、方法、属性上面
- 目的：简化xml配置

#### 2.9.2 基于注解创建对象

##### 四个注解

创建bean实例的注解（下面4个注解功能是一样的）

- **@Component：**标识一个受Spring IOC 容器管理的组件
- **@Service**：标识一个受Spring IOC 容器管理的<u>持久化层</u>组件
- **@controller**：标识一个受Spring IOC 容器管理的<u>业务逻辑层</u>组件
- **@Repository**：标识一个受Spring IOC 容器管理的<u>表述层控制器</u>组件

<br />

##### 组件扫描

- **base-package属性：**指定一个需要扫描的基类包，Spring 容器将会扫描这个基类包及其子包中的所有类。

![image-20211023135545602](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110231422703.png)

- **包含与排除属性：**

  - `<context:include-filter>`：子节点表示要包含的目标类

  注意：通常需要与use-default-filters 属性配合使用才能够达到“仅包含某些组件”这样的效果。即：通过将use-default-filters 属性设置为false，禁用默认过滤器，然后扫描的就只是include-filter 中的规则指定的组件了。

  - `<context:exclude-filter>`：子节点表示要排除在外的目标类

  > component-scan 下可以拥有若干个include-filter和exclude-filter子节点

- 过滤表达式

<br>

##### 示例

1. 扫描组件

   组件被上述注解标识后还需要通过Spring 进行扫描才能够侦测到。

   - 首先，添加xmlns:context和xsi:schemaLocation

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:context="http://www.springframework.org/schema/context"
          xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                              http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
   ```

   - 然后，开启组件扫描

   ```xml
       <!--开启组件扫描-->
       <context:component-scan base-package="com.finn.spring5" />
   ```

2. 创建类，创建注解

   ```java
   /*
   * value可以省略
   *   缺省默认值: value="类名首字母小写"
   * */
   
   @Component(value="userService")
   public class UserService {
       public void test(){
           System.out.println("test");
       }
   }
   ```

- 

<br />

#### 2.9.4 基于注解注入属性 

##### 四个注解

- **@AutoWired**：根据属性类型自动装配
- **@Qualifier**：根据属性名称装配
- **@Resource**：可以根据属性类型，也可以根据属性名称。        ==(是javax拓展包的内容)==
- **@Value**：注入普通类型属性（比如String）

<br>

> 注意：
>
> > - @Qualifier，要和@AutoWired一起使用

##### @AutoWired示例

Service:

```java
@Service
public class UserService {
    //不需要再添加setter()
    @Autowired
    private UserDao userDao;

    public void add(){
        System.out.println("Service is adding.");
        userDao.update();
    }
}
```

Dao:

```java
public interface UserDao {
    public void update();
}
```

```java
@Repository
public class UserDaoImpl implements UserDao {

    @Override
    public void update() {
        System.out.println("UserDaoImpl is updated!");
    }
}
```

##### @Qualifier注解

Service：

```java
@Service
public class UserService {
    //不需要再添加setter()
    @Autowired
    @Qualifier(value="impl")//value是要注入的属性名称
    private UserDao userDao;

    public void add(){
        System.out.println("Service is adding.");
        userDao.update();
    }
}
```

Dao:

```java
@Repository(value="impl")
public class UserDaoImpl implements UserDao {

    @Override
    public void update() {
        System.out.println("UserDaoImpl is updated!");
    }
}
```

##### @Resource示例

```java
    @Resource
    @Resource(value="userDaoImpl")
    private UserDao userDao;
```

<br />

### 2.10 纯注解开发

#### 2.10.1 创建配置类

**替代xml配置文件**

```java
@Configuration
@ComponentScan(basePackages="com.finn.spring5")
public class SpringConfig {
    
}
```

#### 2.10.2 测试类

```java
    @Test
    public void test3(){
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        UserService us = context.getBean("userService", UserService.class);
        us.add();
    }
```

<div style="page-break-after:always;"></div>

## 3 AOP

### 3.1 AOP概述

- **AOP：** Aspect Oriented Programming，面向切面编程。

  AOP是一种新的方法论，是对传统OOP(Object-Oriented Programming，面向对象编程)	的补充。

- AOP可以对业务逻辑的各个部分进行隔离，降低耦合性。

  - 将**日志记录**，**性能统计**，**安全控制**，**事务处理**，**异常处理**等代码从业务逻辑代码中<u>划分</u>出来。

- 图解：

<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110232250496.png" alt="image-20211023144356785" style="zoom:67%;" />

<br/>

### 3.2 AOP术语

- **连接点(Joinpoint)**：<u>类里面可以被增强的方法叫连接点</u>。连接点是横切关注点在程序代码中的具体体现，对应程序执行的某个特定位置。例如：类某个方法调用前、调用后、方法捕获到异常后等。在应用程序中可以使用横纵两个坐标来定位一个具体的连接点：

​	<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110231452016.png" alt="image-20211023145235929" style="zoom:67%;" />

- **切入点(pointcut)：**<u>切入点是实际被真正增强的方法</u>。切入点是定位连接点的方式。每个类的方法中都包含多个连接点，所以连接点是类中客观存在的事物。如果把连接点看作数据库中的记录，那么切入点就是查询条件——AOP 可以通过切入点定位到特定的连接点。切点通过`org.springframework.aop.Pointcut`接口进行描述，它使用类和方法作为连接点的查询条件。
  - 切入点表达式：知道对哪个类里面的哪个方法进行增强。
- **通知(Advice)**：
  - <u>通知是实际增强的逻辑部分</u>。
  - 通知的类型：
    - 前置通知
    - 后置通知
    - 环绕通知
    - 异常通知
    - 最终通知
- **切面(Aspect)**：<u>切面是一个动作，是把通知应用到切入点的过程</u>。（比如把权限判断应用到登录方法）
- **横切关注点**：从每个方法中抽取出来的同一类非核心业务。
- **目标(Target)**：被通知的对象
- **代理(Proxy)**：向目标对象应用通知之后创建的代理对象

**术语图解：**

![image-20211023145410332](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110232250415.png)

<br/>

### 3.3 动态代理(⭐)

AOP底层原理：动态代理

- 两种情况的动态代理
  - 有接口：JDK动态代理
  - 没有接口：CGLIB动态代理

#### 3.2.1 有接口：JDK动态代理

- 代理对象代理UserDaoImpl，当UserDaoImpl想添加新功能时，可以通过**代理对象**来在原有`login()`基础上增加新的功能。

  <img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110232250389.png" alt="image-20211023165656443" style="zoom:100%;" />

  UserDao接口：

  ```java
  package com.finn.spring5;
  
  public interface UserDao {
      public int add(int a, int b);
      public String update(String id);
  }
  ```

  UserDaoImpl实现类：

  ```java
  package com.finn.spring5;
  
  /*
   * @description: 实现接口UserDao
   * @author: Finn
   * @create: 2021-10-23-21-38
   */
  public class UserDaoImpl implements UserDao{
  
      @Override
      public int add(int a, int b) {
          return a+b;
      }
  
      @Override
      public String update(String id) {
          return id;
      }
  }
  
  class TestProxy{
      public static void main(String[] args) {
          UserDaoImpl user = new UserDaoImpl();
          UserDao proxyUser = (UserDao) JDKProxy.getProxyInstance(user);
          System.out.println(proxyUser.add(1, 2));
          System.out.println(proxyUser.update("ID"));
      }
  }
  ```

  JDKProxy：

  ```java
  package com.finn.spring5;
  
  import java.lang.reflect.InvocationHandler;
  import java.lang.reflect.Method;
  import java.lang.reflect.Proxy;
  import java.util.Arrays;
  
  /*
   * @description: JDK代理
   * @author: Finn
   * @create: 2021-10-23-21-39
   */
  public class JDKProxy {
      //返回一个代理类对象
      public static Object getProxyInstance(Object obj){
          MyInvocationHandler myInvocationHandler = new MyInvocationHandler(obj);
          return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), myInvocationHandler);
      }
  }
  
  class MyInvocationHandler implements InvocationHandler{
      private Object obj;
  
      public MyInvocationHandler(Object obj) {
          this.obj = obj;
      }
  
      //增强的逻辑
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          //方法之前执行
          System.out.println("当前方法是："method.getName() + "，参数是: " + Arrays.toString(args));
  
          //执行本身的方法
          Object res = method.invoke(obj, args);
  
          //方法之后执行
          System.out.println("方法之后执行" + res);
  
          return res;
      }
  }
  ```

  运行代码：

  ![image-20211023221033657](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110232250357.png)

  

- Proxy.newProxyInstance( ) 有三个参数

  - 被代理类的类加载器ClassLoader
  - 被代理类实现的接口（可以是多个）
  - 实现InvocationHandler接口的类：放入增强的功能



#### 3.2.2 没有接口：CGLIB动态代理

- 创建子类代理对象

  ![image-20211217144837826](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112171449595.png)

<br />

### 3.4 AOP操作

Spring框架一般使用AspectJ实现AOP操作。

#### 3.3.1 AspectJ

##### AspectJ概述

AspectJ不属于Spring，它是一个<u>独立的AOP框架</u>。

Spring框架和AspectJ框架一同使用可以完成AOP操作。

##### 基于AspectJ实现AOP操作

1. 基于注解的方式(⭐)

2. 基于xml配置文件

   

#### 3.3.2 切入点表达式

- 作用：通过表达式的方式定位一个或多个具体的连接点。就是知道对哪个类下的哪个方法进行增强。

- 语法结构：

  ```java
  execution([权限修饰符] [返回值类型] [简单类名/全类名] [方法名]([参数列表]));
  ```

- **举例1**：对com.finn.dao.BookDao类里面的add()进行增强：

  ```java
  execution(* com.finn.dao.BookDao.add(..));//*表示任意修饰符；[返回值类型]可以省略；[参数列表]用..表示
  ```

  **举例2**：对com.finn.dao.BookDao类里面的所有方法进行增强：

  ```java
  execution(* com.finn.dao.BookDao.*(..));
  ```

  **举例2**：对com.finn.dao包里面的所有类和所有方法进行增强：

  ```java
  execution(* com.finn.dao.*.*(..));
  ```




<div style="page-break-after:always;"></div>

### 3.5 基于注解实现AOP操作(⭐)

#### 3.5.1 实现步骤

1. 创建类，并定义方法

   ```java
   /*被增强的类*/
   public class User {
       public void add(){
           System.out.println();
       }
   }
   ```

   

2. 创建增强类（编写增强逻辑）

   - 在增强类中创建方法，让不同方法代表不同通知类型

     ```java
     //增强类
     public class UserProxy {
         /*前置通知*/
         public void before(){
             System.out.println("before.....");
         }
     }
     ```

   

3. 进行通知的配置

   - Spring中开启注解扫描

   - 使用注解创建User和UserProxy对象

   - 在增强类上面添加@Aspect

   - 在Spring配置文件中开启生成代理对象 

     

4. 配置不同类型的通知

   - 在增强类里面，添加通知类型注解，使用切入点表达式进行配置
   
     
   
5. 抽取相同切入点

   - 每个通知上都有value="execution(* com.finn.spring5.aopanno.User.add(..))"
   - 所以用pointcut来抽取相同切入点



#### 3.5.2**代码**

```java
/*被增强的类User*/
@Component
public class User {
    public void add(){
        System.out.println();
    }
}
```

```java
//增强类
@Component
@Aspect
public class UserProxy {

    //相同切入点抽取
    @Pointcut(value="execution(* com.finn.spring5.aopanno.User.add(..)))")
    public void pointdemo(){

    }

    /*前置通知*/
    @Before(value="pointdemo()")
    public void before(){
        System.out.println("前置通知");
    }

    /*最终通知*/
    @After(value="pointdemo()")
    public void after(){
        System.out.println("最终通知");
    }

    /*后置通知（返回通知）：在@After之后通知*/
    @AfterReturning(value="pointdemo()")
    public void afterReturning(){
        System.out.println("后置通知");
    }

    /*环绕通知：在被增强的方法add()之前之后都通知*/
    @Around(value="pointdemo()")
    public void around(ProceedingJoinPoint p) throws Exception{
        System.out.println("前环绕通知");
        try {
            p.proceed();//执行被增强的add()方法
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("后环绕通知");
    }

    /*异常通知：只在连接点抛出异常时才执行异常通知*/
    @AfterThrowing(value="pointdemo()")
    public void afterThrowing(){
        System.out.println("异常通知");
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    <!--开启注解扫描-->
    <context:component-scan base-package="java" />
    <!--开启Aspect生成代理对象-->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>
</beans>
```

**结果：**

- 如果没有异常：

  ![](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110281534761.png)

- 如果有异常：

  ![image-20211028150802854](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110281534754.png)



#### 3.5.3 设置多个增强类的优先级

@Order(数字) 来设置增强类的优先级，数字越小，优先级越大

```java
//增强类1
@Component
@Aspect
@Order(1)
public class UserProxy{...}
```

```java
//增强类2
@Component
@Aspect
@Order(2)
public class UserProxy{...}
```





### 3.6 基于xml实现AOP操作(了解)

1. 创建增强类和被增强类
2. xml中创建两个类的对象
3. xml中配置切入点



## 4 JdbcTemplate

### 4.1 概念

> Spring框架对JDBC进行封装，使用JdbcTemplate对数据库进行操作



**步骤：**

1. 引入相关jar包

2. spring配置文件中配置数据库连接池

   ```java
   <!-- 数据库连接池 -->
   <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource"
         destroy-method="close">
       <property name="url" value="jdbc:mysql://127.0.0.1:3306/spring5" />
       <property name="username" value="root" />
       <property name="password" value="root" />
       <property name="driverClassName" value="com.mysql.jdbc.Driver" />
   </bean>
   ```

   

3. 配置JdbcTemplate对象，注入DataSource

   ```java
   <!--JdbcTemplate对象-->
   <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
       <!--注入-->
       <property name="dataSource" ref="dataSource"></property>
   </bean>
   ```



4. 创建service类，创建dao类。在dao里注入jdbcTemplate

   - 配置文件

     ```java
     <!--组件扫描-->
     <context:component-scan base-package="cn.spring5.Aop"></context:component-scan>
     ```

   - service

     ```java
     @Service
     public class BookService {
         //注入Dao
         @Autowired
         private BookDao bookDao;
     }
     ```

   - dao

     ```java
     @Repository
     public class BookDaoImpl {
         // 注入JdbcTemplate
         @Autowired
         private JdbcTemplate jdbcTemplate;
     }
     ```



### 4.2 增删改查

数据库：

```mysql
CREATE TABLE t_book(
   book_id BIGINT(20) NOT NULL,
   book_name VARCHAR(100) NOT NULL,
   book_status VARCHAR(50) NOT NULL,
   PRIMARY KEY(book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

#### 4.2.1 添加

1. **数据库实体类**

   ```java
   public class User {
       private String userId;
       private String userName;
       private String userStatus;
   
       public String getUserId() {
           return userId;
       }
   
       public void setUserId(String userId) {
           this.userId = userId;
       }
   
       public String getUserName() {
           return userName;
       }
   
       public void setUserName(String userName) {
           this.userName = userName;
       }
   
       public String getUserStatus() {
           return userStatus;
       }
   
       public void setUserStatus(String userStatus) {
           this.userStatus = userStatus;
       }
   }
   ```

   

2. **编写service和dao**

   - service

     ```java
     @Service
     public class BookService {
     
         //注入dao
         @Autowired
         private BookDao bookDao;
     
         // 添加
         public void addBook(Book book){
             bookDao.add(book);
         }
     }
     ```

   - dao

     ```java
     @Repository
     public class BookDaoImp implements BookDao{
         @Autowired
         private JdbcTemplate jdbcTemplate;
     
         public void add(Book book) {
             String sql = "insert into t_book values(?,?,?)";
             Object[] args = {book.getBookId(), book.getBookName(), book.getBookStatus()};
             
             jdbcTemplate.update(sql, args);
         }
     }
     ```

   

3. **测试**

   ```java
   @Test
   public void testAdd(){
       ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
       BookService bookService = context.getBean("bookService", BookService.class);
       Book book1 = new Book();
       book1.setBookId("1");
       book1.setBookName("WangNing2");
       book1.setBookStatus("borrowed");
       bookService.addBook(book1);
   }
   ```

   



#### 4.2.2 修改和删除

```java
    public void updateBook(Book book) {
        String sql = "update t_book set book_name=?,book_status=? where book_id=?";
        Object[] args = {book.getBookId(), book.getBookName(), book.getBookStatus()};

        int update = jdbcTemplate.update(sql, args);
        System.out.println(update);
    }

    public void delete(String id) {
        String sql = "delete from t_book where book_id=?";

        int update = jdbcTemplate.update(sql, id);
        System.out.println(update);
    }
```



#### 4.2.3 查询

1. **查询返回值**

   查询数据库里有多少内容

   ```java
   public int selectCount() {
       String sql = "select count(*) from t_book";
       Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
       return count;
   }
   ```

2. **查询返回对象**

   查询某一本图书详情

   ![image-20211219162300154](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112191804913.png)

   ```java
   public Book selectBookInfo(String id) {
       String sql = "select * from t_book where book_id = ?";
       Book book = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Book>(Book.class), id);
       return book;
   }
   ```

3. **查询返回集合**

   查询所有图书

   ![image-20211220140248055](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112201402146.png)
   
   ```java
   public List<Book> findAllBook() {
       String sql = "select * from t_book";
       List<Book> books = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Book>(Book.class));
       return books;
   }
   ```



#### 4.2.4 批量操作

1. **批量添加**

   ```java
       public void batchAddBooks(List<Object[]> batchArgs) {
           String sql = "insert into t_book values(?,?,?)";
           int[] ints = jdbcTemplate.batchUpdate(sql, batchArgs);
           System.out.println(Arrays.toString(ints));
       }
   ```

   **测试**

   ```java
       @Test
       public void testBatchAdd(){
           ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
           BookService bookService = context.getBean("bookService", BookService.class);
           List<Object[]> batchArgs = new ArrayList<Object[]>();
           Object[] o1 = {"3", "java", "borrowed"};
           Object[] o2 = {"4", "mysql", "borrowed"};
           Object[] o3 = {"5", "c++", "borrowed"};
           batchArgs.add(o1);
           batchArgs.add(o2);
           batchArgs.add(o3);
           bookService.batchAdd(batchArgs);
       }
   ```

   

2. **批量删除**

   ```java
       public void batchDeleteBooks(List<Object[]> batchArgs) {
           String sql = "delete from t_book where book_id=?";
           int[] ints = jdbcTemplate.batchUpdate(sql, batchArgs);
           System.out.println(Arrays.toString(ints));
       }
   ```

   **测试**

   ```java
       @Test
       public void testBatchDelete(){
           ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
           BookService bookService = context.getBean("bookService", BookService.class);
           List<Object[]> batchArgs = new ArrayList<Object[]>();
           Object[] o1 = {"3"};
           Object[] o2 = {"4"};
           batchArgs.add(o1);
           batchArgs.add(o2);
           bookService.batchDelete(batchArgs);
       }
   ```

   

3. **批量修改**

   ```java
       public void batchUpdateBooks(List<Object[]> batchArgs) {
           String sql = "update t_book set book_name=?,book_status=? where book_id=?";
           int[] ints = jdbcTemplate.batchUpdate(sql, batchArgs);
           System.out.println(Arrays.toString(ints));
       }
   ```

   **测试**

   ```java
       @Test
       public void testBatchUpdate(){
           ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
           BookService bookService = context.getBean("bookService", BookService.class);
           List<Object[]> batchArgs = new ArrayList<Object[]>();
           Object[] o1 = {"java 0.2", "borrowed", "3"};
           Object[] o2 = {"mysql 0.2", "borrowed", "4"};
           Object[] o3 = {"c++ 0.2", "borrowed", "5"};
           batchArgs.add(o1);
           batchArgs.add(o2);
           batchArgs.add(o3);
           bookService.batchUpdate(batchArgs);
       }
   ```

   



### 4.3 事务操作

#### 4.3.1 概念

1. 什么是事务？

   > - 事务是数据库操作最基本单元。逻辑上一组操作，要么都成功，如果有一个失败所有操作都失败
   > - 典型场景:银行转账

2. 事务的四个特性 (ACID)

   - **原子性(Atomicity)**：一个事务的操作要么全都成功，要么全都失败
   - **一致性(Consistency)**：操作之前和操作之后，数据库的完整性不被破坏
   - **隔离性(Isolation)**：事务与事务的操作相互不受影响。
     - 数据库允许多个并发事务同时对其数据进行读写和修改的能力，隔离性可以防止多个事务并发执行时由于交叉执行而导致数据的不一致。
     - 事务隔离分为不同级别，包括未提交读（Read uncommitted）、提交读（read committed）、可重复读（repeatable read）和串行化（Serializable）。
   - **持久性(Durability)**：事务提交之后，对数据的修改是永久的

   

#### 4.3.2 搭建事务操作环境

![image-20201014214050651](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112201639167.png)



1. 创建数据表，添加记录

   ```mysql
   CREATE TABLE bank(
       id INT UNSIGNED AUTO_INCREMENT,
       username VARCHAR(200) NOT NULL,
       money DECIMAL(12,2),
       PRIMARY KEY(id)
   )ENGINE=InnoDB DEFAULT CHARSET=utf8;
   ```

   

2. 创建service，搭建dao。完成对象创建和注入关系

   ```java
   @Service
   public class UserService {
   
       @Autowired
       private UserDao userDao;
   
       public void addUser(User user){
           System.out.println(userDao.addUser(user));
       }
   
       public void accountMoney(){
           System.out.println(userDao.reduceMoney());
           System.out.println(userDao.addMoney());
       }
   }
   ```

   

3. dao中创建两个方法 addMoney和reduceMoney

   ```java
   @Repository
   public class UserDaoImpl implements UserDao {
       @Autowired
       private JdbcTemplate jdbcTemplate;
   
       public String addUser(User user) {
           String sql = "INSERT INTO bank VALUES (?, ?, ?)";
           Object[] args = {user.getUserId(), user.getUserName(), user.getMoney()};
           jdbcTemplate.update(sql, args);
           return user.getUserName();
       }
   
       public int addMoney() {
           String sql = "UPDATE bank SET money=money+? where username=?";
   //        Object[] args = {user.getMoney(), user.getUserId()};
           int update = jdbcTemplate.update(sql, 100, "Mary");
           return update;
       }
   
       public int reduceMoney() {
           String sql = "UPDATE bank SET money=money-? where username=?";
   //        Object[] args = {user.getMoney(), user.getUserId()};
           int update = jdbcTemplate.update(sql, 100, "Lucy");
           return update;
       }
   }
   ```

   

4. 测试

   ```java
       @Test
       public void testAccountMoney(){
           ApplicationContext context = new ClassPathXmlApplicationContext("bean1.xml");
           UserService userService = context.getBean("userService", UserService.class);
           User mary = new User();
           mary.setUserId(1);
           mary.setUserName("mary");
           mary.setMoney(500);
           User lucy = new User();
           lucy.setUserId(2);
           lucy.setUserName("lucy");
           lucy.setMoney(500);
           userService.addUser(mary);
           userService.addUser(lucy);
           userService.accountMoney();
       }
   ```

   此时结果是正常的：

   ![image-20211221221339631](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112212213761.png)

5. 模拟异常发生的情况

   ```java
       public void accountMoney(){
           System.out.println(userDao.reduceMoney());
   
           //模拟异常
           int i = 1/0;
   
           System.out.println(userDao.addMoney());
       }
   ```

   再测试，结果就异常了：

   ![image-20211221221418492](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112212214580.png)

6. 解决异常情况
   - 第一步，开启事务
   - 第二步，进行业务操作
   - 第三步，如果没有发生异常，事务提交
   - 第四步，出现异常，事务回滚



#### 4.3.3 Spring事务管理介绍

1. 事务添加在业务逻辑层（service层）

2. 事务操作方式：

   - 编程式事务操作

   - **声明式事务操作**（使用）

     - **基于注解的方式**（使用）
     - 基于xml配置的方式

     Spring进行声明式事务操作，底层是**AOP**原理

3. Spring事务管理API

   - 事务管理器接口 PlatformTransactionManager( )





### 4.4 声明式事务管理的参数配置

`@Transactional` 可以配置事务的相关参数

- `propagation` ：事务传播行为
- `iosalation`：事务隔离级别
- `timeout`：超时时间
- `readOnly`：是否只读
  - 读：查
  - 写：添删改
  - `readOnly` 默认值false，表示可以查询、添加、修改、删除操作
  - `readOnly` 值为true后，只能查询
- `rollbackFor`：设置出现哪些异常进行事务**回滚**
- `noRollbackFor`：设置出现哪些异常**不**进行事务回滚



#### 4.5.1 事务传播行为

![image-20201015183416966](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221404492.png)

![image-20201015183454089](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221405362.png)



#### 4.5.2 事务隔离级别

1. **脏读**：A事务读取B事务还未提交的数据

   ![image-20211222144025423](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221440541.png)

2. **不可重复读**：前后多次读取同一数据的结果不一样

   ![image-20211222143200957](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221432049.png)

3. **幻读**：前后多次读取时的数据总量不一样

   ![image-20211222144151608](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221441702.png)

   

   **事务隔离级别**

![image-20201015223909643](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221428623.png)





### 4.5 基于XML的声明式事务操作

1. **配置事务管理器**

   - 创建事务管理器

     ```xml
         <!-- 1.事务管理器 -->
         <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
             <!--注入数据源-->
             <property name="dataSource" ref="dataSource"></property>
         </bean>
     ```

     

   - 引入名称空间 `tx`

     ```xml
     <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:context="http://www.springframework.org/schema/context"
            xmlns:aop="http://www.springframework.org/schema/aop"
            xmlns:tx="http://www.springframework.org/schema/tx"
            xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                                http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                                http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
                                http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">
     ```

     

   - 开启事务注解

     ```xml
         <!-- 开启事务注解 -->
         <tx:annotation-driven transaction-manager="transactionManager"></tx:annotation-driven>
     </beans>
     ```

     

   

2. **配置通知**

   ```xml
       <!-- 2.配置通知 -->
       <tx:advice id="txadvice">
           <tx:attributes>
               <tx:method name="accountMoney" propagation="REQUIRED"/>
           </tx:attributes>
       </tx:advice>
   ```

   

3. **配置切入点和切面**

   ```xml
       <!-- 3.配置切入点和切面 -->
       <aop:config>
           <!-- 切入点 -->
           <aop:pointcut id="pt" expression="execution(* com.event.service.UserService.*(..))"></aop:pointcut>
           <!-- 切面 -->
           <aop:advisor advice-ref="txadvice" pointcut-ref="pt"></aop:advisor>
       </aop:config>
   ```

   

4. **Service类中添加事务注解**

   - 添加在类上面，则是为所有方法添加注解

     ```java
     @Service
     @Transactional
     public class UserService {
     }
     ```

     

   - 添加在方法上面，则是为该方法添加注解



### 4.6 基于注解的声明式事务操作（⭐）

1. **创建配置类，代替xml配置文件**

   配置类放在`config`包的下面

   ```java
   @Configuration //配置类
   @ComponentScan(basePackages = "com.Transaction") //开启组件扫描
   @EnableTransactionManagement //开启事务
   public class TxConfig {
       //数据库连接池
       @Bean
       public DruidDataSource getDruidDataSource(){
           DruidDataSource dataSource = new DruidDataSource();
           dataSource.setDriverClassName("com.mysql.jdbc.Driver");
           dataSource.setUrl("jdbc；mysql//127.0.0.1:3306/test");
           dataSource.setUsername("root");
           dataSource.setPassword("admin");
           return dataSource;
       }
   
       //创建JdbcTemplete对象
       @Bean
       public JdbcTemplate getJdbcTemplate(DataSource dataSource){
           JdbcTemplate jdbcTemplate = new JdbcTemplate();
           jdbcTemplate.setDataSource(dataSource);
           return jdbcTemplate;
       }
   
       //创建事务管理器
       @Bean
       public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource){
           DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
           return dataSourceTransactionManager;
       }
   }
   ```

   

2. **Service类中添加事务注解**

   - 添加在类上面，则是为所有方法添加注解

     ```java
     @Service
     @Transactional
     public class UserService {
     }
     ```

     

   - 添加在方法上面，则是为该方法添加注解



3. **测试类**

   ```java
       @Test
       public void testTransaction(){
           ApplicationContext context = new AnnotationConfigApplicationContext(TxConfig.class);
           UserService userService = context.getBean("userService", UserService.class);
           userService.accountMoney();
       }
   ```

   



## 5 Spring5新功能

Spring5代码基于JDK8，兼容JDK9

### 5.1 整合了Log4j2

创建`Log4j2.xml`包

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
<!--Configuration后面的status用于设置log4j2自身内部的信息输出，可以不设置，当设置成trace时，可以看到log4j2内部各种详细输出-->
<configuration status="INFO">
    <!--先定义所有的appender-->
    <appenders>
        <!--输出日志信息到控制台-->
        <console name="Console" target="SYSTEM_OUT">
            <!--控制日志输出的格式-->
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </console>
    </appenders>
    <!--然后定义logger，只有定义了logger并引入的appender，appender才会生效-->
    <!--root：用于指定项目的根日志，如果没有单独指定Logger，则会使用root作为默认的日志输出-->
    <loggers>
        <root level="info">
            <appender-ref ref="Console"/>
        </root>
    </loggers>
</configuration>
```



### 5.2 整合了@Nullable注释

@Nullable 注解可以使用在方法、属性、参数上面，表示方法返回可以为空，属性值可以为空，参数值可以为空。

```java
// 注解用在方法上面，方法返回值可以为空
@Nullable
String getId();

// 注解使用在方法参数里面，方法参数可以为空
public <T. void registerBean(@Nullable String beanName){
    
}

// 注解使用在属性上面，属性值可以为空
@Nullable
private String bookName;
```



### 5.3 支持函数式风格GenericApplicationContext

```java
    @Test
    public void testGenericApplicationContext(){
        // 创建GenericApplicationContext对象
        GenericApplicationContext context = new GenericApplicationContext();
        // 调用context的方法对象注册
        context.refresh();
        context.registerBean(User.class,()->new User());
        // 获取在spring注册的对象
        User user = (User)context.getBean("cn.spring5.Aop.Test.User");
        System.out.println(user);
    }

```



### 5.4 整合了JUnit5

#### 5.4.1 JUnit4

第一步，引入JUnit4 相关依赖

第二步，创建测试类

```java
// 单元测试框架
@RunWith(SpringJUnit4ClassRunner.class)
// 加载配置文件
@ContextConfiguration("classpath:bean1.xml")
public class JTest4 {
    @Autowired
    private UserService userService;

    @Test
    public void test(){
        userService.accountMoney();
    }
}
```



#### 5.4.2 JUnit5

第一步，引入JUnit5 相关依赖

第二步，创建测试类

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:bean1.xml")
public class JTest5 {
    @Autowired
    private UserService userService;

    @Test
    public void test1(){
        userService.accountMoney();
    }
}
```



**可以用复合注解来替代上面的两个注解**

```java
@SpringJUnitConfig(locations = "classpath:bean1.xml")
```





## 6 Spring5新功能：Webflux

Spring 5框架

<img src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221712587.png" alt="image-20211222171246513" style="zoom: 80%;" />

### 6.1 概念

`SpringWebflux`用于web开发，使用了**流程响应式编程**的框架。

- 传统的web框架，比如`SpringMVC`，是基于`Servlet`容器的。

- `Webflux`是一种**异步非阻塞**框架，再`Servlet3.1`之后才支持。核心是基于`Reactor`相关的API实现的。

  > 异步非阻塞
  >
  > - 异步和同步：针对调用者。
  >
  >   同步：请求响应后，才能接着做下一步
  >
  >   异步：请求后，可以直接下一步
  >
  > - 非阻塞和阻塞：针对非调用者。
  >
  >   阻塞：被调用者受到请求之后，做完请求任务之后才给出反馈就是阻塞。
  >
  >   非阻塞：收到请求之后马上给出反馈然后再去做事情就是非阻塞。

![image-20211222175822231](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112221758281.png)



1. **特点**：

   - 非阻塞式：在有限资源下，提高系统吞吐量和伸缩性

   - 函数式编程：Java 8 函数式编程

     

2. **比较SpringMVC**

   ![image-20201017162720090](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112222046653.png)

   相同点：都可以使用注解方式。都可以运行在Tomcat等容器中。

   不同点：SpringMAC采用命令式编程，Webflux采用异步响应式编程



### 6.2 响应式编程

> 响应式编程式一种面向**数据流**和**变化传播**的编程范式。这意味着可以在编程语言中很方便地表达静态或动态的数据流二相关的计算模型会自动将变化的值通过数据流进行传播
>
> 例子: 电子表格程序就是响应式编程的一个例子。单元格可以包含字面值或类似"=B1+C1"的公式，而包含公式的单元格的值会依据其他单元格的值的变化而变化。

响应式编程涉及到 Java 的观察者模式.

1. Java 8 及其之前的版本: 提供的**观察者模式**的两个类`Observer`和`Observable`

   ```java
   public class ObserverDemo extends Observable {
       public static void main(String[] args) {
           ObserverDemo observer = new ObserverDemo();
           //添加观察者
           observer.addObserver((o, arg)->{
               System.out.println("发生了变化");
           });
   
           //添加观察者
           observer.addObserver((o, arg)->{
               System.out.println("收到被观察者的通知，准备改变");
           });
   
           //数据变化
           observer.setChanged();
           //通知数据变化
           observer.notifyObservers();
       }
   }
   ```

   

2. Java 9 及之后的版本: 提供的`Flow`
3. `reactor` 更加强大



### 6.3 Reactor实现响应式编程

#### 6.3.1 概述

- 响应式编程操作中，Reactor 是满足 Reactive 规范的框架

- Reactor两个核心类：

  - `Mono`：实现发布者，**返回 0 或 1 个元素**
  - `Flux`：实现发布者，**返回 N 个元素**

  这两个类都实现了接口 Publisher，并提供了丰富的操作符。



#### 6.3.2 代码实现

1. 引入依赖

   ```java
           <dependency>
               <groupId>io.projectreactor</groupId>
               <artifactId>reactor-core</artifactId>
               <version>3.1.5.RELEASE</version>
           </dependency>
   ```

   

2. 数据流**声明**代码演示

   ```java
   public class TestReactor {
       public static void main(String[] args) {
           // just方法直接传递
           Flux.just(1, 2, 3, 4);
           Mono.just(1);
   
           // 传递数组元素
           Integer[] array = {1, 2, 3, 4};
           Flux.fromArray(array);
   
           //传递集合元素
           List<Integer> list = Arrays.asList(array);
           Flux.fromIterable(list);
   
           //传递Stream流元素
           Stream<Integer> stream = list.stream();
           Flux.fromStream(stream);
       }
   }
   ```

   

3. 以上只是声明了数据流，想要发出的话，要进行订阅。

   ```java
           Flux.just(1, 2, 3, 4).subscribe(System.out::println);
           Mono.just(1).subscribe(System.out::println);
   ```

   

#### 6.3.3 三种信号

- `Flux`和`Mono`都是Stream的发布者，他们可以发布三种数据信号：
  - **元素值**
  - **错误信号**：中止stream时，会同时传递错误信息。
  - **完成信号**

- 三种信号特点
  - 错误信号和完成信号都是终止信号，用于告诉订阅者，这个stream已经结束。
  - 错误信号和完成信号不能同时存在。
  - 空数据流：没有发送任何元素值，而是直接发送错误信号或者完成信号
  - 无限数据流：没有错误信号，也没有完成信号



#### 6.3.4 操作符

- 操作符是对数据流进行的操作。

- 常见操作符：

  - map：把元素映射成新元素

    <img src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112231604390.png" alt="image-20211223160415238" style="zoom: 67%;" />

  - flatMap：把元素映射成流

  

<img src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112231606020.png" alt="image-20211223160538580" style="zoom:67%;" />





### 6.4 SpringWebflux

> SpringWebflux 基于Reactor，默认容器时Netty。
>
> Netty是高性能的异步非阻塞的 NIO 框架。

- BIO

  <img src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112231630780.png" alt="image-20201017211010599" style="zoom:50%;" />

- NIO

  <img src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112231632286.png" alt="img" style="zoom:50%;" />



#### 6.4.1 SpringWebflux 核心API

- SpringWebflux 核心控制器 DispatchHandler，实现接口 WebHandler

  - 接口 WebHandler 有一个方法

    ```java
    public interface WebHandler {
        Mono<Void> handle(ServerWebExchang var1)
    }
    ```

    底层实现：

    ![image-20201017214718714](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112231634296.png)

    

- SpringWebflux 里面 DispatcherHandler，负责请求的处理

  - `HandlerMapping`：查询处理请求的方法
  - `HandlerAdapter`：负责请求处理
  - `HandlerResultHandler`：响应结果处理

  

- SpringWebflux 实现函数式编程，提供两个接口

  - `RouterFunction`（路由处理）
  - `HandlerFunction`（处理具体函数）

#### 6.4.2 基于注解编程模型

SpringBoot 自动配置相关运行容器，默认情况下使用 Netty 服务器

演示：

1. 创建 SpringBoot 工程，引入 Webflux 依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-webflux</artifactId>
   </dependency>
   ```

   

2. 配置启动端口号

   ![image-20211223172232270](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112231722381.png)

3. 创建包和相关类

   ![image-20211223172353253](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112231723343.png)

   - entity.User

     ```java
     public class User {
         private String name;
         private String gender;
         private Integer age;
     
         public User(String name, String gender, Integer age) {
             this.name = name;
             this.gender = gender;
             this.age = age;
         }
     
         public String getName() {
             return name;
         }
     
         public void setName(String name) {
             this.name = name;
         }
     
         public String getGender() {
             return gender;
         }
     
         public void setGender(String gender) {
             this.gender = gender;
         }
     
         public Integer getAge() {
             return age;
         }
     
         public void setAge(Integer age) {
             this.age = age;
         }
     }
     ```

   - service.UserService

     ```java
     public interface UserService {
         /** 根据id查询用户*/
         Mono<User> getUserById(int id);
     
         /** 查询所有用户*/
         Flux<User> getAllUser();
     
         /** 添加用户*/
         Mono<Void> saveUserInfo(Mono<User> user);
     }
     ```

   - service.UserServiceImpl

     ```java
     public class UserServiceImpl implements UserService {
         /** 创建map集合存储数据*/
         private final Map<Integer,User> userMap = new HashMap<>();
     
         public UserServiceImpl() {
             this.userMap.put(1, new User("lucy","nan",20));
             this.userMap.put(2, new User("mary","nv",30));
             this.userMap.put(3, new User("jack","nan",40));
             this.userMap.put(4, new User("WeiSanJin","nan",50));
         }
     
         /** 根据id查询*/
         @Override
         public Mono<User> getUserById(int id) {
             return Mono.justOrEmpty(this.userMap.get(id));
         }
     
         /** 查询多个用户*/
         @Override
         public Flux<User> getAllUser() {
             return Flux.fromIterable(this.userMap.values());
         }
     
         /** 添加用户*/
         @Override
         public Mono<Void> saveUserInfo(Mono<User> userMono) {
             return userMono.doOnNext(person -> {
                 /* 向map集合里面放值*/
                 int id = userMap.size()+1;
                 userMap.put(id,person);
             }).thenEmpty(Mono.empty());
         }
     }
     ```

   - controller.UserController 

     ```java
     @RestController
     public class UserController {
         /** 注入service*/
         @Autowired
         private UserService userService;
     
         /** id查询*/
         @GetMapping("/user/{id}")
         public Mono<User> getUserId(@PathVariable int id){
             return userService.getUserById(id);
         }
     
         /** 查询所有*/
         @GetMapping("/user")
         public Flux<User> getUser(){
             return userService.getAllUser();
         }
     
         /** 添加*/
         @PostMapping("/saveUser")
         public Mono<Void> saveUser(@RequestBody User user){
             Mono<User> userMono = Mono.just(user);
             return userService.saveUserInfo(userMono);
         }
     }
     ```



- 说明：
  - SpringMVC 方式实现，**同步阻塞**的方式，基于 SpringMVC+Servlet+Tomcat
  - SpringWebflux 方式实现，**异步非阻塞**方式，基于 SpringWebflux+Reactor+Netty



#### 6.4.3 基于函数式编程模型

- 在使用函数式编程模型操作时候，需要自己初始化服务器

- 基于函数式编程模型时候，有两个核心接口：

  - `RouterFunction`（实现路由功能，请求转发给对应的 handler）
  - `HandlerFunction`（处理请求生成响应的函数）。

  核心任务定义两个函数式接口的实现并且启动需要的服务器。

- SpringWebflux 请 求 和 响 应 不 再 是 ServletRequest 和 ServletResponse ， 而 是ServerRequest 和 ServerResponse



步骤

1. 保留解编程模型工程中 entity 和 service 内容。

2. 创建 Handler

   ```java
   public class UserHandler {
       private final UserService userService;
       public UserHandler(UserService userService){
           this.userService = userService;
       }
   
       /** 根据id查询*/
       public Mono<ServerResponse> getUserById(ServerRequest request){
           /* 获取id*/
           int userId = Integer.valueOf(request.pathVariable("id"));
           /* 空值处理*/
           Mono<ServerResponse> notFound = ServerResponse.notFound().build();
           /* 调用service方法得到数据*/
           Mono<User> userMono = this.userService.getUserById(userId);
           /* userMono进行转换返回*/
           /* 使用Reactor操作符fluxMap*/
           return userMono
                       .flatMap(person -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                           .body(BodyInserters.fromObject(person)))
                           .switchIfEmpty(notFound);
       }
   
       /** 查询所有*/
       public Mono<ServerResponse> getAllUser(){
           /* 调用service得到结果*/
           Flux<User> userFlux = this.userService.getAllUser();
           return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(userFlux,User.class);
       }
   
       /** 添加*/
       public Mono<ServerResponse> saveUser(ServerRequest request){
           /* 得到user对象*/
           Mono<User> userMono = request.bodyToMono(User.class);
           return ServerResponse.ok().build(this.userService.saveUserInfo(userMono));
       }
   
   ```



3. 第三步 初始化服务器，编写 Router

   - 创建路由

     ```java
     public class Server {
         /** 创建Router路由*/
         public RouterFunction<ServerResponse> routerFunction(){
             /* 创建hanler对象*/
             UserService userService = new UserServiceImpl();
             UserHandler handler = new UserHandler(userService);
             /* 设置路由*/
             return RouterFunctions.route(
                     GET("/user/{id}").and(accept(APPLICATION_JSON)),handler::getUserById)
                     .andRoute(GET("/user").and(accept(APPLICATION_JSON)),handler::getAllUser);
     
         }
     }
     ```

   - 创建服务器，完成适配

     ```java
     public class Server {
         /** 创建服务器完成配置*/
         public void createReactorServer(){
             /*路由和handler适配*/
             RouterFunction<ServerResponse> route = routerFunction();
             HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
             ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
     
             /* 创建服务器*/
             HttpServer httpServer = HttpServer.create();
             httpServer.handle(adapter).bindNow();
         }
     }
     ```

   - 最终调用

     ```java
     public class Server {
         public static void main(String[] args) throws Exception {
             Server server = new Server();
             server.createReactorServer();
             System.out.println("enter to exit");
             System.in.read();
         }
     }
     ```

     

4. 使用 WebClient 调用

   ```java
   public class Client {
       public static void main(String[] args) {
           /* 调用服务器地址*/
           WebClient webClient = WebClient.create("http://127.0.0.1:8080");
   
           /* 根据ID查询*/
           String id = "1";
           User userResult = webClient.get().uri("/user/{id}",id)
                   .accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(User.class)
                   .block();
           System.out.println(userResult.getName());
   
           /* 查询所有*/
           Flux<User> results = webClient.get().uri("/user").accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(User.class);
           results.map(stu ->stu.getName()).buffer().doOnNext(System.out::println).blockFirst();
       }
   }
   ```

   

