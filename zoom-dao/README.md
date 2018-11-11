# zoom-dao


# 概述

zoom通过ActiveRecord和Entity两种模式让访问数据库变得异常简单。目前支持的数据库有:

+ Mysql
+ Oracle
+ H2


#### 配置
```java

package com.example.modules;
...

@Module
class MyConfig{
  @IocBean
  public Dao getDao(DataSource dataSource){
      DruidDataSourceProvider dataSourceProvider = new DruidDataSourceProvider();
      dataSourceProvider.setUrl("jdbc:mysql://localhost:3306/database?useUnicode=true&characterEncoding=UTF-8");
      dataSourceProvider.setPassword("root");
      dataSourceProvider.setUsername("root");
      dataSourceProvider.setDriverClassName("com.mysql.jdbc.Driver");
      return new ZoomDao(dataSourceProvider.getDataSource());
  }
}

```

####  SqlBuilder+ActiveRecord模式

```java

@Inject
private Dao dao;

...
dao.ar("student")
  .where("age",SqlBuilder.Symbol.GT,18)
  .like("name", SqlBuilder.Like.LEFT,"张")
  .find();

Db.ar("class")
  .where("id",1)
  .join("student","student.class_id=class.id").page(1,30);
```

### SqlBuilder+Entity模式


```java

@Table("product")
class Product{
  @AutoGenerate
  private int id;

  private String name;

  private double price;
}

Product product = dao.ar(Product.class).get(1);

```



