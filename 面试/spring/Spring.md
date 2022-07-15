# Spring

## Spring代理

### 流程

1. 初始化ProxyFactory
2. 获取BeanFactory中所有实现了Advisor接口的Bean
3. 然后获取BeanFactory中所有标注了@Aspect注解的Bean
4. 解析Bean中所有的切面逻辑

### 初始化ProxyFactory

```java
protected Object createProxy(
        Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {
        //specificInterceptors： 拦截器
        //代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.copyFrom(this);
        //判断是否targetClass代理不是接口代理
        if (!proxyFactory.isProxyTargetClass()) {
            if (shouldProxyTargetClass(beanClass, beanName)) {
                proxyFactory.setProxyTargetClass(true);
            } else {
                //如果是接口代理，需要添加接口属性
                evaluateProxyInterfaces(beanClass, proxyFactory);
            }
        }
        //适配器模式把拿到的拦截器统一封装成Advisor
		Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
		for (Advisor advisor : advisors) {
			proxyFactory.addAdvisor(advisor);
		}
		//创建代理
		return proxyFactory.getProxy(getProxyClassLoader());
    }
```

#### 拦截器封装为Advisor的适配处理

```java
	@Override
	public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
		if (adviceObject instanceof Advisor) {
			return (Advisor) adviceObject;
		}
		if (!(adviceObject instanceof Advice)) {
			throw new UnknownAdviceTypeException(adviceObject);
		}
		Advice advice = (Advice) adviceObject;
		if (advice instanceof MethodInterceptor) {
			// So well-known it doesn't even need an adapter.
			return new DefaultPointcutAdvisor(advice);
		}
		for (AdvisorAdapter adapter : this.adapters) {
			// Check that it is supported.
			if (adapter.supportsAdvice(advice)) {
				return new DefaultPointcutAdvisor(advice);
			}
		}
		throw new UnknownAdviceTypeException(advice);
	}
```



## Spring注解

### @Autowired和@Resource的区别

对于一个 bean 对象：

```xml
<!-- 
	Spring根据bean标签来创建对象，bean标签里添加相应的属性。 
		常用：
    	1) id：唯一标识
    	2) class：创建对象的类型路径
    创建对象的时候，默认执行无参数构造器
    	如果类里面声明了有参构造器，却没有声明无参构造器，则运行会报错。
-->

<bean id="book" class="com.finn.object.Book">
    <!--通过setter来注入属性-->
    <property name="bookName" value="天龙八部"></property>
</bean>
```

- 这里就创建了一个id或name为book，类型为com.finn.object.Book类型的bean

鉴于此：

#### @Autowired

1. 属于Spring框架

2. 默认使用类型（byType）进行注入

   ```java
   @Autowired   
   public IUserService userService  
   ```

   比如这里，会按照接口类型IUserService进行注入，

   - 如果这个接口只有<u>一个实现类</u>，那么会正常注入

   - 如果这个接口有<u>多个实现类或没有实现类</u>，则会报错

     ```java
     @Autowired(required = false)
     public IUserService userService  
     ```

​				可以指定required = false，这样，即使找不到bean也不会报错。



3. @Autowired 和 @Qualifier 结合使用可以使 @Autowired 变为按照 byName 来注入。@Qualifier可以指定要注入的变量名。
   - 这样，spring就会根据bean的id来进行注入。



#### @Resource

1. 属于J2EE，但是Spring支持该注解

2. @Resource默认按byName自动注入

3. @Resource可以指定 name 或者 type

   如果使用name属性，则使用byName的自动注入策略，而使用type属性时则使用byType自动注入策略。如果既不指定name也不指定type属性，这时将通过反射机制使用byName自动注入策略。

   ![image-20220516135139586](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202205161352735.png)

   
