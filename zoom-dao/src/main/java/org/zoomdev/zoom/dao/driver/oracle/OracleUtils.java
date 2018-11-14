package org.zoomdev.zoom.dao.driver.oracle;

public class OracleUtils {


    public static String dropIfExistsSequence(String sequence){
        String str = "declare num number; begin \n" +
                "select count(1) into num from all_sequences where SEQUENCE_NAME = '%s';" +
                "if num > 0 then " +
                "execute immediate 'drop sequence %s';" +
                "end if;" +
                "end;";

        return String.format(str,sequence.toUpperCase(),sequence);
    }
}
