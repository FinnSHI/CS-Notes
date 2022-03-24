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

