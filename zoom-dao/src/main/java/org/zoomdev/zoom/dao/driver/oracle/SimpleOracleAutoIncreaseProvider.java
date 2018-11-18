package org.zoomdev.zoom.dao.driver.oracle;

import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.auto.AutoField;
import org.zoomdev.zoom.dao.auto.DatabaseAutoGenerateKey;
import org.zoomdev.zoom.dao.auto.SequenceAutoGenerateKey;
import org.zoomdev.zoom.dao.driver.AutoGenerateProvider;
import org.zoomdev.zoom.dao.meta.ColumnMeta;
import org.zoomdev.zoom.dao.meta.TableMeta;
import org.zoomdev.zoom.dao.migrations.TableBuildInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SimpleOracleAutoIncreaseProvider implements AutoGenerateProvider {


    public SimpleOracleAutoIncreaseProvider() {

    }


    @Override
    public void buildAutoIncrease(TableBuildInfo table, ColumnMeta autoColumn, List<String> sqlList) {
        String sequence = String.format("%s_sequence", table.getName());
        String str = OracleUtils.dropIfExistsSequence(sequence);
        sqlList.add(str);

        //  sqlList.add(String.format("DROP SEQUENCE %s_sequence",table.getName()));

        sqlList.add(String.format("create sequence %s_sequence minvalue 1 maxvalue 999999999999999999999999999 start with 1 increment by 1 cache 20", table.getName()));

        String $sql = "CREATE OR REPLACE TRIGGER %s_increase " +
                "BEFORE insert ON %s FOR EACH ROW\n";
        $sql += "begin\n";
        $sql += "select %s.nextval into :New.%s from dual;\n";
        $sql += "end;";

        sqlList.add(String.format($sql,
                table.getName(),
                table.getName(),
                sequence, autoColumn.getName()));

    }

    @Override
    public AutoField createAutoField(Dao dao, TableMeta tableMeta, ColumnMeta columnMeta) {
        //寻找一下是否有 table_increase的trigger
        Map<String, Collection<String>> triggers = dao.getDbStructFactory().getTriggers();
        String triggerName = (tableMeta.getName() + "_increase").toUpperCase();
        Collection<String> tableTriggers = triggers.get(tableMeta.getName().toUpperCase());
        if (tableTriggers != null && tableTriggers.contains(triggerName)) {
            return new DatabaseAutoGenerateKey();
        }

        //看下sequence是否存在
        String sequenceName = (tableMeta.getName() + "_sequence").toUpperCase();
        if (dao.getDbStructFactory().getSequences().contains(sequenceName)) {
            return new SequenceAutoGenerateKey(sequenceName);
        }


        return null;
    }


}
