package org.zoomdev.zoom.dao.impl;

import org.zoomdev.zoom.common.designpattern.SingletonUtils;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Entity;
import org.zoomdev.zoom.dao.EntityFactory;
import org.zoomdev.zoom.dao.Record;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedEntityFactory implements EntityFactory {


    private static class EntityKey{

        String[] tables;
        Class<?> type;

        int h;

        public EntityKey(Class<?> type,String...tables){
            this.type = type;
            this.tables = tables;
        }

        @Override
        public int hashCode() {
            int h = this.h;
            if (h == 0) {
                h = 31 + type.hashCode();
                for(String table : tables){
                    h = 31 * h + table.hashCode();
                }
                this.h = h;
            }
            return h;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof EntityKey){
                EntityKey key = (EntityKey)obj;
                if(key.type != this.type){
                    return false;
                }

                return Arrays.equals(key.tables,this.tables);

            }
            return false;
        }
    }


    public CachedEntityFactory(EntityFactory proxy) {
        this.proxy = proxy;
    }

    private EntityFactory proxy;
    private Map<EntityKey,Entity> map = new ConcurrentHashMap<EntityKey, Entity>();



    @Override
    public Entity getEntity(final Class<?> type) {
        if(Record.class.isAssignableFrom(type)){
            throw new DaoException("不支持Redord直接绑定Entity,需要制定至少一个表名称");
        }
        return SingletonUtils.liteDoubleLockMap(map, new EntityKey(type), new SingletonUtils.SingletonInit<Entity>() {
            @Override
            public Entity create() {
                return proxy.getEntity(type);
            }
        });
    }

    @Override
    public Entity getEntity(final Class<?> type, final String... tables) {
        return SingletonUtils.liteDoubleLockMap(map, new EntityKey(type,tables), new SingletonUtils.SingletonInit<Entity>() {
            @Override
            public Entity create() {
                return proxy.getEntity(type,tables);
            }
        });
    }

    @Override
    public void clearCache() {
        map.clear();
    }
}
