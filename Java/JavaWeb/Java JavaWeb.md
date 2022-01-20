# JavaWeb

## 一、JQuery

### 函数

#### 核心函数 $

```javascript
$的四个作用:
1.
$(function(){}): 相当于window.onload = function(){}
2.
$(“html字符串”): 根据这个html字符串创建元素节点对象
3.
$(“选择器”): 根据这个选择器查找元素节点对象
4.
$(DOM对象): 将DOM对象包装为jQuery对象
-例如：var $btnEle = $(btnEle)
-声明一个变量指向jQuery对象，那么这个变量习惯上要以$开头
```



### 查阅chm文档

在E:\Files_Coding\Java\chm文档\jQueryAPI_1.7.1_CN.chm







## 二、XML

### 定义

1、作用：XML的<u>使命</u>，就是以一个统一的格式，组织有关系的数据，为不同平台下的应用程序服务。
2、定义：XML用来传输和存储数据，HTML用来显示数据

![image-20210912225740306](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210912225740306.png)

### 组成

```xml
<?xml version="1.0" encoding="UTF-8"?>
<student>
    <student id="1">
        <name>John</name>
        <gender>男</gender>
        <age>19</age>
    <student id="2">
        <name>James</name>
        <gender>男</gender>
        <age>22</age>
</student>
```

#### XML声明

<?xml version="1.0" encoding="UTF-8"?> 

#### CDATA区

<![CDATA[.......]]>

cdata区的内容不会被xml编译



### Java解析XML

<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110271052928.png" alt="image-20210912234210505" style="zoom:80%;" />



#### **DOM标准和SAX标准对比：**

<img src="https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210912234252498.png" alt="image-20210912234252498" style="zoom: 67%;" />



#### **dom4j**

​	dom4j是一个非常优秀的Java XML API，具有性能优异、功能强大和极易使用的特点。现在很多软件都采用dom4j，例如Hibernate。
使用dom4j开发，需导入dom4j相应的jar包dom4j-1.6.1.jar



#### 例子

```java
public void test2(){
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read("src/student.xml");
            //获取根标签
            Element root = doc.getRootElement();
            //获取根标签下的子标签
            List<Element> students = root.elements("student");
            for (Element stu:students) {
                //获取name属性
                Element nameElement = stu.element("name");
                //asXML(): 转换成XML字符串
                String nameXML = nameElement.asXML();
                //getText(): 获取标签中的文本内容
                String name = nameElement.getText();
                //elementText() 直接获取指定标签名的内容
                String gender = stu.elementText("gender");
                int age = Integer.parseInt(stu.elementText("age"));
                //attributeValue获取属性值
                int id = Integer.parseInt(stu.attributeValue("id"));

                System.out.println(new Student(id, name, gender, age));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
```

















## 三、Tomcat

### javaWeb知识体系

![image-20210913132215370](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210913132215370.png)

javaweb三大组件：Servlet程序、Filter过滤器、Listener监听器

静态页面：css，js，img，mp4...

动态页面：jsp，servlet



### Tomcat

#### tomcat的文件结构：

![image-20210913140319040](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210913140319040.png)

#### tomcat启动：

​	cmd——catalina run



#### tomcat项目布置

1. ##### 方式一：

   直接把项目放在\webapps下

2. ##### 方式二：

   在tomcat文件下的conf\catalina\localhost里创建xml文件：

   ①path: 工程访问路径

   ②docBase：工程目录在哪里

   ```
   <content path="\mypro" docBase="E:\developer_pro\mypro" />
   ```



#### IDEA动态Web工程目录

![image-20210913144547650](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210913144547650.png)













## 四、Servlet

1.Servlet是javaee的规范之一。是一个接口

2.javaweb三大组件：Servlet程序、Filter过滤器、Listener监听器

### 定义

serlvet是运行在**Web<u>服务器</u>**上的小型**java程序**，通过HTTP来接受和响应**Web<u>客户端</u>**的请求。

### 实现

#### 例子

##### 1.实现servlet程序

①方法一：使用IDEA创建Servlet程序（最方便，常用）

![image-20210914161036273](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210914161036273.png)



②方法二：继承HttpServlet类

​	a.继承HttpServlet类

