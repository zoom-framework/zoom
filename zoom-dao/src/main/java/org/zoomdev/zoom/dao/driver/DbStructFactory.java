package org.zoomdev.zoom.dao.driver;

import org.zoomdev.zoom.dao.meta.TableMeta;

import java.util.Collection;
import java.util.Map;


public interface DbStructFactory {

    /**
     * 表名称和注释
     */
    class TableNameAndComment {
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
     *
     * @return
     */
    Collection<String> getTableNames();

    /**
     * 获取基础的tableMeda信息,只有jvm获取的信息
     *
     * @param tableName
     * @return
     */
    TableMeta getTableMeta(String tableName);

    /**
     * 填充更加详细的信息，必须要有访问系统表的权限,没有也不影响一般的查询
     *
     * @param meta
     */
    void fill(TableMeta meta);

    /**
     * 获取表的名称和注释，必须要有访问系统表的权限
     *
     * @return
     */
    Collection<TableNameAndComment> getNameAndComments();


    /**
     * 所有trigger名称
     * @return
     */
    Map<String,Collection<String>> getTriggers();

    /**
     * 所有sequence名称
     * @return
     */
    Collection<String> getSequences();


    /**
     * 清除缓存
     */
    void clearCache();



}
