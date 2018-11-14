package org.zoomdev.zoom.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.zoomdev.zoom.dao.builder.h2.TestH2Builder;
import org.zoomdev.zoom.dao.builder.mysql.TestMysqlBuilder;
import org.zoomdev.zoom.dao.impl.TestSqlBuilder;
import org.zoomdev.zoom.dao.rename.TestRenamePolicy;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestH2Builder.class,TestMysqlBuilder.class,
        TestRenamePolicy.class,TestSqlBuilder.class})
public class TestAll {
}
