package org.zoomdev.zoom.dao.driver;

import org.zoomdev.zoom.dao.Ar;
import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.Collection;


public interface DbStructFactory {

	/**
	 * 表名称和注释
	 */
	class TableNameAndComment{
		public String getName() {
			return name;
		}

		public String getComment() {
			return comment;
		}

		private String name;
		private String comment;

        public void setName(String name) {
            this.name = name;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public TableNameAndComment() {

        }

		public TableNameAndComment(String name, String comment) {
			this.name = name;
			this.comment = comment;
		}
	}

	/**
	 * 获取所有的表名称
	 * @param ar
	 * @return
	 */
	Collection<String> getTableNames( Ar ar );

	/**
	 * 获取基础的tableMeda信息,只有jvm获取的信息
	 * @param ar
	 * @param tableName
	 * @return
	 */
	TableMeta getTableMeta(Ar ar,String tableName);

	/**
	 * 填充更加详细的信息，必须要有访问系统表的权限,没有也不影响一般的查询
	 * @param ar
	 * @param meta
	 */
	void fill(Ar ar,TableMeta meta);
	
	/**
	 * 获取表的名称和注释，必须要有访问系统表的权限
	 * @param ar
	 * @return
	 */
	Collection<TableNameAndComment> getNameAndComments( Ar ar );

	/**
	 * 清除缓存
	 */
    void clearCache();
}
