# zoom-dao


# 概述

zoom通过ActiveRecord、Entity、RawActiveRecord三种模式让访问数据库变得异常简单。目前支持的数据库有:

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



# 创建表

```java

@Inject
private Dao dao;

....

 dao.builder()
    .dropIfExists("product")
    .createTable("product").comment("产品")
    .add("pro_id").comment("编号").integer().keyPrimary().autoIncement()
    .add("pro_name").comment("名称").string(100).keyIndex().notNull()
    .add("pro_price").comment("价格").number().keyIndex().notNull()
    .add("pro_info").comment("信息").text()
    .add("pro_thumb").comment("预览图地址").string(200)
    .add("pro_img").comment("产品图片").blob()
    .add("pro_count").comment("产品库存").integer().defaultValue(100)
    .add("tp_id").comment("类型编号").integer().keyIndex()
    .add("create_at").comment("创建时间").timestamp().defaultValue(new DatabaseBuilder.FunctionValue("CURRENT_TIMESTAMP"))

    .dropIfExists("type")
    .createTable("type").comment("类型")
    .add("tp_id").comment("编号").integer().keyPrimary().autoIncement()
    .add("tp_title").comment("标题").string(100).keyIndex().notNull()

.build();


```


对于mysql，会生成：

```

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`(
	`pro_id` int(32) PRIMARY KEY AUTO_INCREMENT,
	`pro_name` varchar(100) NOT NULL,
	`pro_price` double NOT NULL,
	`pro_info` mediumtext NULL,
	`pro_thumb` varchar(200) NULL,
	`pro_img` blob NULL,
	`pro_count` int(32) DEFAULT 100,
	`tp_id` int(32) NULL,
	`create_at` timestamp DEFAULT CURRENT_TIMESTAMP
)charset=utf8;
CREATE INDEX IDX_product_pro_name ON product(pro_name);
CREATE INDEX IDX_product_pro_price ON product(pro_price);
CREATE INDEX IDX_product_tp_id ON product(tp_id);
COMMENT ON product IS '产品';
COMMENT ON product.pro_id IS '编号';
COMMENT ON product.pro_name IS '名称';
COMMENT ON product.pro_price IS '价格';
COMMENT ON product.pro_info IS '信息';
COMMENT ON product.pro_thumb IS '预览图地址';
COMMENT ON product.pro_img IS '产品图片';
COMMENT ON product.pro_count IS '产品库存';
COMMENT ON product.tp_id IS '类型编号';
COMMENT ON product.create_at IS '创建时间';
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type`(
	`tp_id` int(32) PRIMARY KEY AUTO_INCREMENT,
	`tp_title` varchar(100) NOT NULL
)charset=utf8;
CREATE INDEX IDX_type_tp_title ON type(tp_title);
COMMENT ON type IS '类型';
COMMENT ON type.tp_id IS '编号';
COMMENT ON type.tp_title IS '标题';

```

这样的sql语句


如果直接想要打印出来，可以如下

```
String sql =  dao.builder()
            ...
            .buildSql();
```

# 基本操作

####  SqlBuilder+ActiveRecord模式

在本模式下，主要支持如下特性；
+ 对于每一个操作的字段都有严格限制
+ 自动适配字段类型，如Map、List、Bean类型存储到Clob会自动转成json字符串
+ 自动适配字段名称，将字段名称自动识别改成类似实体类的驼峰式标准。

对于上述product表

```java

/// 插入商品
Record product = Record.as(
        "name", "我的商品",
        "price", 100.0D,
        "info", "好长好长的描述",
        "thumb", "图片的url",
        "img", new File("/Users/jzoom/Documents/FEEBEB71F4A304883EFA5E349A50DB2B.jpg"),
        "count", 200,
        "tpId", 1
);
dao.ar("product")
        .insert(product);

///获取到自增id
int insertId = product.getInt("id");

/// 修改商品
product.set("count",300);
product.set("price",188D);
product.set("name","测试");

dao.ar("product")
        .filter("count|price")        //增加一个更新过滤器
        .update(product);

//// 主键获取到商品
Record record = dao.ar("product").get(insertId);
/// 没有修改名称
assertEquals(record.getString("name"),"我的商品");
/// 修改price和count成功
assertEquals(record.getInt("count"),300);
assertEquals(record.getDouble("price"),188D,0);

/// 查询所有商品
List<Record> list = dao.ar("product").find();

/// 分页查询
Page<Record> page = dao.ar("product")
            .like("name",SqlBuilder.Like.MATCH_BOTH,"我的")
            .page(1,30);
/// 删除商品
dao.ar("product").delete(record);

```

注意到上面的所有字段都是经过适配过的形式，查看具体的适配规则，请移步这里

### SqlBuilder+RawActiveRecord模式



在RawActiveRecord模式下，主要支持如下特性：
+ 所有的字段都不会做类型的自动适配，而仅仅采用了统一的命名规则，该命名规则可以在Dao中设置，或者也可以在每一个数据库操作中设置。在默认情况下，规则为字段统一修改为小写。
+ 支持在select中进行函数或者其他运算，如:dao.table('a').select("count(*) as count,max(a) as a")
+ 支持insertOrUpdate操作。


下面的代码和上面代码功能基本一致

```java

 /// 插入商品 ,在默认情况下，往数据库方向的字段，将被改成大写,可以使用dao.setNameAdapter来修改默认行为
 Record product = Record.as(
         "pro_name", "我的商品",
         "pro_price", 100.0D,
         "pro_info", "好长好长的描述",
         "pro_thumb", "图片的url",
        /// 在RawActiveRecord模式下不适配数据库字段类型，所以插入和更新二进制操作在某些数据库类型下不支持
        // "pro_img", new File("/Users/jzoom/Documents/FEEBEB71F4A304883EFA5E349A50DB2B.jpg"),
         "pro_count", 200,
         "tp_id", 1
 );
 dao.table("product")
         .insert(product,"pro_id");

 ///获取到自增id,在默认情况下，出数据库方向的字段将全部改成小写，可以使用dao.setNameAdapter来修改默认行为
 int insertId = product.getInt("pro_id");

 /// 修改商品
 /// 使用链式的set,来设置或者使用setAll一次性设置要修改的字段
 dao.table("product")
         .where("pro_id",insertId)
         .set("pro_count",3000)
         .set("pro_price",188D)
         .update();

 //// 主键获取到商品
 Record record = dao.table("product").where("pro_id",insertId).get();
 /// 修改price和count成功
 assertEquals(record.getInt("pro_count"),3000);
 assertEquals(record.getDouble("pro_price"),188D,0);

 /// 查询所有商品,仅仅查看pro_id,pro_name字段
 List<Record> list = dao.table("product").select("pro_id,pro_name").find();

 /// 分页查询
 Page<Record> page = dao.table("product")
         .like("pro_name",SqlBuilder.Like.MATCH_BOTH,"我的")
         .page(1,30);

 /// 删除商品
 dao.table("product").where("pro_id",insertId).delete();


```

关于RawActivityRecord模式下的改名，请看这里


### SqlBuilder+Entity模式

在zoom中允许懒加载Entity，在使用的时候再去绑定实体类和数据库字段的关系。

对于上述操作，使用实体类如下

```java




```


### where条件



### join多表联合



