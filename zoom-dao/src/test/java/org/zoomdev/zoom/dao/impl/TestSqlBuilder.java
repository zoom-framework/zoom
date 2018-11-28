package org.zoomdev.zoom.dao.impl;

import org.junit.Test;
import org.zoomdev.zoom.common.expression.Symbol;
import org.zoomdev.zoom.common.utils.MapUtils;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Sql;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.driver.SqlDriver;
import org.zoomdev.zoom.dao.driver.mysql.MysqlDriver;
import org.zoomdev.zoom.dao.driver.oracle.OracleDriver;

import static junit.framework.Assert.assertEquals;

public class TestSqlBuilder {

    @Test
    public void testWhere() {
        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);


        sqlBuilder.whereIn("id", 1, 2, 3, 4);
        assertEquals(sqlBuilder.where.toString(), "id IN (?,?,?,?)");
        sqlBuilder.clear(true);

        sqlBuilder.whereNotIn("id", 1, 2, 3, 4);
        assertEquals(sqlBuilder.where.toString(), "id NOT IN (?,?,?,?)");
        sqlBuilder.clear(true);

        sqlBuilder.whereCondition("id=? and name=?", 1, 2);
        assertEquals(sqlBuilder.values.get(0), 1);
        assertEquals(sqlBuilder.values.get(1), 2);
        assertEquals(sqlBuilder.where.toString(), "id=? and name=?");
        sqlBuilder.clear(true);

        sqlBuilder.whereNull("name");
        assertEquals(sqlBuilder.values.size(), 0);
        assertEquals(sqlBuilder.where.toString(), "name IS NULL");
        sqlBuilder.clear(true);


        sqlBuilder.whereNotNull("name");
        assertEquals(sqlBuilder.values.size(), 0);
        assertEquals(sqlBuilder.where.toString(), "name IS NOT NULL");
        sqlBuilder.clear(true);

        sqlBuilder.where("id", 1).orWhere("name", 2);
        assertEquals(sqlBuilder.values.get(0), 1);
        assertEquals(sqlBuilder.values.get(1), 2);
        assertEquals(sqlBuilder.where.toString(), "id=? OR name=?");
        sqlBuilder.clear(true);


        sqlBuilder.where(new SqlBuilder.Condition() {
            @Override
            public void where(Sql where) {
                where.where("id", 1)
                        .orLike("name", SqlBuilder.Like.MATCH_BOTH, "张");
            }
        });


        assertEquals(sqlBuilder.where.toString(), "(id=? OR name LIKE ?)");
        sqlBuilder.clear(true);


        sqlBuilder.where(new SqlBuilder.Condition() {
            @Override
            public void where(Sql where) {
                where.where("table1.id", 1)
                        .orLike("table1.name", SqlBuilder.Like.MATCH_BOTH, "张");
            }
        });

        assertEquals(sqlBuilder.where.toString(),
                "(table1.id=? OR table1.name LIKE ?)");
        sqlBuilder.clear(true);


        sqlBuilder.where(new SqlBuilder.Condition() {
            @Override
            public void where(Sql where) {
                where.where("table1.id", 1)
                        .orLike("table1.name", SqlBuilder.Like.MATCH_BOTH, "张");
            }
        }).orWhere(new SqlBuilder.Condition() {
            @Override
            public void where(Sql where) {
                where.where("table2.id2", 1)
                        .whereIn("table2.name2", "1", "2");
            }
        });

