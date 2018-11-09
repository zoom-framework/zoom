package com.jzoom.zoom.dao.driver.h2;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.common.utils.MapUtils;
import com.jzoom.zoom.dao.Ar;
import com.jzoom.zoom.dao.Record;
import com.jzoom.zoom.dao.driver.AbsDbStruct;
import com.jzoom.zoom.dao.meta.ColumnMeta;
import com.jzoom.zoom.dao.meta.ColumnMeta.KeyType;
import com.jzoom.zoom.dao.meta.TableMeta;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class H2DbStrict extends AbsDbStruct {

	private String dbName;

	public H2DbStrict(String dbName) {
		this.dbName = dbName;
	}

	private static final Log log = LogFactory.getLog(H2DbStrict.class);


	@Override
	public Collection<String> getTableNames(Ar ar) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<TableNameAndComment> getNameAndComments(Ar ar) {
		List<Record> list = ar.table("information_schema.tables")
				.select("TABLE_NAME as NAME,REMARKS AS COMMENT")
				.where("TABLE_SCHEMA", "PUBLIC")
			.find();


        List<TableNameAndComment> result = new ArrayList<TableNameAndComment>(list.size());
		for (Record record : list) {
            TableNameAndComment data = Caster.to(record,TableNameAndComment.class);
            result.add(data);
		}

        for (TableNameAndComment n : result) {
			n.setName(StringUtils.lowerCase(n.getName()));
        }

		return  result;
	}

	@Override
	public void fill(Ar ar, TableMeta meta) {

		List<Record> list = ar
				.table("information_schema.columns")
				.select("TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,SEQUENCE_NAME,CHARACTER_MAXIMUM_LENGTH,REMARKS,COLUMN_DEFAULT")
				.where("TABLE_SCHEMA", "PUBLIC")
				.where("TABLE_NAME", meta.getName().toUpperCase()).find();
		//index
		List<Record> indexes = ar.table("INFORMATION_SCHEMA.indexes").select("COLUMN_NAME,INDEX_TYPE_NAME")
					.where("TABLE_NAME", meta.getName().toUpperCase()).find();
		Map<String,String> indexesMap = MapUtils.toKeyAndLabel(indexes, "column_name", "index_type_name");

		for (Record record : list) {
			String column = record.getString("column_name");
			ColumnMeta columnMeta = meta.getColumn(column);
			if (columnMeta == null) {
				// 没有？不可能
				log.warn("找不到对应的字段:" + column);
				continue;
			}

			columnMeta.setComment(record.getString("remarks"));
			// 常用的
			columnMeta.setAuto(!StringUtils.isEmpty(record.getString("sequence_name")));
			String keyType = indexesMap.get(record.getString("column_name"));
			if(StringUtils.startsWith(keyType, "PRIMARY")) {
				columnMeta.setKeyType(KeyType.PRIMARY);
			}else if(StringUtils.startsWith(keyType, "UNIQUE")) {
				columnMeta.setKeyType(KeyType.UNIQUE);
			}
			
			columnMeta.setDefaultValue(record.getString("column_default"));
			columnMeta.setNullable(record.getString("is_nullable").equals("YES"));
			
			columnMeta.setMaxLen(record.getInt("character_maximum_length"));
			columnMeta.setRawType(record.getString("data_type"));

		}

	}

}
