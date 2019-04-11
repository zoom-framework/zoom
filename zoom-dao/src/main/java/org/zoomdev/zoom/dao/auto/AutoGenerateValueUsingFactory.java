package org.zoomdev.zoom.dao.auto;

public class AutoGenerateValueUsingFactory extends DatabaseAutoGenerateKey {

//    private AutoGenerateValue factory;
//
//    public AutoGenerateValueUsingFactory(AutoGenerateValue factory) {
//        this.factory = factory;
//    }
//
//
//    @Override
//    public Object generateValue(Object entity, EntityField entityField) {
//        //当调用的时候，直接设置值
//        Object value = factory.nextVal();
//        entityField.set(entity, Caster.toType(value, entityField.getFieldType()));
//        return value;
//    }
//
//    //不是数据库自动生成
//    @Override
//    public boolean isDatabaseGeneratedKey() {
//        return false;
//    }
}