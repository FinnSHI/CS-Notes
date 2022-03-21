# Spring Boot

## Spring Boot 启动流程

1. 执行main方法的 `run()`, run方法会做IOC操作。Spring Boot不会走配置文件初始化，而是走注解初始化。
2. 我们会讲当前Java配置类的类对象传递进去，然后走到SpringBootApplication注解
3. 起作用的注解是@EnableAutoConfiguration注解，这个注解会去加载sprint-boot-autoconfiguration包下META-INF/spring.factories文件
4. 读取CondidatesConfiguration（候选配置）：org.springframework.boot.autoconfigure.EnableAutoConfiguration下133个类全路径。但是候选配置不会全部加载到内存。
5. 然后调用this.getConfigurationClassFilter()来进行条件筛选，filter出没有引入的依赖包。
6. 剩下的配置会加载到内存。
7. 同时，我们还有一些环境配置，会在`run()`中重加载。



### Configuration

#### Auto Configuration核心

1. 读取CondidatesConfiguration：sprint-boot-autoconfiguration包下META-INF/spring.factories的org.springframework.boot.autoconfigure.EnableAutoConfiguration下133个类全路径。
2. 然后调用this.getConfigurationClassFilter()来进行条件筛选，filter出没有引入的依赖包。
3. 剩下的配置会加载到内存。



## Spring Boot 注解

### @import

1. 静态导入

   ```java
   @Import({Person.class, User.class})
   ```

   

2. 动态导入：自定义ImportSelector实现类

   ```java
   public class MyImportSelector implements ImportSelector {
   	@Override
   	public String[] selectImports(...) {
           
   		return new String[]{Logger.class.getName(), Cache.class.getName()};
   	}
   
   }
   ```

   

   ```java
   @Import(MyImportSelector.class)
   ```

   

3. 动态导入：自定义ImportBeanDefitionRegistrar实现类

   ```java
   public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
       @Override
   	public void registerBeanDefinitions(..., BeanDefinitionRegistry registry) {
           RootBeanDefition rootBeanDefition = new RootBeanDefinition(Cache.class);
           registry.registerBeanDefinition("cache", rootBeanDefinition);
       }
   }
   ```

   ```java
   @Import(MyImportBeanDefinitionRegistrar.class)
   ```



## Spring Boot 自动装配原理



## Spring Boot 内置Tomcat原理



## Spring Boot 启动事件





## Spring Boot 服务通信

### Netty



