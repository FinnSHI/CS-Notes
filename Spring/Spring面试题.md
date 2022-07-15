# Spring面试题

## Spring 5

### Autowired和Resource关键字的区别？

#### @Autowired

1. 由 spring 提供得注解，在包 org.springframework.beans.factory.annotation.Autowired；

2. 默认是 byType 进行注入。

   - 如果在接口上用@Autowired，Spring 会找到该接口的实现类，并将其注入。当存在多个实现类时，应该通过 byName 的形式注入，（使用@Qualifier 或者 @Resource）

3. 如果想 byName 进行注入，需要加入注解 @Qualifier，比如：

   ```java
   public class TestServiceImpl {
       @Autowired
       @Qualifier("userDao")
       private UserDao userDao;
   }
   ```

3. 默认对象必须存在，如果对象可以为null，则需要添加 `(required=false)`

#### @Resource

1. 由 j2EE 提供，但是 spring 也可以支持。在包 javax.annotation.Resource；

2. 默认通过反射机制由 byName 进行注入。

3. 也可以指定 byName 还是 byType

   ```java
   public class TestServiceImpl {
       // 下面两种@Resource只要使用一种即可
       @Resource(name="userDao")
       private UserDao userDao; // 用于字段上
   
       @Resource(name="userDao")
       public void setUserDao(UserDao userDao) { // 用于属性的setter方法上
           this.userDao = userDao;
       }
   }
   ```

   ①如果同时指定了name和type，则从Spring上下文中找到唯一匹配的bean进行装配，找不到则抛出异常。 

   ②如果指定了name，则从上下文中查找名称（id）匹配的bean进行装配，找不到则抛出异常。

   ③如果指定了type，则从上下文中找到类似匹配的唯一bean进行装配，找不到或是找到多个，都会抛出异常。 

   ④如果既没有指定name，又没有指定type，则自动按照byName方式进行装配；如果没有匹配， 则回退为一个原始类型进行匹配，如果匹配则自动装配



### 依赖注入的方式有几种，各是什么?

1. 构造器注入

   - 将被依赖对象通过构造器注入给依赖对象，并且在初始化对象的时候注入

     ```xml
         <bean id="userDaoImpl" class="dao.UserDAOImpl"/>
         <bean id="construct" class="ConstructDi.UserServiceImpl">
             <constructor-arg ref="userDaoImpl"/>
         </bean>
     ```

     

2. setter方法注入

   - IOC Service Provider 在通过调用无参构造器或无参 static 工厂进行 bean 的实例化后，再调用 bean 对象提供的 setter 函数进行依赖注入。

     ```java
     public class UserServiceImpl {
         private UserDAO userDAO;
     
         public void setUserDAO(UserDAO userDAO) {
             this.userDAO = userDAO;
         }
     
         public void getUser() {
             userDAO.getUser();
         }
     }
     ```

     ```xml
         <bean id="a" class="dao.UserDAOImpl"/>
     
         <bean id="hello" class="service.UserServiceImpl" >
             <property name="userDAO" ref="a"/>
         </bean>
     ```

     

3. 注解注入

   @Autowired、@Resource

   

4. 接口注入

   ```java
   public class ClassA {  
       
     private InterfaceclzB clzB;  
       
     public void doSomething() {  
         // 先获得接口b的实现类实例的Object对象
         Ojbect obj = Class.forName(Config.InterfaceClzBImpl).newInstance();  
         // 将Object对象强转成接口b类型来使用
         clzB = (InterfaceclzB)obj;  
         clzB.doIt();
     } 	
   ```

   

### SpringMVC的理解

#### MVC是什么

![image-20220624135324377](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206241353461.png)

##### M：model

service + dao + entity：完成业务逻辑

##### V：view

界面展示

##### C：Controller

controller：接受请求，调用 model，再根据结果展示 view



#### 常用注解

1. @Controller

2. @RequestMapping：将url映射到对应的类或处理方法上

3. @RequestBody：将前端传进来的json数据转为java对象。

4. @ResponseBody：将java对象转为json

   

5. @PathVariable：将值填充进 url 模板

6. @RequestParam：从request中拿出取值

比如：`http://localhost:8080/hello/101?param1=10&param2=20`

```java
@RequestMapping("/hello/{id}")
	public String getDetails(@PathVariable(value="id") String id,
                         	@RequestParam(value="param1", required=true) String param1,
                         	@RequestParam(value="param2", required=false) String param2){
        // do something....
}
```



### Spring AOP理解

- AOP：面向切面编程。它可以将与业务无关，但是为业务模块所共同调用的逻辑封装起来，降低模块间耦合度，并利于后期拓展。

- 应用场景：
  1. 事务处理
  2. 日志通知
  3. 权限控制

- AOP 基于动态代理实现。

  - 如果被代理的对象实现了接口，那么Spring使用JDK动态代理被代理的对象。

    - 代理对象代理UserDaoImpl，当UserDaoImpl想添加新功能时，可以通过代理对象来在原有 login() 基础上增加新的功能。
    - 代理对象内有增强的方法，可以在UserDaoImpl的login( )方法前后调用其他的方法。

    ![image-20220627152453453](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206271524521.png)

    ```java
    //增强的逻辑
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //方法之前执行
        System.out.println("当前方法是："method.getName() + "，参数是: " +
        Arrays.toString(args));
        //执行本身的方法
        Object res = method.invoke(obj, args);
        //方法之后执行
        System.out.println("方法之后执行" + res);
    	return res;
    }
    ```

    

  

  - 如果被代理的对象没有实现接口，那么Spring使用CGlib来创建当前类的子类来代理对象。

  ![image-20220627152514062](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202206271525117.png)

​	

#### Spring AOP和Aspectj AOP的区别

- spring AOP 是运行时增强，而Aspectj AOP是编译时增强。
- spring aop 是基于代理实现，而 aspectj aop是基于字节码实现

- spring aop已经集成了aspectj aop，而aspectj aop功能更加强大。
- 如果切面不多，则二者性能差不多；如果切面较多，则选择aspectj aop。



#### AOP术语

1. 连接点：可以被增强的方法，对应到某个程序执行的具体位置。如果方法调用前、调用后、方法捕获到异常登。
2. 切入点：真正被增强的方法。
3. 通知：实际增强的逻辑部分
   - 前置通知
   - 后置通知
   - 环绕通知
   - 异常通知
   - 最终通知
4. 切面：把通知应用到切入点的过程。
5. 横切关注点：从方法中抽取出来的非核心部分。



### Spring IOC的理解

IOC就是控制反转，就是将对象的创建交给spring的容器去管理，spring容器会使用java的<u>反射</u>机制，根据配置文件，在<u>运行时</u>动态的去创建实例和管理实例之间的依赖关系。