import junit.framework.TestCase;

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


//    public void test() {
//
//        DataSourceProvider provider = new DruidDataSourceProvider(
//                new MysqlConnDescription(
//                        "localhost",
//                        13306,
//                        "zoom",
//                        "root",
//                        "root"
//                )
//        );
//
//
//        Dao dao = new ZoomDao(provider.getDataSource(), false);
//
//
//        TableMeta tableMeta = dao.getDbStructFactory().getTableMeta(dao.ar(), "(select * from r_book) a");
//        System.out.println(tableMeta);
////		Book book = dao.ar(Book.class).get("1");
////
////
////		book.setTitle("Test");
////
////		dao.ar(Book.class).update(book);
////
//
//        EAr<Record> ar = dao.record("r_book").filter("^id|title$");
//
//        System.out.println(ar.find());
//
//        Record record = new Record();
//        record.put("id", 1001);
//        record.put("title", "jlkfjdf");
//
//        ar.insert(record);
//
//        System.out.println(record);
//
//    }
}
