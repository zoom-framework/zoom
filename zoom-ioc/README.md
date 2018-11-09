# ioc容器

使用：

IocContainer ioc = new SimpleIocContainer();

# 原理

ioc主要解决两件事情：

1、对象保存

2、对象生命周期
 + 对象创建
 + 对象依赖注入
 + 对象销毁
 


# 对象创建
1、使用实际类new出来
2、使用配置中的class找到实际类new出来
3、调用某个类的静态函数
4、调用ioc容器中的某个对象的方法


# 依赖注入
1、参数依赖
2、字段依赖
3、调用的方法
4、调用的构造方法

封装: IocValue/IocInjecter/IocConstructor/IocClass/IocClassFactory


## IocClassFactory
注册配置
register(config)

注册ioc容器对象创建方法
register(Class,Method)
注册类
registerClass(Class)  

get(Class)=>IocClass
get(id)=>IocClass

# 过程

## ioc容器获取对象过程

### 通过类名获取，要求实际类或者已经在ioc容器中注册的接口
伪代码：
iocClass = IocClassFactory.get(class);
Object target = iocClass.newInstance();
save(target);
iocClass.inject(target)
return target;


	






# 

IocObjectProxy
IocClass
IocClassFactory







