package org.zoomdev.zoom.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.dao.DaoException;
import org.zoomdev.zoom.dao.Record;
import org.zoomdev.zoom.dao.SqlBuilder;
import org.zoomdev.zoom.dao.adapters.EntityField;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

public class RecordEntity extends AbstractEntity<Record> {




    RecordEntity(String table,
                 EntityField[] entityFields,
                 EntityField[] primaryKeys,
                 AutoEntity autoEntity,
                 Map<String, String> namesMap) {
        super(table, entityFields, primaryKeys, autoEntity, namesMap);
    }

    @Override
    public Class<Record> getType() {
        return Record.class;
    }

    @Override
    public Object newInstance() {
        return new Record();
    }

    @Override
    public void setQuerySource(SqlBuilder builder) {
        builder.table(table);
    }

    private Map<String,EntityField> pool = new ConcurrentHashMap<String, EntityField>();

    public List<EntityField> select(
            List<EntityField> holder,
            Iterable<String> fields
    ){
        for(String key : fields){
            EntityField field = pool.get(key);
            if(field==null){
                Matcher matcher;
                String fieldName;
                String alias = null;
                if( (matcher=  BuilderKit.AS_PATTERN.matcher(key)).matches() ){
                    fieldName = matcher.group(1);
                    alias = matcher.group(2);

                }else{
                    fieldName = key;
                }
                RecordEntityField entityField = (RecordEntityField) tryToFind(fieldName);
                if(entityField==null){
                    throw new DaoException("找不到"+key+"对应的字段,所有可能的字段为"+StringUtils.join(getAvailableFields()));
                }

                if(alias!=null){
                    try {
                        RecordEntityField recordEntityField = (RecordEntityField) entityField.clone();
                        recordEntityField.field = alias;
                        pool.put(key,recordEntityField);
                        field = recordEntityField;
                    } catch (CloneNotSupportedException e) {
                        throw new DaoException(e);
                    }
                }else{
                    field = entityField;
                    pool.put(key,entityField);
                }
            }
            holder.add(field);
        }

        return holder;
    }

}
