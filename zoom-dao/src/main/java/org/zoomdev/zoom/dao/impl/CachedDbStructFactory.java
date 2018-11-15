package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.dao.driver.DbStructFactory;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedDbStructFactory implements DbStructFactory,Destroyable {

    private DbStructFactory factory;

    //保证与表名称不一样就行了
    private static final String NAME_AND_COMMENT_KEY_NAME = "#@names";
    private static final String ALL_TRIGGERS = "#@triggers";
    private static final String ALL_SEQUENCES = "#@sequences";


    public CachedDbStructFactory(DbStructFactory factory) {
        this.factory = factory;
    }

    /**
     * 缓存
     */
    private Map<String, Object> pool = new ConcurrentHashMap<String, Object>();

    public <T> T get(String key) {
        return (T) pool.get(key);
    }

    @Override
    public Collection<String> getTableNames() {
        List<String> array = new ArrayList<String>();
        for (TableNameAndComment data : getNameAndComments()) {
            array.add(data.getName());
        }
        return array;
    }


    @Override
    public TableMeta getTableMeta(final String tableName) {
        TableMeta tableMeta= (TableMeta) SingletonUtils.liteDoubleLockMap(pool, tableName, new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return factory.getTableMeta(tableName);
            }
        });

        return tableMeta;
    }


    @Override
    public Collection<TableNameAndComment> getNameAndComments() {
        return (Collection<TableNameAndComment>) SingletonUtils.liteDoubleLockMap(pool, NAME_AND_COMMENT_KEY_NAME, new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return factory.getNameAndComments();
            }
        });
    }

    @Override
    public Map<String,Collection<String>> getTriggers() {
        return (Map<String, Collection<String>>) SingletonUtils.liteDoubleLockMap(pool, ALL_TRIGGERS, new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return factory.getTriggers();
            }
        });
    }

    @Override
    public Collection<String> getSequences() {
        return (Collection<String>) SingletonUtils.liteDoubleLockMap(pool, ALL_SEQUENCES, new SingletonUtils.SingletonInit<Object>() {
            @Override
            public Object create() {
                return factory.getSequences();
            }
        });
    }

    @Override
    public void fill(TableMeta tableMeta) {

    }

    @Override
    public void clearCache() {
        pool.clear();
    }

    @Override
    public void destroy() {
        Classes.destroy(pool);
    }
}
