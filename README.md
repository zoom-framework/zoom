# zoom


# 语义说明：

* getXXX            获取单个或多个数据，如果没有获取到数据，返回null或者空集合
* fetchXXX          断言能获取到一个数据，如果没有获取到，将创建，如果不成功将报错
* findXXX           获取多个数据，返回集合（可能为空），一定不是null


# 公共接口说明

* Destroyable       销毁
* Initable          初始化



# 内容更新

[2018-10-30]
后端：
entity 在查询单个的时候能link其他表
完善entity的自动生成 auto increase/sequence/trigger等形式
完善entity的primary key设置
完善entity的查询条件
前端：
完善属性编辑器，增加枚举类型、布尔类型

[2018-10-31]
*[x] 新增event模块，异步执行系统事件
*[x] 增加IocEventListener,优化Timer、Event

[2018-11-01]
*[ ] Websocket,web shell
*[ ] 日志实时查询
*[x]错误：没有办法上传:

[2018-11-03]
*[x] 多tab
*[ ] 优化模板管理，增加自定义字段
*[ ] 优化多记录编辑、多记录删除



增强模板生成器，可以生成各种数据
1、生成api接口调用
2、生成

后台可直接创建 rest接口
1、选择资源（多个表等）
2、选择字段
3、选择参数
4、定制规则


dao的sql分页优化，看看其他系统都是怎么做的, position和pageSize放在参数里面。


Exception的处理规则


primary key 如果用户没有选择，需要给用户选择

#### 


#### 代码统计
```
cloc ./ --include-ext=java --match-d=src
```

统一的版本管理
```
mvn versions:set -DnewVersion=0.1.2
```

发布
```
mvn deploy
```

