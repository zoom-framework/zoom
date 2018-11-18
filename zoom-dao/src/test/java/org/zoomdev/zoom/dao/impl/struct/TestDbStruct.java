package org.zoomdev.zoom.dao.impl.struct;

import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.impl.AbstractDaoTest;

public class TestDbStruct extends AbstractDaoTest {



    @Override
    protected void process(Dao dao) {
        dao.getDbStructFactory().getTriggers();
        dao.getDbStructFactory().getNameAndComments();

        dao.getDbStructFactory().getTableNames();


        dao.getDbStructFactory().getSequences();


        dao.getDbStructFactory().getTableMeta("shop");

    }
}
