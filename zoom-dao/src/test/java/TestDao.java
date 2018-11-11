import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.utils.Page;
import org.zoomdev.zoom.dao.*;
import org.zoomdev.zoom.dao.driver.mysql.MysqlConnDescription;
import org.zoomdev.zoom.dao.impl.ZoomDao;
import org.zoomdev.zoom.dao.provider.DruidDataSourceProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDao extends TestCase {


    /**
     * 比较一下， 正则表达式缓存和不缓存的速度
     */

    public void testDb() {
//		Db.register(dao);
//		Db.register(dao);
//		Db.register(dao);
//		Db.register(dao);
//		Db.register(dao);
//
//		assertEquals(Db.count, 5);
//		Db.visit(new Visitor<Dao>() {
//
//			@Override
//			public void visit(Dao data) {
//				System.out.println(dao);
//			}
//		});
//		Db.unregister(dao);
//		assertEquals(Db.count, 4);
//		Db.unregister(dao);
//		Db.unregister(dao);
//		Db.unregister(dao);
//		Db.unregister(dao);
//
//		assertEquals(Db.count, 0);

    }
    private Pattern pattern2 = Pattern.compile("([a-zA-Z0-9\\.]+)");

    public void test() {

        String test = "a.a = b.b And band.b is null or a.b no is null";

        Pattern pattern = Pattern.compile("[\\s]+and[\\s]+|[\\s]+or[\\s]+",Pattern.CASE_INSENSITIVE);

        Pattern pattern1 = Pattern.compile("[a-zA-Z0-9]+[\\s]*\\.[\\s]*[a-zA-Z0-9]+|[a-zA-Z0-9]+");


        Matcher matcher = pattern.matcher(test);
        int start = 0;
        while(matcher.find()){
            String str = test.substring(start,matcher.start());

            System.out.println(str);
            Matcher matcher1 = pattern1.matcher(str);
            while(matcher1.find()){
                System.out.println(matcher1.group());
            }

            start = matcher.end();
        }
        System.out.println(test.substring(start));


        String[] parts = test.split("[\\s]+and|or[\\s]+");

        System.out.println(StringUtils.join(parts,","));

        DataSourceProvider provider = new DruidDataSourceProvider(
                new MysqlConnDescription(
                        "localhost",
                        13306,
                        "zoom",
                        "root",
                        "root"
                )
        );


        Dao dao = new ZoomDao(provider.getDataSource(), false);

        dao.ar(Product.class).find();

        Product product = new Product();
        product.setInfo("hello");
        product.setName("title");
        product.setThumb("jifdf");
        dao.ar(Product.class).insert(product);

        dao.ar(Product.class).where("id",2)
                .orderBy("id",SqlBuilder.Sort.DESC)
                .filter("id|name").get();
        Page<Product> page = dao.ar(Product.class).where("id",2)
                .orderBy("id",SqlBuilder.Sort.DESC)
                .filter("id|name").page(1,30);

        System.out.println(page);


    }
}
