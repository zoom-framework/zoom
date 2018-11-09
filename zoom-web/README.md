

# 生命周期

## 启动
1、加载主配置文件
2、扫描所有文件
3、初始化aop
4、初始化ioc （将配置加入到ioc,并初始化系统对象加入ioc容器）
5、解析类
5.1、解析配置类
   将bean依赖信息加入到ioc
   
5.2、初始化配置类
   调用配置类的config方法，增加aop、ioc、parameter、rendering等配置信息
   调用配置类的iocbean方法，增加到ioc容器中
    
5.3、解析controller
   增加到容器和路由