​	b.重写doGet或doPost方法

​	c.到web.xml中配置servlet程序的访问



③方法三：实现servlet接口类

​	a.编写一个类区实现Servlet接口

​	b.实现**service**方法，处理请求，并响应数据

​	c.到web.xml中配置servlet程序的访问

##### 2.配置web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    <!--给Tomcat配置Servlet程序-->
    <servlet>
        <!--servlet-name: 给servlet程序取一个别名-->
        <servlet-name>HelloServlet</servlet-name>
        <!--servlet-class: servlet全类名-->
        <servlet-class>com.finn.servlet.HelloServlet</servlet-class>
    </servlet>

    <!--servlet-mapping: 给servlet程序配置访问地址-->
    <servlet-mapping>
        <!--servlet-name: 告诉服务器，我配置的地址给哪个servlet程序使用-->
        <servlet-name>HelloServlet</servlet-name>
        <!--
        url-pattern: 配置访问地址
        /hello: 表示http://ip:port/工程路径/hello
        -->
        <url-pattern>/hello</url-pattern>
    </servlet-mapping>
</web-app>
```



#### 原理：

##### client如何定位到servlet程序去执行

![image-20210914142710577](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210914142710577.png)









### Servlet生命周期

1. 执行servlet构造器方法
2. 执行init初始化方法
3. 执行service方法
4. 执行destroy方法

#### 	注意

​	**1、2、 只在创建servlet程序时调用时执行一次**

​	**3、每次访问都会调用**

​	**4、servlet停止的时候才会执行**







### Serlvet继承关系

![image-20210915135027697](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210915135027697.png)







### ServletConfig类

#### 定义

servlet配置信息。

#### 作用

1. 获取Servlet程序的别名servlet-name的值
2. 获取Servlet初始化参数init-param
3. 获取ServletContext对象

```java
        System.out.println("1. 获取Servlet程序的别名servlet-name的值: " + servletConfig.getServletName());
        System.out.println("2. 获取Servlet初始化参数init-param " + servletConfig.getInitParameter("url"));
        System.out.println("3. 获取ServletContext对象: " + servletConfig.getServletContext());
```

```java
        ServletContext servletContext = getServletConfig().getServletContext();
        System.out.println("context-param的username: " + servletContext.getInitParameter("username"));
        System.out.println("context-param的password: " + servletContext.getInitParameter("password"));
