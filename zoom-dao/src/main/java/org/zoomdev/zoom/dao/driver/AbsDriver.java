package org.zoomdev.zoom.dao.driver;

import org.zoomdev.zoom.dao.adapters.StatementAdapter;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class AbsDriver implements SqlDriver {
    private static DefaultStatementAdapter defaultStatementAdapter = new DefaultStatementAdapter();

    /**
     * 应付绝大部分情况够了
     *
     * @author jzoom
     */
    private static class DefaultStatementAdapter implements StatementAdapter {

        @Override
        public void adapt(PreparedStatement ps, int index, Object value) throws SQLException {
            ps.setObject(index, value);
        }

    }

    public StatementAdapter get(Class<?> dataClass, Class<?> columnClass) {
        return defaultStatementAdapter;
    }

    @Override
    public StringBuilder protectColumn(StringBuilder sb, String name) {

        return sb.append(name);
    }

    @Override
    public String protectColumn(String name) {
        return name;
    }

    @Override
    public StringBuilder protectTable(StringBuilder sb, String name) {

        return sb.append(name);
    }



    protected static String buildCommentOnTable(String table, String comment) {
        return String.format("COMMENT ON TABLE %s IS '%s", table, comment);
    }


    protected static String buildCommentOnColumn(String table, String column, String comment) {
        return String.format("COMMENT ON %s.%s IS '%s", table, column, comment);
    }


    protected static void buildUnique(TableBuildInfo table, List<String> sqlList) {
        StringBuilder sb = new StringBuilder();
        //index
        for (ColumnMeta columnMeta : table.getColumns()) {

            if (columnMeta.isUnique()) {
                sb.setLength(0);
                sb.append("ALTER TABLE ")
                        .append(table.getName())
                        .append(" ADD CONSTRAINT UNI_")
                        .append(table.getName().toUpperCase())
                        .append("_")
                        .append(columnMeta.getName().toUpperCase())
                        .append(" UNIQUE ")
                        .append("(")
                        .append(columnMeta.getName())
                        .append(")");
                sqlList.add(sb.toString());
            }
        }
    }

    protected static void buildIndex(TableBuildInfo table, List<String> sqlList) {
        StringBuilder sb = new StringBuilder();
        //index
        for (ColumnMeta columnMeta : table.getColumns()) {

            if (columnMeta.isIndex()) {
                sb.setLength(0);
                sb.append("CREATE INDEX ")
                        .append("IDX_")
                        .append(table.getName())
                        .append("_")
                        .append(columnMeta.getName())
                        .append(" ON ")
                        .append(table.getName())
                        .append("(")
                        .append(columnMeta.getName())
                        .append(")");
                sqlList.add(sb.toString());
            }
        }
    }


    protected void buildComment(TableBuildInfo table, List<String> sqlList) {

        if (table.getComment() != null) {
            sqlList.add(String.format("COMMENT ON TABLE %s IS '%s'",
                    protectTable(table.getName()), table.getComment()));
        }
        for (ColumnMeta columnMeta : table.getColumns()) {

            if (columnMeta.getComment() != null) {

                sqlList.add(String.format("COMMENT ON COLUMN %s.%s IS '%s'",
                        protectTable(table.getName()),
                        protectColumn(columnMeta.getName()),
                        columnMeta.getComment()));
            }
        }

    }


    @Override
    public StatementAdapter getStatementAdapter(Class<?> fieldType, Class<?> columnType) {
        return null;
    }
}
