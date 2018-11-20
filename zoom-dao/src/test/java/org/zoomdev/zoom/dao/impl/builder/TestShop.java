package org.zoomdev.zoom.dao.impl.builder;

import org.junit.Test;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.annotations.AutoGenerate;
import org.zoomdev.zoom.dao.annotations.Column;
import org.zoomdev.zoom.dao.annotations.Table;
import org.zoomdev.zoom.dao.impl.AbstractDaoTest;
import org.zoomdev.zoom.dao.migrations.DatabaseBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestShop extends AbstractDaoTest {

    static final String IMAGE_FILE;

    static {
        ResScanner scanner = new ResScanner();
        try {
            scanner.scan();
            IMAGE_FILE = scanner.findFile("*").get(0).getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Table("product")
    public static class Product {


        @AutoGenerate
        Integer id;
        /// 表示不想用name，而是用title,这里可以写name,也可以写pro_name
        @Column("name")
        String title;


        String thumb;
        Double price;
        String info;

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

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
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

    @Override
    protected void process(Dao dao) {

        dao.builder()
                .dropIfExists("product")
                .createTable("product").comment("产品")
                .add("PRO_ID").comment("编号").integer().keyPrimary().autoIncement()
                .add("PRO_NAME").comment("名称").string(100).keyIndex().notNull()
                .add("PRO_PRICE").comment("价格").number().keyIndex().notNull()
                .add("PRO_INFO").comment("信息").text()
                .add("PRO_THUMB").comment("预览图地址").string(200)
                .add("PRO_IMG").comment("产品图片").blob()
                .add("PRO_COUNT").comment("产品库存").integer().defaultValue(100)
                .add("TP_ID").comment("类型编号").integer().keyIndex()
                .add("CREATE_AT").comment("创建时间").timestamp().defaultValue(new DatabaseBuilder.FunctionValue("CURRENT_TIMESTAMP"))

                .dropIfExists("type")
                .createTable("type").comment("类型")
                .add("TP_ID").comment("编号").integer().keyPrimary().autoIncement()
                .add("TP_TITLE").comment("标题").string(100).keyIndex().notNull()

                .build();

        doActionWithActiveRecord(dao);

    }

    @Test
    public void testEntityActiveRecord() {

        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) throws Exception {

                /// 插入商品
                Product product = new Product();
                product.setTitle("我的商品");
                product.setPrice(100.0D);
                product.setInfo("好长好长的描述");
                product.setThumb("图片的url");
                product.setImg(Io.readBytes(new File(IMAGE_FILE)));
                product.setCount(200);

                dao.ar(Product.class)
                        .insert(product);

                ///获取到自增id
                int insertId = product.getId();

                /// 修改商品
                product.setCount(300);
                product.setPrice(188D);
                product.setTitle("测试");

                dao.ar(Product.class)
                        .filter("count|price")        //增加一个更新过滤器
                        .update(product);

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


            }
        });

    }

    @Test
    public void testRawActiveRecord() {


        execute(new RunWithDao() {
            @Override
            public void run(Dao dao) {
                /// 插入商品 ,在默认情况下，往数据库方向的字段，将被改成大写,可以使用dao.setNameAdapter来修改默认行为
                Record product = Record.as(
                        "pro_name", "我的商品",
                        "pro_price", 100.0D,
                        "pro_info", "好长好长的描述",
                        "pro_thumb", "图片的url",
                        /// 在RawActiveRecord模式下不适配数据库字段类型，所以插入和更新二进制操作在某些数据库类型下不支持
                        // "pro_img", new File(IMAGE_FILE),
                        "pro_count", 200,
                        "tp_id", 1
                );
                dao.table("product")
                        .insert(product, "pro_id");

                ///获取到自增id
                int insertId = product.getInt("pro_id");

                /// 修改商品
                /// 使用链式的set,来设置或者使用setAll一次性设置要修改的字段
                dao.table("product")
                        .where("pro_id", insertId)
                        .set("pro_count", 3000)
                        .set("pro_price", 188D)
                        .update();

                //// 主键获取到商品
                Record record = dao.table("product").where("pro_id", insertId).get();
                /// 修改price和count成功
                assertEquals(record.getInt("pro_count"), 3000);
                assertEquals(record.getDouble("pro_price"), 188D, 0);

                /// 查询所有商品,仅仅查看pro_id,pro_name字段
                List<Record> list = dao.table("product").select("pro_id,pro_name").find();

                /// 分页查询
                Page<Record> page = dao.table("product")
                        .like("pro_name", SqlBuilder.Like.MATCH_BOTH, "我的")
                        .page(1, 30);

                /// 删除商品
                dao.table("product").where("pro_id", insertId).delete();
            }
        });

    }


    protected void doActionWithActiveRecord(Dao dao) {
        /// 插入商品
        Record product = Record.as(
                "name", "我的商品",
                "price", 100.0D,
                "info", "好长好长的描述",
                "thumb", "图片的url",
                "img", new File(IMAGE_FILE),
                "count", 200,
                "tpId", 1
        );
        dao.ar("product")
                .insert(product);

        ///获取到自增id
        int insertId = product.getInt("id");

        /// 修改商品
        product.set("count", 300);
        product.set("price", 188D);
        product.set("name", "测试");

        dao.ar("product")
                .filter("count|price")        //增加一个更新过滤器
                .update(product);

        //// 主键获取到商品
        Record record = dao.ar("product").get(insertId);
        /// 没有修改名称
        assertEquals(record.getString("name"), "我的商品");
        /// 修改price和count成功
        assertEquals(record.getInt("count"), 300);
        assertEquals(record.getDouble("price"), 188D, 0);

        /// 查询所有商品
        List<Record> list = dao.ar("product").find();

        /// 分页查询
        Page<Record> page = dao.ar("product")
                .like("name", SqlBuilder.Like.MATCH_BOTH, "我的")
                .page(1, 30);

        /// 删除商品
        dao.ar("product").delete(record);

    }
}
