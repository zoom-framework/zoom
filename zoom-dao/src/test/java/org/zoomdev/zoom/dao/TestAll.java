package org.zoomdev.zoom.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zoomdev.zoom.dao.impl.TestTrans;
import org.zoomdev.zoom.dao.impl.builder.TestBuilder;
import org.zoomdev.zoom.dao.impl.entity.TestEntity;
import org.zoomdev.zoom.dao.impl.TestSqlBuilder;
import org.zoomdev.zoom.dao.impl.module.TestModule;
import org.zoomdev.zoom.dao.impl.record.TestRecord;
import org.zoomdev.zoom.dao.impl.struct.TestDbStruct;
import org.zoomdev.zoom.dao.rename.TestRenamePolicy;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestRenamePolicy.class,TestSqlBuilder.class,
    TestEntity.class,TestBuilder.class,TestRecord.class,TestTrans.class,
        TestDbStruct.class,
        TestModule.class})
public class TestAll {

}
