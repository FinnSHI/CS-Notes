# my-spring

## 01 创建简单的 Bean 容器

![image-20220718141947032](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207181419088.png)

### BeanDefinition

```java
public class BeanDefinition {

    private Object bean;

    public BeanDefinition(Object bean) {
        this.bean = bean;
    }
    
    public Object getBean() {
        return bean;
    }
}
```



### BeanFactory

```java
public class BeanFactory {

    /*
    * ConcurrentHashMap: (Bean的name, BeanDefinition)
    * */
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /*
    * 获取bean
    * */
    public Object getBean(String name) {
        return beanDefinitionMap.get(name).getBean();
    }

    /*
    * 注册bean
    * */
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
    }
}
```



## 02 实现 Bean 的定义、注册、获取

### 目标

- **这一次我们把 Bean 的创建交给容器**，而不是我们在调用时候传递一个实例化好的 Bean 对象。
  - 这时候，Bean就不是Object类型了，而是一个Class类型。

### 设计

![image-20220718142406836](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207181424902.png)



![image-20220718143249378](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207181432441.png)

Bean 注册的时候**只注册一个类Class信息**，而不会直接把实例化信息注册到 Spring 容器中。

1. 定义 `BeanFactory` 的抽象类 `AbstractBeanFactory`，并且实现模板模式，统一通用核心方法的调用逻辑和定义。
2. 在继承抽象类 `AbstractBeanFactory` 后的 `AbstractAutowireCapableBeanFactory` 就可以实现相应的抽象方法了

3. 单例接口 `SingletonBeanRegistry` 的接口定义实现，而 `DefaultSingletonBeanRegistry` 对接口实现后，会被抽象类 `AbstractBeanFactory` 继承



### 工程结构

![image-20220718142701872](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207181427923.png)





## 03 对象实例化策略

