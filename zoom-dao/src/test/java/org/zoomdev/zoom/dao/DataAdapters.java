package org.zoomdev.zoom.dao;

import org.zoomdev.zoom.dao.adapters.DataAdapter;

public class DataAdapters {

    /**
     * 这里最主要要实现格式的转化
     */
    public static class TimeStamp2String implements DataAdapter<String,String>{

        @Override
        public String toDbValue(String o) {
            return o;
        }

        @Override
        public String toEntityValue(String o) {
            return o;
        }
    }
}
