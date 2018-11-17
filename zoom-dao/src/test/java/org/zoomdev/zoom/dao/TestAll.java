package org.zoomdev.zoom.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zoomdev.zoom.dao.impl.builder.TestBuilder;
import org.zoomdev.zoom.dao.impl.entity.EntityTestDao;
import org.zoomdev.zoom.dao.impl.TestSqlBuilder;
import org.zoomdev.zoom.dao.impl.record.TestRecord;
import org.zoomdev.zoom.dao.rename.TestRenamePolicy;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestRenamePolicy.class,TestSqlBuilder.class,
    EntityTestDao.class,TestBuilder.class,TestRecord.class})
public class TestAll {

}
