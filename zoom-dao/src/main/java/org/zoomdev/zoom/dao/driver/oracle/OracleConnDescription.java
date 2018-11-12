package org.zoomdev.zoom.dao.driver.oracle;

import org.zoomdev.zoom.dao.ConnectionDescriptor;

public class OracleConnDescription extends ConnectionDescriptor {

    private String dbName;

    public OracleConnDescription() {
        setDriverClass("oracle.jdbc.driver.OracleDriver");
    }

    public OracleConnDescription(String server, int port, String db, String user, String password) {
        this();
        setPassword(password);
        setUser(user);
        setServer(server);
        setDbName(db);
        setPort(port);
    }

    public OracleConnDescription(String server, String db, String user, String password) {
        this(server, 1521, db, user, password);
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    @Override
    public String getJdbcUrl() {
        return "jdbc:oracle:thin:@" + getServer() + ":" + getPort() + ":" + dbName;
    }


}
