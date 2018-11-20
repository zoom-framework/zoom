package org.zoomdev.zoom.dao.impl.entity;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.common.utils.Converter;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.entities.*;
import org.zoomdev.zoom.dao.impl.AbstractDaoTest;
import org.zoomdev.zoom.dao.impl.Utils;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEntity extends AbstractDaoTest {


    @BeforeClass
    public static void setup() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                Utils.createTables(dao);
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
                simpleProduct.setImg("测试图片src" .getBytes());

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
    public void testShop() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                SimpleShop shop = dao.ar(SimpleShop.class).get("1");
                if (shop != null) {
                    dao.ar(SimpleShop.class).delete(shop);
                }


                shop = new SimpleShop();
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
                            public void where(SqlBuilder where) {
                                where.where("title", "测试");
                            }
                        })
                        .orWhere("title", "测试2")
                        .where("id", Symbol.EQ, "testBusiness")
                        .where(new SqlBuilder.Condition() {
                            @Override
                            public void where(SqlBuilder where) {
                                where.like("title", SqlBuilder.Like.MATCH_BOTH, "测试");
                            }
                        })
                        .select("id,title")
                        .get("id", "title");

            }
        });

    }
}
