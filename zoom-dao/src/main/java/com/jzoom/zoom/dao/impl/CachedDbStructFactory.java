package com.jzoom.zoom.dao.impl;

import com.jzoom.zoom.common.designpattern.SingletonUtils;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.driver.DbStructFactory;
import com.jzoom.zoom.dao.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedDbStructFactory implements DbStructFactory {

    private DbStructFactory factory;

    //保证与表名称不一样就行了
    private static final String NAMES = "#@names";


    public CachedDbStructFactory(DbStructFactory factory){
        this.factory = factory;
    }
    /**
     * 缓存
     */
    private Map<String,Object> pool = new ConcurrentHashMap<String, Object>();

    public <T> T get(String key){
        return (T)pool.get(key);
    }

    @Override
    public Collection<String> getTableNames(Ar ar) {
        List<String> array = new ArrayList<String>();
        for(TableNameAndComment data : getNameAndComments(ar)){
            array.add(data.getName());
        }
        return array;
    }


    @Override
    public TableMeta getTableMeta(final Ar ar, final String tableName) {
        return (TableMeta)SingletonUtils.liteDoubleLockMap(pool, tableName, new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return factory.getTableMeta(ar,tableName);
            }
        });
    }

    @Override
    public void fill(Ar ar, TableMeta meta) {
        if(meta.getComment()!=null)return;
        factory.fill(ar,meta);
    }

    @Override
    public Collection<TableNameAndComment> getNameAndComments(final Ar ar) {
        return (Collection<TableNameAndComment>)SingletonUtils.liteDoubleLockMap(pool, NAMES, new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return factory.getNameAndComments(ar);
            }
        });
    }

    @Override
    public void clearCache() {
        pool.clear();
    }
}