        assertEquals(sqlBuilder.where.toString(),
                "(table1.id=? OR table1.name LIKE ?) OR (table2.id2=? AND table2.name2 IN (?,?))");
        sqlBuilder.clear(true);


    }


    @Test(expected = DaoException.class)
    public void testEmptyCondition() {
        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.where(new SqlBuilder.Condition() {
            @Override
            public void where(Sql where) {

            }
        }).orWhere(new SqlBuilder.Condition() {
            @Override
            public void where(Sql where) {

            }
        });
    }


    @Test
    public void testOrderBy() {
        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.orderBy("id", SqlBuilder.Sort.DESC);

        assertEquals(sqlBuilder.orderBy.toString(),
                " ORDER BY id DESC");
        sqlBuilder.clear(true);


        sqlBuilder.orderBy("id", SqlBuilder.Sort.DESC)
                .orderBy("name", SqlBuilder.Sort.ASC);

        assertEquals(sqlBuilder.orderBy.toString(),
                " ORDER BY id DESC,name ASC");
        sqlBuilder.clear(true);
    }

    @Test
    public void testGroupBy() {
        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.groupBy("id");

        assertEquals(sqlBuilder.groupBy.toString(),
                " GROUP BY id");
        sqlBuilder.clear(true);


        sqlBuilder.groupBy("id,name");

        assertEquals(sqlBuilder.groupBy.toString(),
                " GROUP BY id,name");
        sqlBuilder.clear(true);
    }


    @Test
    public void testHaving() {
        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.having("id", Symbol.GT, 1);


        assertEquals(sqlBuilder.having.toString(),
                "id>?");
        assertEquals(sqlBuilder.values.get(0), 1);
        sqlBuilder.clear(true);
    }


    @Test
    public void testSelect() {

        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.table("student");
        sqlBuilder.buildSelect();

        assertEquals(sqlBuilder.sql.toString(),
                "SELECT * FROM student");
        sqlBuilder.clear();


        sqlBuilder.table("student").orderBy("id", SqlBuilder.Sort.ASC);
        sqlBuilder.buildSelect();
        assertEquals(sqlBuilder.sql.toString(),
                "SELECT * FROM student ORDER BY id ASC");
        sqlBuilder.clear();


        sqlBuilder.table("student")
                .select("id")
                .selectAvg("score", "score")
                .where("cls_id", 1)
                .where("score", Symbol.GT, 60)
                .orderBy("id", SqlBuilder.Sort.ASC)
                .groupBy("score")
                .having("AVG(score)", Symbol.GT, 60);
        sqlBuilder.buildSelect();
        assertEquals(sqlBuilder.sql.toString(),
                "SELECT id,AVG(score) AS score FROM student WHERE cls_id=? AND score>? GROUP BY score HAVING AVG(score)>? ORDER BY id ASC");
        sqlBuilder.clear();

    }


    @Test
    public void testPage() {

        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);
        sqlBuilder.table("student").buildLimit(0, 30);
        assertEquals(sqlBuilder.sql.toString(), "SELECT * FROM student LIMIT ?,?");
        assertEquals(sqlBuilder.values.get(0), 0);
        assertEquals(sqlBuilder.values.get(1), 30);

        sqlBuilder.clear();

        driver = new OracleDriver();
        String expect = "SELECT * FROM (SELECT A.*, ROWNUM R FROM (SELECT * FROM student) A WHERE ROWNUM <= ?) B WHERE R > ?";
        sqlBuilder = new SimpleSqlBuilder(driver);
        sqlBuilder.table("student").buildLimit(0, 30);
        assertEquals(sqlBuilder.values.get(0), 30);
        assertEquals(sqlBuilder.values.get(1), 0);
        assertEquals(sqlBuilder.sql.toString(), expect);
        sqlBuilder.clear();


        driver = new MysqlDriver();
        sqlBuilder = new SimpleSqlBuilder(driver);
        sqlBuilder.table("student").buildLimit(30, 30);
        assertEquals(sqlBuilder.sql.toString(), "SELECT * FROM student LIMIT ?,?");
        assertEquals(sqlBuilder.values.get(0), 30);
        assertEquals(sqlBuilder.values.get(1), 30);

        sqlBuilder.clear();

        driver = new OracleDriver();
        expect = "SELECT * FROM (SELECT A.*, ROWNUM R FROM (SELECT * FROM student) A WHERE ROWNUM <= ?) B WHERE R > ?";
        sqlBuilder = new SimpleSqlBuilder(driver);
        sqlBuilder.table("student").buildLimit(40, 30);
        assertEquals(sqlBuilder.values.get(0), 70);
        assertEquals(sqlBuilder.values.get(1), 40);
        assertEquals(sqlBuilder.sql.toString(), expect);
        sqlBuilder.clear();


    }


    @Test
    public void testFunc() {


        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);
        sqlBuilder.table("student").selectMax("COUNT", "COUNT");
        sqlBuilder.buildSelect();
        assertEquals(sqlBuilder.sql.toString(), "SELECT MAX(COUNT) AS COUNT FROM student");
        sqlBuilder.clear();

        sqlBuilder.table("student").selectMin("COUNT", "COUNT");
        sqlBuilder.buildSelect();
        assertEquals(sqlBuilder.sql.toString(), "SELECT MIN(COUNT) AS COUNT FROM student");
        sqlBuilder.clear();

        sqlBuilder.table("student").selectAvg("COUNT", "COUNT");
        sqlBuilder.buildSelect();
        assertEquals(sqlBuilder.sql.toString(), "SELECT AVG(COUNT) AS COUNT FROM student");
        sqlBuilder.clear();

        sqlBuilder.table("student").selectSum("COUNT", "COUNT");
        sqlBuilder.buildSelect();
        assertEquals(sqlBuilder.sql.toString(), "SELECT SUM(COUNT) AS COUNT FROM student");
        sqlBuilder.clear();

        sqlBuilder.table("student").selectCount("COUNT");
        sqlBuilder.buildSelect();
        assertEquals(sqlBuilder.sql.toString(), "SELECT COUNT(*) AS COUNT FROM student");
        sqlBuilder.clear();

    }


    @Test
    public void testInsert() {
        SqlDriver driver = new MysqlDriver();
        SqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.table("student").set("id", 1)
                .set("name", "张三")
                .set("class", "三班")
                .buildInsert();
        assertEquals(sqlBuilder.sql(), "INSERT INTO student (`id`,`name`,`class`) VALUES (?,?,?)");
        assertEquals(sqlBuilder.values().get(0), 1);
        assertEquals(sqlBuilder.values().get(1), "张三");
        assertEquals(sqlBuilder.values().get(2), "三班");
        sqlBuilder.clear();

        sqlBuilder.table("student")
                .setAll(MapUtils.asMap(
                        "id", 1,
                        "name", "张三",
                        "class", "三班"
                ))
                .buildInsert();
        assertEquals(sqlBuilder.sql(), "INSERT INTO student (`id`,`name`,`class`) VALUES (?,?,?)");
        assertEquals(sqlBuilder.values().get(0), 1);
        assertEquals(sqlBuilder.values().get(1), "张三");
        assertEquals(sqlBuilder.values().get(2), "三班");

        sqlBuilder.clear();

    }


    @Test
    public void testUpdate() {
        SqlDriver driver = new MysqlDriver();
        SqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.table("student")
                .set("id", 1)
                .set("name", "张三")
                .set("class", "三班")
                .buildUpdate();
        assertEquals(sqlBuilder.sql(), "UPDATE student SET `id`=?,`name`=?,`class`=?");
        assertEquals(sqlBuilder.values().get(0), 1);
        assertEquals(sqlBuilder.values().get(1), "张三");
        assertEquals(sqlBuilder.values().get(2), "三班");
        sqlBuilder.clear();

        sqlBuilder.table("student")
                .setAll(MapUtils.asMap(
                        "id", 1,
                        "name", "张三",
                        "class", "三班"
                ))
                .buildUpdate();
        assertEquals(sqlBuilder.sql(), "UPDATE student SET `id`=?,`name`=?,`class`=?");
        assertEquals(sqlBuilder.values().get(0), 1);
        assertEquals(sqlBuilder.values().get(1), "张三");
        assertEquals(sqlBuilder.values().get(2), "三班");

        sqlBuilder.clear();
    }


    @Test
    public void testSelect2() {
        SqlDriver driver = new MysqlDriver();
        SimpleSqlBuilder sqlBuilder = new SimpleSqlBuilder(driver);

        sqlBuilder.select("id,  name");
        assertEquals(sqlBuilder.select.toString(),
                "id,name");
        sqlBuilder.clear();


        sqlBuilder.select("table1.id,tabl22.name");
        assertEquals(sqlBuilder.select.toString(),
                "table1.id,tabl22.name");
        sqlBuilder.clear();


    }
}
