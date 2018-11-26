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

    private String sequenceName="%s_sequence";
    private String triggerName = "%s_increase";


    public SimpleOracleAutoIncreaseProvider(){

    }

    public SimpleOracleAutoIncreaseProvider(
            String sequenceName,
            String triggerName
    ) {
        this.sequenceName = sequenceName;
        this.triggerName = triggerName;
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

        if(!(tableMeta.getPrimaryKeys().length == 1 && columnMeta.isPrimary()) ){
            return null;
        }

        if(containsSpecialTrigger(dao,tableMeta.getName())){
            return new DatabaseAutoGenerateKey();
        }

        //看下sequence是否存在

        if(containSpecialSequence(dao,tableMeta.getName())){
            return new SequenceAutoGenerateKey(getSepcialSequenceName(tableMeta.getName()));
        }

        return null;
    }

    private boolean containsSpecialTrigger(Dao dao,String table){
        Map<String,Collection<String>> map = dao.getDbStructFactory().getTriggers();
        Collection<String> triggers = map.get(table.toUpperCase());
        if(triggers!=null && triggers.contains(getSepcialTriggerName(table))){
            return true;
        }
        return false;
    }

    private boolean containSpecialSequence(Dao dao,String table){

        if(dao.getDbStructFactory().getSequences().contains(getSepcialSequenceName(table))){
            return true;
        }

        return false;
    }


    private String getSepcialTriggerName(String table){
        return String.format(triggerName,table).toUpperCase();
    }

    private String getSepcialSequenceName(String table){
        return String.format(sequenceName,table).toUpperCase();
    }

    /**
     * 本表是否包含有特殊命名的trigger?
     * 数据库中是否包含有特殊命名的sequence?
     * @param dao
     * @param tableMeta
     * @param columnMeta
     * @return
     */
    @Override
    public boolean isAuto(Dao dao, TableMeta tableMeta, ColumnMeta columnMeta) {


        return (tableMeta.getPrimaryKeys().length == 1 && columnMeta.isPrimary()) && (containsSpecialTrigger(dao,tableMeta.getName())
                || containSpecialSequence(dao,columnMeta.getName()));
    }


}
