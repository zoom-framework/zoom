package org.zoomdev.zoom.dao.driver.oracle;

import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;

import java.util.List;

public class SimpleOracleAutoIncreaseProvider implements OracleAutoIncreaseProvider {
    @Override
    public void buildAutoIncrease(TableBuildInfo table, ColumnMeta autoColumn, List<String> sqlList) {
        String sequence = String.format("%s_sequence", table.getName());
        String str = OracleUtils.dropIfExistsSequence(sequence);
        sqlList.add(str);

      //  sqlList.add(String.format("DROP SEQUENCE %s_sequence",table.getName()));

        sqlList.add(String.format("create sequence %s_sequence minvalue 1 maxvalue 999999999999999999999999999 start with 1 increment by 1 cache 20",table.getName()));

        String $sql = "CREATE OR REPLACE TRIGGER %s_increase " +
                "BEFORE insert ON %s FOR EACH ROW ";
        $sql += "begin\n";
        $sql += "select %s.nextval into :New.%s from dual;\n";
        $sql += "end";

        sqlList.add(String.format($sql,
                table.getName(),
                table.getName(),
                sequence,autoColumn.getName()));

    }
}