```

#### 注意

如果子类重写init初始化方法，要写上super.init(config);









### ServletContext类

#### 定义

1. ServletContext是一个接口，表示Servlet上下文对象。
2. 一个web工程，只有一个ServletContext对象实例。
3. ServletContext对象是一个域对象。
4. ServletContext对象在web工程部署启动时创建，在web工程停止时候销毁。

##### 什么是域对象？

域对象：可以像Map一样存取数据的对象。

域：指存取数据的操作范围。

方法：putAttribute(), getAttribute(), removeAttribute()



#### 作用

1. ##### 获取web.xml配置的上下文参数context-param

   ```xml
       <context-param>
           <param-name>username</param-name>
           <param-value>context</param-value>
       </context-param>
       <context-param>
           <param-name>password</param-name>
           <param-value>root</param-value>
       </context-param>
   ```

   ```java
           ServletContext servletContext = getServletConfig().getServletContext();
           System.out.println("context-param的username: " + servletContext.getInitParameter("username"));
           System.out.println("context-param的password: " + servletContext.getInitParameter("password"));
   ```

2. ##### 获取当前工作路径，格式：/工程路径

3. ##### 获取工程部署后在服务器上的绝对路径

   ```java
           System.out.println("当前工作路径： " + servletContext.getContextPath());
   		// "/"：表示http://ip:port/工程名/ （映射到IDEA代码的web目录）
           System.out.println("工程部署的路径是： " + servletContext.getRealPath("/"));
   		//E:\developer\JavaWeb\out\artifacts\servlet_war_exploded\css
   		System.out.println("工程部署的路径是： " + servletContext.getRealPath("/css"));
   		//E:\developer\JavaWeb\out\artifacts\servlet_war_exploded\img
   		System.out.println("工程部署的路径是： " + servletContext.getRealPath("/img"));
   ```

   **工程部的路径是： E:\developer\JavaWeb\out\artifacts\servlet_war_exploded\ “  这个路径是IDEA整合Tomcat后，拷贝的Tomcat副本内容。**

4. ##### 像Map一样存取数据

   域：整个web工程。









### HttpServletRequest类

#### 作用

向Tomcat服务器发送请求时，服务器会把请求过来的*HTTP协议信息*解析好封装到Request对象中，然后传递到service方法（doGet和doPost）中。

我们可以通过HttpServletRequest对象，来获取所有请求信息。

#### 常用的方法

![image-20210917134957746](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210917134957746.png)



![image-20211231140539665](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202112311405758.png)

[修改请求 - 廖雪峰的官方网站 (liaoxuefeng.com)](https://www.liaoxuefeng.com/wiki/1252599548343744/1328976435871777)



### HTTP请求和响应

#### GET

client向server要HTTP Header和Object。

1. 请求行

   * 请求的方式 GET

   * 请求的资源路径[+?+请求参数]

   * 请求的协议的版本号 HTTP/1.1

2. 请求头

   * key:value 组成——不同的键值对，表示不同的含义。


##### 常见的GET请求：

1. form标签 method=get

2. a标签

3. link标签引入css

4. Script标签引入js文件

5. img标签引入img文件

6. iframe标签引入html文件

7. 浏览器输入地址后回车


#### POST

client向server发数据。

1. 请求行

   * 请求的方式 POST

   * 请求的资源路径[+?+请求参数]

   * 请求的协议的版本号 HTTP/1.1

2. 请求头

   * key:value 组成——不同的键值对，表示不同的含义。

     **<font color='red'>空行</font>**

3. 请求体 ==>发送给服务器的数据

##### **常见的POST请求：**

1. form标签 method=post



**POST和GET常见的请求头：**

* Accept: 表示客户端可以接受的数据类型

* Accept-Language: 表示客户端可以接收的语言类型

* Referer：表示请求发送时，浏览器地址栏中的地址

* User-Agent：浏览器信息

* Host：请求的端口名和端口号

  

#### 响应HTTP协议格式

1. 响应行

   * 响应的协议和版本号（HTTP/1.1）
   * 响应的状态码     （比如200）
   * 响应的状态描述符 （OK）

2. 响应头

   * key：value

     **<font color='red'>空行</font>**

3. 响应体 ==> 回传给客户的数据

##### 常见的状态码

1. 200	请求成功
2. 302    请求重定向
3. 404    请求服务器收到了， 但是数据不存在（请求地址错误）
4. 500    表示服务器收到请求，但是服务器内部错误（代码错误）



#### MIME类型

MIME就是HTTP中数据协议。比如.avi, .html, .txt, .mpg等等。









### 请求转发

 Servlet之间可以共同完成任务。从Servlet1到Servlet2的操作叫请求转发。

#### 特点

1. 地址栏不会变化

2. client只有一次请求

3. 会共享Request域中的数据

4. 可以转发到WEB-INF下的资源

   

**注意：其中，WEB-INF是浏览器无法直接访问的，但是却可以通过<u>请求转发的方法访问</u>到。**

#### base标签

当前页面中所有相对路径工作时，会参照base指定的路径来进行跳转。

这样可以避免请求转发的时候，跳转到错误的路径。

##### java中路径

.   当前目录

..  上一级目录

##### javaWeb中 ''/'' 的不同意义

1. 浏览器解析时，‘/’是 http://ip:port/

2. 服务器解析时，‘/’是 http://ip:port/工程路径

   * ```xml
     <url-pattern>/hello</url-pattern>
     ```

   * ```java
     servletContext.getRealPath("/");
     ```

   * ```java
     request.getRequestDispatcher("/");
     ```

3. 特殊情况：

   ```java
   response.sendRediect("/");  //把斜杆发送给浏览器解析。得到http://ip:port/
   ```

   









### HttpServletResponse类

#### 作用

1. Tomcat服务器收到请求时，服务器会创建一个response对象，然后传递给servlet程序。
2. HttpServletResponse类可以设置响应给客户端的信息。
3. HttpServletResponse类通过**<font color="red">流</font>**响应信息。

#### 输出流

1. 字节流 getOutputStream(); //常用于下载（传递二进制数据）
2. 字符流 getWriter(); //常用于回传字符串（常用）

**两个流只能同时使用一个**

```java
public class ResponseIOServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setCharacterEncoding("UTF-8");//设置【服务器】字符集
//        resp.setHeader("Content-Type", "text/html; charset=UTF-8");//设置【浏览器】字符集
        resp.setContentType("text/html; charset=UTF-8");//设置服务器和浏览器用的字符集
        PrintWriter writer = resp.getWriter();
        writer.write("Hello, there");
        writer.write("窝嫩爹！");
    }
}
```



### 请求重定向

客户端请求的地址可能废弃，服务器告诉客户端新的地址。

#### 用法

##### 方法1.（常用）

```java
public class Response1 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect("http://127.0.0.1:8080/servletExec/response2");
    }
}        
```

##### 方法2.

```java
public class Response1 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //302: 表示重定向
        //设置Header中Location，指明要重定向的地址
        resp.setStatus(302);
        resp.setHeader("Location", "http://127.0.0.1:8080/servletExec/response2");
    }
}
```

#### 特点

1. 地址栏发生变化
2. client有两次请求
3. 不会共享Request域中的数据
4. 不能访问WEB-INF下的资源



**注意区分请求转发和请求重定向的特点**













## 五、JSP

### 定义

JSP: java server pages。

JSP是Java的服务器页面。



### 作用

代替servlet程序回传html页面的数据。因为servlet程序回传html页面数据的开发成本和维护成本很高。



### 如何访问

jsp页面和html页面一样，放在web目录下。



### JSP本质

jsp本质是servlet程序。

当我们第一次访问jsp页面。Tomcat服务器会把jsp页面翻译成java源文件，并编译成.class字节码文件。



### Page指令



### 常用脚本

#### 声明脚本

```jsp
<%!声明脚本%>
```

可以给jsp翻译出来的java类定义属性和方法，甚至是静态代码、内部类等。

#### 表达式脚本

```jsp
<%=表达式%>
```

可以输出数据： 整型( 比如<%=12 %> )、浮点型、字符串、对象。

#### 代码脚本

```jsp
<% java语句 %>
```

可以在jsp中编写自己需要的功能（java语句）。



### 注释

<%--  注释内容  --%>



### JSP九大内置对象

1. request: 请求对象
2. response: 响应对象
3. pageContext: 会话对象
4. session: 会话对象
5. application: ServletContext对象
6. config: ServletConfig对象
7. out: jsp输出流对象
8. page: 指向当前jsp的对象
9. 设置成errorPage后的exception异常对象



### JSP四大域对象

1. pageContext (PageContextImpl类)：当前jsp页面范围有效
2. request (HttpServletRequest类)：一次请求有效
3. session (HttpSession类)：一个会话范围有效（打开浏览器访问服务器，直到关闭服务器）
4. application (ServletContext类)：整个web工程有效（数据存在，直到web工程停止）

 

### JSP输出

1. out.write(): 
2. response.getWriter(): 

![image-20210917191017212](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210917191017212.png)

一般情况，都用out进行输出，以避免页面输出内容打乱。



### JSP常用标签

1. 静态包含
2. 动态包含
3. 请求转发





## 六、EL表达式

EL: Expression Language。

主要是代替jsp页面中的<u>表达式脚本</u>在jsp页面中进行数据的输出。





## 七、JSTL表达式

JSTL: JSP Standard Tag Library是一个不断完善的开放源代码JSP标签库。

主要是代替jsp页面中的<u>代码脚本</u>。





## 八、文件的上传和下载

### 1. 文件的上传

1. form标签，设置method="post" encType="multipart/form-data"

2. input标签, type="file"

   ```jsp
   <form action="" enctype="multipart/form-data">
       username: <input type="text" name="username">
       头像: <input type="file" name="photo">
       <input type="submit" value="上传">
   </form>
   ```

3. 编写服务器代码接收，处理上传数据

   ![image-20210919151017492](E:\Typora_Documents\JavaWeb.assets\image-20210919151017492.png)

```java
public class UploadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.判断上传的数据是否是多段数据（多段数据才是上传的文件）
        if (ServletFileUpload.isMultipartContent(req)) {
            //创建FileItemFactory工厂实现类
            FileItemFactory fileItemFactory = new DiskFileItemFactory();//DiskFileItemFactory是个是实现类
            //创建用于解析上传数据的工具类ServletFileUpload
            ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
            try {
                //解析上传的数据，得到每一个表单项FileItem
                List<FileItem> list = servletFileUpload.parseRequest(req);
                //判断每一个表单项：①普通类型 ②上传的文件
                for (FileItem fileItem : list) {
                    if (fileItem.isFormField()) {
                        //普通表单项
                        System.out.println("表单项的name: " + fileItem.getFieldName());
                        System.out.println("表单项的value: " + fileItem.getString("UTF-8"));
                    } else{
                        //上传的文件
                        System.out.println("表单项的name: " + fileItem.getFieldName());
                        System.out.println("文件名: " + fileItem.getName());

                        fileItem.write(new File("\\src\\" + fileItem.getName()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```



HTTP协议内容：

![image-20210917195838307](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210917195838307.png)



### 2. 文件的下载

![image-20210919151613262](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210919151613262.png)



![image-20210919151017492](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210919151017492.png)

```java
public class Download extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取要下载的文件名
        String fileName = "1.jpg";

        //2.server读取要下载的文件内容(ServletContext对象读取)
        ServletContext servletContext = getServletContext();
        InputStream resourceAsStream = servletContext.getResourceAsStream("/file/" + fileName);//server中，'/'表示http://ip:port/工程名/ 映射到web目录

        //3.回传前，通过响应头告诉client返回的数据类型
        String mimeType = servletContext.getMimeType("/file/" + fileName);
        resp.setContentType(mimeType);

        //4.告诉client收到的数据用于下载
            //Content-Disposition: 响应头，表示收到的数据怎么处理
            //attachment;filename: 附件和要下载的文件名
            //如果文件名有中文，则使用url.encode()进行转换
        resp.setHeader("Content-Disposition", "attachment;filename" + fileName);

        //5.文件内容回传给client
        OutputStream outputStream = resp.getOutputStream();
            //读取数据，复制给输出流
        IOUtils.copy(resourceAsStream, outputStream);
    }
}
```













## 九、Cookie

### 1. 定义

1. Cookie：Server通知Client保存键值对的技术。
2. Client有了Cookie后，每次请求都发送给服务器。
3. 每个Cookie不超过4kb。

**实际应用场景：免用户名登录。**



### 2. 创建

1.创建Cookie对象cookie

2.resp.addCookie( cookie );

![image-20210919210941726](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210919210941726.png)

可以同时创建多个Cookie



### 3. Cookie的获取

Server获取Client的Cookie只需：req.getCookies( );//返回Cookie[]

![image-20210919212026737](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210919212026737.png)

### 4.Cookie的修改

方法一：覆盖原Cookie对象

方法二：cookie.setValue( )



### 5.Cookie生命控制

决定Cookie最大生存时间, 单位：s。 （以**格林时间**计时）

**setMaxAge()**

* 正值：指定秒后过期
* 负值：关闭浏览器后Cookie就删除（一般用-1）
* 0：马上删除Cookie



### 6.有效路径path属性的设置

Cookie的path属性可以用来过滤，以决定哪些Cookie发给服务器，哪些不发。

举例：

![image-20210924163523154](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210924163523154.png)











## 十、Session

### 1. 定义：

1) Session用于维护一个客户端和服务器之间关联的技术。每个客户端都有自己的一个Session会话。
2) Session会话中，我们经常用来保存用户登陆后的信息。
3) Java中Session是一个接口（HttpSession）。

### 2. 创建

**request.getSession( )**:

1. 第一次调用：创建Session会话
2. 之后的调用：获取Session

**isNew( )**: 判断会话是不是刚创建出来的

**getId( )**: 得到会话ID值（ID唯一）。

![image-20210924170055897](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210924170055897.png)



### 3. 域中数据的存取

1. req.getSession().setAttribute("key", value);
2. req.getSession().getAttribute(key);



### 4. Session生命周期

默认生存时长为30分钟。

1. public void setMaxInactiveInterval(int interval)    设置生存时长。单位：秒
   * 正数：超时时长
   * 负数：永不超时（几乎不用）
2. public void getMaxInactiveInterval(int interval)    获取生存时长。单位：秒

 超时：指的是client两次请求的最大间隔时长。



### 5.Session和浏览器之间的关联原理

![image-20210924190627229](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210924190627229.png)













## 十一、Filter过滤器

### 1. 定义

a）JavaWeb三大组件：Servlet程序、Listener监听器、Filter过滤器

b）Filter过滤器是JavaEE规范，也是接口

c）作用：拦截请求、过滤响应

### 2、Filter生命周期

1. 构造器方法
2. init()
3. doFilter()
4. destroy()

1.2.步在web启动时执行

3.每次请求拦截到请求，会执行

4.停止web工程时，会执行

### 3、FilterConfig类

Filter配置文件的配置信息

作用：

1. 获取Filter的名称 filter-name的内容
2. 获取在Filter中配置的init-param初始化参数
3. 获取ServletContext对象

### 4、FilterChain过滤器链

![image-20210924211531910](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/image-20210924211531910.png)

### 5、Filter的拦截路径

1. 精确匹配： http://ip:port/工程路径/target.jsp
2. 目录匹配： http://ip:port/工程路径/admin/*
3. 后缀名匹配： http://ip:port/工程路径/admin/*.html













## 十二、json

json存在有两种形式：

1. json对象：用于操作Json中的数据
2. json字符串：用于Client和Server之间的数据交换

两者可以相互转换。

### 1. json在JavaScript中的使用

##### 两个常用方法

1. JSON.stringify()：json对象转成json字符串
2. JSON.parse()：json字符串转成json对象

### 2. json在Java中的使用

##### 用**gson.jar**进行三种转换

```java
Person person = new Person();

Gson gson = new Gson();
```



1. JavaBean和json

   * javaBean—>json: 

     ```java
         //gson.toJson()
         String personJsonString = gson.toJson(person);
     ```

   * json—>javaBean

     ```java
         //gson.fromJson()
         Person person = gson.fromJson(personJsonString, Person.class);
     ```

     

2. List和json

   ```java
       List<Person> personList = new ArrayList<>();
       personList.add(new Person());
   
       //gson.toJson()
       String listJsonString = gson.toJson(personList);
   
       //gson.fromJson()
       List<Person> list1 = gson.fromJson(listToJson, new TypeToken<ArrayList<Person>>(){}.getType());
       for (Person p:list1) {
           System.out.println(p);
       }
   ```

   

3. map和json

   ```java
       @Test
       public void test2(){
           Map<Integer, Person> personMap = new HashMap<>();
           personMap.put(1, new Person(1, "James"));
           personMap.put(2, new Person(2, "Finn"));
   
           Gson gson = new Gson();
           //map转json
           String mapToJson = gson.toJson(personMap);
   
           //json转map
           Map<Integer, Person> newMap = gson.fromJson(mapToJson, new TypeToken<HashMap<Integer, Person>>(){}.getType());//匿名内部类
       }
   ```











## 十三、AJAX

### 概念

AJAX： **A**synchronous **J**avascript **A**nd **X**ML（异步Javascript和XML）。是一种创建交互式网页应用的网页开发技术。

**<font color="red">AJAX是一种浏览器通过js异步发起请求。局部更新页面的技术。</font>**

### AJAX局部更新

1. 地址栏不会变化
2. 不会舍弃原来页面内容

### jQuery中AJAX请求













## 十四、i18n（国际化）

### 1.是什么？

国际化（internationalization，简写i18n）：同一个网站可以支持多种不同的语言

### 2.三要素

#### a) Locale对象

Locale表示不同的时区、位置、语言

zh_CN: 中国，中文

en_US: 美国，英文

#### b) Properties属性配置文件

i18n配置文件命名规则：baseName_locale.properties

中文配置文件：i18n_zh_CN.properties

英文配置文件：i18n_en_US.properties

#### c) ResourceBundle资源包

```java
//根据给定的baseName和Locale读取响应的配置文件，得到文字信息
ResourceBundle.getBundle(); 
//得到不同国家的yuyanxinxi
ResourceBundle.getString(key);
```

