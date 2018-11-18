package org.zoomdev.zoom.dao.impl;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;

import static org.junit.Assert.assertEquals;

public class TestTrans extends AbstractDaoTest {
    @Override
    protected void process(final Dao dao) {


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
                                            throw new RuntimeException("Something bad!");
                                        }
                                    });
                                }
                            });

                        }
                    });
                }  finally {

                    assertEquals(productCount, getProductCount(dao));
                }


            }
        });

    }
}
