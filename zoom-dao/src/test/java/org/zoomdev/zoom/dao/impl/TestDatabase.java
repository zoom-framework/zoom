package org.zoomdev.zoom.dao.impl;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.common.utils.Converter;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.annotations.AutoGenerate;
import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.Table;
import org.zoomdev.zoom.dao.entities.*;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class TestDatabase extends AbstractDaoTest {

    @BeforeClass
    public static void setup() {
        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) throws Exception {

                createTables(dao);
            }
        });
    }


    static final String IMAGE_FILE;

    public static void createTables(Dao dao) {
        DatabaseBuilder builder = dao.builder();
        String sql = builder

                .dropIfExists("product")
                .createTable("product")
                .add("pro_id").integer().keyPrimary().autoIncement()
                .add("pro_name").string(100).keyIndex().notNull()
                .add("pro_price").number().keyIndex().notNull()
                .add("pro_info").text()
                .add("pro_thumb").string(200)
                .add("pro_img").blob()
                .add("pro_count").integer().defaultValue(100)
                .add("tp_id").integer().keyIndex()
                .add("shp_id").string(30).keyIndex()
                .add("create_at").timestamp().defaultValue(new DatabaseBuilder.FunctionValue("CURRENT_TIMESTAMP"))

                .dropIfExists("type")
                .createTable("type")
                .add("tp_id").integer().keyPrimary().autoIncement()
                .add("tp_title").string(100).keyIndex().notNull()
                .add("shp_id").string(30).keyIndex().notNull().keyIndex()

                .dropIfExists("customer")
                .createTable("customer")
                .add("cm_id").integer().keyPrimary().autoIncement()
                .add("cm_account").string(100).keyUnique().notNull()
                .add("create_at").timestamp().defaultFunction("CURRENT_TIMESTAMP")
                .add("cm_pwd").string(32)

                .dropIfExists("collection")
                .createTable("collection")
                .add("pro_id").integer().keyPrimary()
                .add("usr_id").integer().keyPrimary()
                .add("c_order").integer()

                .dropIfExists("shop")
                .createTable("shop")
                .add("shp_id").string(30).keyPrimary()
                .add("shp_title").string(100)
                .add("shp_level").integer()
                .add("shp_stars").number()
                .add("shp_address").string(100)
                .add("shp_sales").integer()

                //订单 , 关键字???
                .dropIfExists("shp_order")
                .createTable("shp_order")
                .add("ord_id").integer().keyPrimary().autoIncement()
                .add("shp_id").string(30).notNull()
                .add("pro_id").integer().notNull()
                .add("ord_count").number().notNull()
                .add("ord_status").integer().defaultValue(0).notNull()
                .add("cm_id").integer().notNull()

                .buildSql();

        System.out.println(sql);


        builder.build();


        dao.builder()
                .dropIfExists("shp_product")
                .createTable("shp_product").comment("产品")
                .add("PRO_ID").comment("编号").integer().keyPrimary().autoIncement()
                .add("PRO_NAME").comment("名称").string(100).keyIndex().notNull()
                .add("PRO_PRICE").comment("价格").number().keyIndex().notNull()
                .add("PRO_INFO").comment("信息").text()
                .add("PRO_THUMB").comment("预览图地址").string(200)
                .add("PRO_IMG").comment("产品图片").blob()
                .add("PRO_COUNT").comment("产品库存").integer().defaultValue(100)
                .add("TP_ID").comment("类型编号").integer().keyIndex()
                .add("CREATE_AT").comment("创建时间").timestamp().defaultValue(new DatabaseBuilder.FunctionValue("CURRENT_TIMESTAMP"))

                .dropIfExists("shp_type")
                .createTable("shp_type").comment("类型")
                .add("TP_ID").comment("编号").integer().keyPrimary().autoIncement()
                .add("TP_TITLE").comment("标题").string(100).keyIndex().notNull()

                .build();


        //takesnapshot

        dao.getDbStructFactory().takeSnapshot();

    }

    static {
        ResScanner scanner = new ResScanner();
        try {
            scanner.scan();
            IMAGE_FILE = scanner.findFile("*").get(0).getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new ZoomException(e);
        }
    }


    public static class ProductInfo {

        public ProductInfo() {

        }

        public ProductInfo(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        private String title;
    }

    @Table("shp_product")
    public static class Product {


        @AutoGenerate
        Integer id;
        /// 表示不想用name，而是用title,这里可以写name,也可以写pro_name
        @Column("name")
        String title;


        String thumb;
        Double price;

        public List<ProductInfo> getInfo() {
            return info;
        }

        public void setInfo(List<ProductInfo> info) {
            this.info = info;
        }

        List<ProductInfo> info;

        public byte[] getImg() {
            return img;
        }

        public void setImg(byte[] img) {
            this.img = img;
        }

        byte[] img;
        int count;
        int tpId;
        Date createAt;


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }


        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTpId() {
            return tpId;
        }

        public void setTpId(int tpId) {
            this.tpId = tpId;
        }

        public Date getCreateAt() {
            return createAt;
        }

        public void setCreateAt(Date createAt) {
            this.createAt = createAt;
        }
    }

    @Test
    public void testRecord() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) throws Exception {

                doActionWithActiveRecord(dao);

                try {
                    testEntityActiveRecord(dao);
                } catch (IOException e) {
                    throw new DaoException(e);
                }

                testRawActiveRecord(dao);
            }
        });
    }

    public void testEntityActiveRecord(Dao dao) throws IOException {


        /// 插入商品
        Product shp_product = new Product();
        shp_product.setTitle("我的商品");
        shp_product.setPrice(100.0D);
        shp_product.setInfo(
                Arrays.asList(new ProductInfo("好长好长的描述"))
        );
        shp_product.setThumb("图片的url");
        shp_product.setImg(Io.readBytes(new File(IMAGE_FILE)));
        shp_product.setCount(200);

        dao.ar(Product.class)
                .insert(shp_product);

        ///获取到自增id
        int insertId = shp_product.getId();

        /// 修改商品
        shp_product.setCount(300);
        shp_product.setPrice(188D);
        shp_product.setTitle("测试");

        dao.ar(Product.class)
                .filter("count|price")        //增加一个更新过滤器
                .update(shp_product);

        //// 主键获取到商品
        Product record = dao.ar(Product.class).get(insertId);
        /// 没有修改名称
        assertEquals(record.getTitle(), "我的商品");
        /// 修改price和count成功
        assertEquals(record.getCount(), 300);
        assertEquals(record.getPrice(), 188D, 0);

        /// 查询所有商品,同样可以设置查询字段过滤器
        List<Product> list = dao.ar(Product.class)
                .filter("id|title").find();

        /// 分页查询
        Page<Product> page = dao.ar(Product.class)
                .like("title", SqlBuilder.Like.MATCH_BOTH, "我的")
                .page(1, 30);

        /// 删除商品
        dao.ar(Product.class).delete(record);

        Product product = dao.ar(Product.class).where(new SqlBuilder.Condition() {
            @Override
            public void where(Sql where) {
                where.where("id", 1);
            }
        }).get();

    }


    public void testRawActiveRecord(Dao dao) {

        /// 插入商品 ,在默认情况下，往数据库方向的字段，将被改成大写,可以使用dao.setNameAdapter来修改默认行为
        Record shp_product = Record.as(
                "pro_name", "我的商品",
                "pro_price", 100.0D,
                "pro_info", "好长好长的描述",
                "pro_thumb", "图片的url",
                /// 在RawActiveRecord模式下不适配数据库字段类型，所以插入和更新二进制操作在某些数据库类型下不支持
                // "pro_img", new File(IMAGE_FILE),
                "pro_count", 200,
                "tp_id", 1
        );
        dao.table("shp_product")
                .insert(shp_product, "pro_id");

        ///获取到自增id
        int insertId = shp_product.getInt("pro_id");

        /// 修改商品
        /// 使用链式的set,来设置或者使用setAll一次性设置要修改的字段
        dao.table("shp_product")
                .where("pro_id", insertId)
                .set("pro_count", 3000)
                .set("pro_price", 188D)
                .update();

        //// 主键获取到商品
        Record record = dao.table("shp_product").where("pro_id", insertId).get();
        /// 修改price和count成功
        assertEquals(record.getInt("pro_count"), 3000);
        assertEquals(record.getDouble("pro_price"), 188D, 0);

        /// 查询所有商品,仅仅查看pro_id,pro_name字段
        List<Record> list = dao.table("shp_product").select("pro_id,pro_name").find();

        /// 分页查询
        Page<Record> page = dao.table("shp_product")
                .like("pro_name", SqlBuilder.Like.MATCH_BOTH, "我的")
                .page(1, 30);

        /// 删除商品
        dao.table("shp_product").where("pro_id", insertId).delete();

    }


    protected void doActionWithActiveRecord(Dao dao) {
        /// 插入商品
        Record shp_product = Record.as(
                "name", "我的商品",
                "price", 100.0D,
                "info", "好长好长的描述",
                "thumb", "图片的url",
                "img", new File(IMAGE_FILE),
                "count", 200,
                "tpId", 1
        );
        dao.ar("shp_product")
                .insert(shp_product);

        ///获取到自增id
        int insertId = shp_product.getInt("id");

        /// 修改商品
        shp_product.set("count", 300);
        shp_product.set("price", 188D);
        shp_product.set("name", "测试");

        dao.ar("shp_product")
                .filter("count|price")        //增加一个更新过滤器
                .update(shp_product);

        //// 主键获取到商品
        Record record = dao.ar("shp_product").get(insertId);
        /// 没有修改名称
        assertEquals(record.getString("name"), "我的商品");
        /// 修改price和count成功
        assertEquals(record.getInt("count"), 300);
        assertEquals(record.getDouble("price"), 188D, 0);

        /// 查询所有商品
        List<Record> list = dao.ar("shp_product")
                .select("id,name as title")     // 这里也支持as，表示不想用name而使用title
                .find();

        record = list.get(0);
        assertEquals(record.size(), 2);
        assertNotNull(record.get("title"));
        assertNull(record.get("name"));

        /// 分页查询
        Page<Record> page = dao.ar("shp_product")
                .like("name", SqlBuilder.Like.MATCH_BOTH, "我的")
                .page(1, 30);

        /// 删除商品
        dao.ar("shp_product").delete(record);

    }


    protected int getProductCount(Dao dao) {
        return dao.ar("product").count();
    }

    protected void insert(Dao dao, int count) {
        for (int i = 0; i < count; ++i) {
            dao.ar("product")
                    .insert(Record.as(
                            "name", String.format("title:%d", i),
                            "price", 100,
                            "info", "测试商品",
                            "count", (int) (Math.random() * 1000),
                            "tpId", 1,
                            "shpId", "firstBusiness"
                    ));
        }
    }


    @Test
    public void test() {
        execute(new RunWithDao() {
            @Override
            public void run(final Dao dao) {
                final DruidDataSource dataSource = (DruidDataSource) dao.getDataSource();
                long current = dataSource.getConnectCount();
                int productCount = getProductCount(dao);
                ZoomDao.executeTrans(new Runnable() {
                    @Override
                    public void run() {
                        insert(dao, 3);
                    }
                });

                /// we only open one connection

                assertEquals(productCount + 3, getProductCount(dao));
            }
        });
    }

    @Test
    public void testReenter() {


        execute(new RunWithDao() {

            @Override
            public void run(final Dao dao) {
                int productCount = getProductCount(dao);

                ZoomDao.executeTrans(new Runnable() {
                    @Override
                    public void run() {
                        insert(dao, 1);
                        ZoomDao.executeTrans(new Runnable() {
                            @Override
                            public void run() {
                                insert(dao, 1);
                                ZoomDao.executeTrans(new Runnable() {
                                    @Override
                                    public void run() {
                                        insert(dao, 1);
                                    }
                                });
                            }
                        });

                    }
                });

                assertEquals(productCount + 3, getProductCount(dao));

            }
        });

    }


    @Test
    public void testInsertOrUpdate() {
        final Product product = new Product();
        product.setId(1);
        product.setTitle("test");
        product.setPrice(300.0);

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) throws Exception {
                dao.ar(Product.class).filter("title|price").insertOrUpdate(product, "id");
            }
        });
    }


    @Test(expected = DaoException.class)
    public void testError() {


        execute(new RunWithDao() {

            @Override
            public void run(final Dao dao) {
                int productCount = getProductCount(dao);

                try {
                    ZoomDao.executeTrans(new Runnable() {
                        @Override
                        public void run() {
                            insert(dao, 1);
                            ZoomDao.executeTrans(new Runnable() {
                                @Override
                                public void run() {
                                    insert(dao, 1);
                                    ZoomDao.executeTrans(new Runnable() {
                                        @Override
                                        public void run() {
                                            insert(dao, 1);
                                            throw new ZoomException("Something bad!");
                                        }
                                    });
                                }
                            });

                        }
                    });
                } finally {
                    int currentCount = getProductCount(dao);

//                    assertEquals(productCount,currentCount );
                }


            }
        });

    }


    @Test
    public void testEntity() {
        execute(new RunWithDao() {
            @Override
            public void run(final Dao dao) throws Exception {


                final String test_business = "testBusiness";

                Record testRecors = Record.as(
                        "shp_id", test_business,
                        "shp_title", "测试行家",
                        "shp_level", 1,
                        "shp_stars", 4.9,
                        "shp_sales", 100000
                );
                dao.table("shop").insert(testRecors);
                dao.table("shop").where("shp_id", test_business).delete();

                //       dao.table("shop").insert(testRecors);
                //       dao.table("shop").where("id",test_business).delete();


                final String FIRST_BUSINESS = "firstBusiness";

                Record record = Record.as(
                        "shp_id", FIRST_BUSINESS,
                        "shp_title", "天下第一家",
                        "shp_level", 1,
                        "shp_stars", 4.9,
                        "shp_sales", 100000
                );
                //商家注册 (add)
                dao.table("shop")
                        .insert(record);


                record = Record.as(
                        "shp_id", "second",
                        "shp_title", "弱弱的第二家",
                        "shp_level", 2,
                        "shp_stars", 2.9,
                        "shp_address", "测试地址",
                        "shp_sales", 100

                );
                //商家注册 (add)
                dao.table("shop")
                        .insert(record);

                //verify business
                List<Record> result = dao.table("shop")
                        .orderBy("shp_id", SqlBuilder.Sort.DESC).find();
                assertEquals(JSON.stringify(result.get(0)), JSON.stringify(record));

                // 商家编辑信息

                assertEquals(dao.table("shop")
                        .where("shp_id", FIRST_BUSINESS)
                        .update(
                                Record.as(
                                        "shp_title", "牛逼的第一家"
                                )
                        ), 1);

                assertEquals(dao.table("shop")
                        .where("shp_id", "找不到这家")
                        .setAll(Record.as(
                                "shp_title", "牛逼的第一家"
                        )).update(


                        ), 0);

                // 是不是真的改了?
                assertEquals(dao.table("shop").select("shp_title").where("shp_id", FIRST_BUSINESS).get(),
                        Record.as("shp_title", "牛逼的第一家"));


                Record type = Record.as(
                        "shp_id", FIRST_BUSINESS,
                        "tp_title", "好吃到爆的饭"
                );
                // 商家编辑分类
                assertEquals(dao.table("type")
                        .insert(type, "tp_id"), 1);

                //type中应该有id
                assertEquals(type.getInt("tp_id"), 1);


                type = Record.as(
                        "shp_id", FIRST_BUSINESS,
                        "tp_title", "高级翅膀"
                );
                // 商家编辑分类
                assertEquals(dao.table("type")
                        .insert(type, "tp_id"), 1);

                //type中应该有id
                assertEquals(type.getInt("tp_id"), 2);


                //商家编辑商品 (add/edit)

                assertEquals(dao.table("product")
                        .set("tp_id", 1)
                        .insert(Record.as(
                                "pro_name", "牛肉饭",
                                "shp_id", FIRST_BUSINESS,
                                "pro_price", 50.0,
                                //     "pro_img", "image binary".getBytes(),
                                "pro_info", "very very long text,好长好长啊a"

                        ), "pro_id"), 1);


                Record product = dao.table("product").where("pro_id", 1).get();
                //   assertEquals(product.get("pro_img").getClass(), byte[].class);


                //买家注册 (add)
                assertEquals(dao.table("customer").insert(Record.as(
                        "cm_account", "firstUser"
                )), 1);

                //买家浏览商品 (查询 query)
                Page<Record> page = dao
                        .table("product")
                        .join("type", "type.tp_id=product.tp_id")
                        .join("shop", "shop.shp_id=product.shp_id")
                        .like("pro_name", SqlBuilder.Like.MATCH_BOTH, "饭")
                        .page(1, 30);

                assertTrue(page.getTotal() > 0);


                List<Record> list = dao
                        .table("product")
                        .join("type", "type.tp_id=product.tp_id")
                        .join("shop", "shop.shp_id=product.shp_id")
                        .whereIn("pro_id", 1, 2).limit(0, 30);

                //assertTrue(list.size() > 0);


                Record record1 = dao.table("product").whereNull("pro_img").get();

                Record record2 = dao.table("product").whereNotNull("pro_img").get();


                //买家下单商品 (trans:  update/create 减去库存、并创建订单）

                Executor executor = Executors.newFixedThreadPool(10);

                List<Future> futures = new ArrayList<Future>();
                for (int i = 0; i < 10; ++i) {
                    Future future = ((ExecutorService) executor).submit(new Runnable() {
                        @Override
                        public void run() {
                            ZoomDao.executeTrans(new Runnable() {
                                @Override
                                public void run() {
                                    //随机购买几件商品
                                    int count = (int) (Math.random() * 10);
                                    if (dao.ar().executeUpdate(
                                            "update product set pro_count=pro_count-? where pro_id=? and pro_count>?",
                                            count, 1, count) > 0) {
                                        dao.table("shp_order")
                                                .insert(Record.as(
                                                        "shp_id", FIRST_BUSINESS,
                                                        "ord_count", count,
                                                        "pro_id", 1,
                                                        "cm_id", 1
                                                ));
                                    } else {
                                        throw new DaoException("库存不足");
                                    }

                                }
                            });
                        }
                    });

                    futures.add(future);
                }

                for (Future future : futures) {
                    try {
                        future.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                ((ExecutorService) executor).shutdown();

                //商家发货 (update)

                List<Record> orders = dao.table("shp_order")
                        .where(new SqlBuilder.Condition() {
                            @Override
                            public void where(Sql where) {
                                where.where("ord_status", 0)
                                        .orWhere("ord_status", 1);
                            }
                        }).find();

                orders = dao.table("shp_order")
                        .whereCondition("ord_status=?", 0)
                        .orWhere("ord_status", 1).find();


                //买家收货（完成一次交易) (update)


                // 买家查看今日总卖出量（统计）

                List<Record> view = dao.table("shp_order").selectSum("ord_count")
                        .groupBy("shp_id")
                        .having("sum(ord_count)", Symbol.GT, 100).find();


                dao.table("shp_order")
                        .select(Arrays.asList(

                                "shp_id", "sum(ord_count)"
                        )).groupBy("shp_id")
                        .having("sum(ord_count)", Symbol.GT, 100).find();

                dao.table("shp_order")
                        .select("shp_id,sum(ord_count)").groupBy("shp_id")
                        .having("sum(ord_count)", Symbol.GT, 100).find();

                dao.table("shp_order")
                        .whereCondition("ord_status=?", 0)
                        .orWhere(new SqlBuilder.Condition() {
                            @Override
                            public void where(Sql where) {
                                where.where("ord_status", 1)
                                        .where("ord_count", Symbol.GT, 100);
                            }
                        }).find();
            }
        });

    }


    @Test(expected = DaoException.class)
    public void testError1() {
        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                dao.table("shp_order")
                        .whereCondition("ord_status", 0)
                        .orWhere(new SqlBuilder.Condition() {
                            @Override
                            public void where(Sql where) {
                                where.where("ord_status", 1)
                                        .where("ord_count", Symbol.GT, 100);
                            }
                        }).find();
            }
        });

    }


    @Test(expected = DaoException.class)
    public void testUni() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                try {
                    dao.table("customer")
                            .set("cm_account", "123")
                            .insert();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ZoomException(e);
                }

                dao.table("customer")
                        .set("cm_account", "123")
                        .insert();


            }
        });

    }


    @Test
    public void testInsertOrUpdte() {
        final MutableInt mutableInt = new MutableInt(0);

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                try {
                    if (mutableInt.getValue() == 0) {
                        return;
                    }
                    dao.table("customer")
                            .set("cm_account", "123")
                            .set("cm_pwd", "test")
                            .insertOrUpdate("cm_account");


                    dao.table("customer")
                            .set("cm_account", "123")
                            .set("cm_pwd", "test")
                            .insertOrUpdate("cm_account");
                } finally {
                    mutableInt.add(1);
                }


            }
        });
    }


    /**
     * 比较一下， 正则表达式缓存和不缓存的速度
     */
    @Test()
    public void testEntityJoin() {


        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {

                List<JoinProductWithType> list = dao.ar(JoinProductWithType.class).find();
                list = dao.ar(JoinProductWithType.class).filter("id|title").find();

                System.out.println(JSON.stringify(list));

                JoinProductWithType product = dao.ar(JoinProductWithType.class).where("id", 2).get();
                product = dao.ar(JoinProductWithType.class).get(2);
            }
        });


    }

    @Test()
    public void testSimpleEntityQuery() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                // query
                List<SimpleProduct> products = dao.ar(SimpleProduct.class)
                        .like("name", SqlBuilder.Like.MATCH_START, "张")
                        .find();

                products = dao.ar(SimpleProduct.class).where("id", 2).find();

                products = dao.ar(SimpleProduct.class)
                        .orderBy("id", SqlBuilder.Sort.DESC).find();

                products = dao.ar(SimpleProduct.class)
                        .limit(1, 30);

                Page<SimpleProduct> page = dao.ar(SimpleProduct.class)
                        .page(1, 30);


                System.out.println(page);
            }
        });

    }

    @Test()
    public void testSimpleEntityModify() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                SimpleProduct simpleProduct = new SimpleProduct();
                simpleProduct.setName("测试商品");
                simpleProduct.setPrice(100);
                simpleProduct.setInfo("介绍");
                simpleProduct.setThumb("Http://mycom/1.jpg");
                simpleProduct.setImg("测试图片src".getBytes());

                dao.ar(SimpleProduct.class)
                        .insert(simpleProduct);

                assertFalse(simpleProduct.getId() == 0);


                SimpleProduct queryProduct = dao.ar(SimpleProduct.class).get(simpleProduct.getId());

                assertEquals(queryProduct.getId(), simpleProduct.getId());
                assertEquals(queryProduct.getName(), simpleProduct.getName());
                assertEquals(queryProduct.getPrice(), simpleProduct.getPrice());
                assertEquals(queryProduct.getInfo(), simpleProduct.getInfo());
                assertEquals(queryProduct.getThumb(), simpleProduct.getThumb());

                assertTrue(Arrays.equals(queryProduct.getImg(), simpleProduct.getImg()));

                queryProduct.setThumb("http://test.com/2.jpg");
                queryProduct.setName("修改名称");

                //值修改一个字段
                dao.ar(SimpleProduct.class)
                        .filter("thumb")
                        .update(queryProduct);


                queryProduct = dao.ar(SimpleProduct.class).get(simpleProduct.getId());
                assertEquals(queryProduct.getId(), simpleProduct.getId());
                assertEquals(queryProduct.getName(), simpleProduct.getName());
                assertEquals(queryProduct.getPrice(), simpleProduct.getPrice());
                assertEquals(queryProduct.getInfo(), simpleProduct.getInfo());
                assertFalse(
                        ObjectUtils.equals(queryProduct.getThumb(), simpleProduct.getThumb())
                );
            }
        });
    }


    @Test()
    public void testEntity1() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                SimpleShop shop = dao.ar(SimpleShop.class).get("1");
                if (shop != null) {
                    dao.ar(SimpleShop.class).delete(shop);
                }


                shop = new SimpleShop();
                shop.setId(String.valueOf(System.currentTimeMillis()));
                shop.setTitle("测试商店");
                shop.setAddress("地理位置");
                dao.ar(SimpleShop.class).insert(shop);
                //    assertEquals(shop.getId(),"1");
            }
        });
    }


    @Test(expected = DaoException.class)
    public void testCannotFindJoin() {
        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                dao.ar(ErrorCannotFindJoin.class).find();
            }
        });
    }

    @Test(expected = DaoException.class)
    public void testCannotFindField() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                dao.ar(ErrorCannotFindField.class).find();
            }
        });
    }

    @Test
    public void testGroupBy() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {

                SimpleShop shop = new SimpleShop();
                shop.setId(String.valueOf(Math.random()));
                shop.setAddress("测试地址");
                shop.setId("testBusiness");
                dao.ar(SimpleShop.class).insert(shop);


                SimpleProduct product = new SimpleProduct();
                product.setName("测试商品");
                product.setShpId("testBusiness");
                product.setThumb("");
                product.setPrice(100);

                dao.ar(SimpleProduct.class).insert(product);


                GroupByEntity entity = dao.ar(GroupByEntity.class)
                        .groupBy("shpId").having("avg(pro_price)", Symbol.GT, 50).get();

                assertNotNull(entity);

                System.out.println(entity.getPrice());

            }
        });


        //   System.out.println(JSON.stringify(entity));

    }


    @Override
    protected void process(Dao dao) {
        SimpleShop shop = new SimpleShop();
        shop.setTitle("测试商家");
        shop.setAddress("测试地址");
        shop.setId("testBusiness1");
        dao.ar(SimpleShop.class).insert(shop);

        dao.ar(SimpleShop.class).where("id", "testBusiness1").delete();

    }

    @Test
    public void testFilter() {
        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                Shop shop = new Shop();
                shop.setTitle("测试商家");
                shop.setAddress("测试地址");
                shop.setId("business2");
                dao.ar(Shop.class).insert(shop);


                shop.setTitle("测试商家500");
                shop.setAddress("测试地址500");
                dao.ar(Shop.class).filter("title")
                        .update(shop);
                Shop shop2 = dao.ar(Shop.class).get("business2");

                assertEquals(shop.getId(), shop2.getId());
                assertEquals(shop2.getTitle(), "测试商家500");
                assertEquals(shop2.getAddress(), "测试地址");

            }
        });

    }


    @Test
    public void testList() {


        execute(new RunWithDao() {
            @Override
            public void run(final Dao dao) {
                Shop shop = new Shop();
                shop.setTitle("测试商家");
                shop.setAddress("测试地址");
                shop.setId("testBusiness1");

                Shop shop1 = new Shop();
                shop1.setTitle("测试商家3");
                shop1.setAddress("测试地址3");
                shop1.setId("testBusiness2");

                Shop shop2 = new Shop();
                shop2.setTitle("测试商家2");
                shop2.setAddress("测试地址2");
                shop2.setId("testBusiness100");


                List<Shop> list = Arrays.asList(
                        shop, shop1, shop2
                );

                assertEquals(dao.ar(Shop.class).insert(list), 3);


                List<Shop> list1 = dao.ar(Shop.class).find();

                for (Shop shop3 : list1) {
                    System.out.println(shop3.getTitle());
                }

                assertEquals(
                        dao.ar(Shop.class).where("id", "testBusiness100")
                                .value("title", String.class),
                        "测试商家2"
                );
                dao.ar(Shop.class).filter("title").update(
                        CollectionUtils.map(list, new Converter<Shop, Shop>() {
                            @Override
                            public Shop convert(Shop data) {
                                data.setTitle(data.getTitle() + ".back");
                                return data;
                            }
                        })
                );

                List<Shop> list2 = dao.ar(Shop.class).find();

                for (Shop shop3 : list2) {
                    System.out.println(shop3.getTitle());
                }


                dao.ar(Shop.class).delete(list);
            }
        });
    }

    @Test(expected = DaoException.class)
    public void testError2() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                Entity entity = dao.ar(SimpleShop.class).getEntity();

                assertNotNull(entity);

                dao.ar(SimpleShop.class)
                        .strict(false)
                        .orWhere(new SqlBuilder.Condition() {
                            @Override
                            public void where(Sql where) {
                                where.where("title", "测试");
                            }
                        })
                        .orWhere("title", "测试2")
                        .where("id", Symbol.EQ, "testBusiness")
                        .where(new SqlBuilder.Condition() {
                            @Override
                            public void where(Sql where) {
                                where.like("title", SqlBuilder.Like.MATCH_BOTH, "测试");
                            }
                        })
                        .select("id,title")
                        .get("id");

            }
        });

    }


    @Test
    public void testDbStruct() {


        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) throws Exception {
                dao.getDbStructFactory().getTriggers();
                dao.getDbStructFactory().getNameAndComments();

                dao.getDbStructFactory().getSequences();

                dao.getDbStructFactory().getTableNames();
                dao.getDbStructFactory().getTableMeta("shp_product");

                assertNotNull(dao.getDbStructFactory().getTableNames());
            }
        });

    }
}
