package org.zoomdev.zoom.dao.migrations;

/**
 * dao.newBuilder()
 * .create("user)
 * .add("id").integer(4).primaryKey().autoIncreament()
 * .add("name").varchar(30).notNull().comment("用户")
 * .create("class")
 * .add("code").varchar(30).notNull().primaryKey()
 * ...
 * .build()
 */
public interface DatabaseBuilder {


    DatabaseBuilder dropIfExists(String table);

    DatabaseBuilder createIfNotExists(String table);

    // commments current context is table, for table, current context is column, for column
    DatabaseBuilder comment(String comment);

    DatabaseBuilder createTable(String table);

    // add column
    DatabaseBuilder add(String column);

    // modify
    DatabaseBuilder modify(String table, String column);

    // Column start
    DatabaseBuilder string(int len);

    DatabaseBuilder text();

    DatabaseBuilder timestamp();

    DatabaseBuilder date();

    DatabaseBuilder integer();

    DatabaseBuilder bigInt();

    DatabaseBuilder number();

    DatabaseBuilder notNull();

    DatabaseBuilder primaryKey();

    /// for mysql
    DatabaseBuilder autoIncement();

    DatabaseBuilder unique();

    DatabaseBuilder index();


    String buildSql();

//
//    DatabaseBuilder createTrigger(String name);
//
//    DatabaseBuilder createSequence();

    void build();


    // create a table from class directly
    void build(Class<?> type, boolean dropIfExists);

}
