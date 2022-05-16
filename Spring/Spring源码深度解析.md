# Spring源码深度解析

## 1 Spring整体架构和环境搭建

### 1.1 Spring的整体架构

![image-20220331174118304](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202203311741433.png)

1. Core Container

   Core和Beans是框架的基础部分，提供IOC和依赖注入。

   - Core：核心工具类
   - Beans：IOC/DI操作

2. Data Access/Integration
   - JDBC
   - ORM：对象-关系映射API，如JPA、JDO、Hibernate、iBatis 等，提供了 一个交互层。
   - OXM：Object/XML映射实现的抽象层。
   - JMS（Java Messaging Service）制造和消费消息的特性。
   - Transaction：事务管理

3. Web：Web 模块简化了处理大部分请求以及将请求参数绑定<u>域对象</u>的工作。

   - Web：基础的面向Web的继承特性
   - Web-Servlet：MVC的实现
   - Web-Struts：对Struct的支持，在Spring 3.0中弃用
   - Web-Porlet：用于Portlet环境和 Web-Servlet 模块的 MVC 的实现


4. AOP：面向切面。它让你可以定义例如方法拦截器和切点，从而将逻辑代码分开，降低它们之间的耦合性。
5. Test：支持JUnit和TestNG。





## 2 容器的基本实现

### 2.1 Spring 的结构组成

#### 2.1.1 beans 包的层级结构

#### 2.1.2 核心类的介绍

1. **DefaultlistableBeanFactory**：XmlBeanFactory 继承自 DefaultListableBeanFactory，它整个bean加载的核心部分。
1. **XmlBeanDefinitionReader**：读取XML配置文件



#### 2.1.3 容器的基础 XmlBeanFactory

```java
BeanFactory bf = new XmlBeanFactory(new ClassPathResource("beanFactoryTest.xml"));
```

在 Java 中，将不同来源的资源抽象成 URL ，通过注册不同的 handler ( URLStreamHandler )  来处理不同来源的资源的读取逻辑。

在Spring 中，也实现了自己的 Resource 接口。对不同来源的资源文件都有相应的 Resource 实现：文件（ FileSystemResource Class path 、资源（ ClassPathResource ）、 URL 资源（ Ur!Resource ）、 InputStream 资源（ InputStreamResource）、Byte 数组（ ByteArrayResource ）等

```java
public interface putStrearnSource ( 
    InputStcearn getinputStrearn() throws IOException;
    public interface Resource extends InputStrearnSource ( 
    // 存在性
    boolean exists (); 
    // 可读性
    boolean isReadable() ; 
    // 开放性
    boolean isOpen()； 
    URL getURL() throws IOException ; 
    URI getURI() throws IOException ; 
    File getFile() throws IOException ; 
    long lastModified() throws IOException ; 
    Resource createRelative (S tring relativePath) throws IOException ; 
    String getFilenarne() ; 
    String getDescription() ; 
}
```



- **XmlBeanFactory**

  ```java
  public XmlBeanFactory(Resource resource) throws BeansException ( 
      // 调用 XmlBeanFactory (Resource, BeanFactory ）构造方法
      this.(resource , null);
  }
      
  // 构造函数内部再次调用内部构造函数：
  // parentBeanFactory 为父类 BeanFactory 用于 factory 合并，可以为空
  public XmlBeanFactory(Resource resource , BeanFactory parentBeanFactory) throws BeansException｛
      super(parentBeanFactory); 
      this.reader.loadBeanDefinitions(resource);
  }
  ```

  **核心**：this.reader.**loadBeanDefinitions(resource);**

