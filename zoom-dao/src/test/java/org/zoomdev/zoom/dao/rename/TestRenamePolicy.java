package org.zoomdev.zoom.dao.rename;

import org.junit.Test;
import org.zoomdev.zoom.dao.Dao;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyFactory;
import org.zoomdev.zoom.dao.alias.NameAdapter;
import org.zoomdev.zoom.dao.alias.impl.*;
import org.zoomdev.zoom.dao.impl.AbstractDaoTest;
import org.zoomdev.zoom.dao.impl.Utils;

import static org.junit.Assert.assertEquals;

public class TestRenamePolicy extends AbstractDaoTest {


    @Test
    public void testAliasPolicy() {

        AliasPolicy policy = CamelAliasPolicy.DEFAULT;
        assertEquals(policy.getAlias("TEST_ALIAS_"), "testAlias");
        assertEquals(policy.getAlias("_TEST__ALIAS_"), "testAlias");

        policy = EmptyAliasPolicy.DEFAULT;
        assertEquals(policy.getAlias("_TEST__ALIAS_"), "_TEST__ALIAS_");


        policy = new PrefixAliasPolicy("TP_");
        assertEquals(policy.getAlias("TP_NULL"), "null");
        assertEquals(policy.getAlias("_TP_NULL"), "tpNull");

        policy = ToLowerCaseAiiasPolicy.DEFAULT;
        assertEquals(policy.getAlias("TP_NULL"), "tp_null");
        assertEquals(policy.getAlias("_TP_NULL"), "_tp_null");


    }

    @Test
    public void testAliasPolicyFactory() {

        AliasPolicyFactory factory = DetectPrefixAliasPolicyFactory.DEFAULT;
        AliasPolicy aliasPolicy = factory.getAliasPolicy(new String[]{
                "TP_ID",
                "TP_NAME",
                "TP_FACTORY",
                "RN_ID",
                "RN_NAME",
                "RN_TEST"
        });

        assertEquals(
                aliasPolicy.getAlias("TP_ID"),
                "id"
        );

        assertEquals(
                aliasPolicy.getAlias("RN_ID"),
                "rnId"
        );


        aliasPolicy = factory.getAliasPolicy(new String[]{
                "TP_ID",
                "TP_NAME",
                "RN_ID",
                "RN_NAME",
                "RN_TEST"
        });

        assertEquals(
                aliasPolicy.getAlias("TP_ID"),
                "tpId"
        );

        assertEquals(
                aliasPolicy.getAlias("RN_ID"),
                "rnId"
        );


        aliasPolicy = factory.getAliasPolicy(new String[]{
                "id",
                "name",
                "RN_ID",
                "RN_NAME",
                "RN_TEST"
        });

        assertEquals(
                aliasPolicy.getAlias("RN_ID"),
                "rnId"
        );

        assertEquals(
                aliasPolicy.getAlias("RN_NAME"),
                "rnName"
        );

        aliasPolicy = factory.getAliasPolicy(new String[]{
                "TP_ID",
                "TP_NAME",
                "TP_COUNT",
        });

        assertEquals(
                aliasPolicy.getAlias("TP_ID"),
                "id"
        );

        assertEquals(
                aliasPolicy.getAlias("TP_NAME"),
                "name"
        );


        aliasPolicy = factory.getAliasPolicy(new String[]{
                "TP_ID",
                "AR_NAME",
                "PT_FACTORY",
                "AN_ID",
                "BB_NAME",
                "CC_TEST"
        });

        assertEquals(aliasPolicy.getAlias("TP_ID"), "tpId");


        assertEquals(EmptyNameAdapter.DEFAULT.getColumnName("TEST_COLUMN"), "TEST_COLUMN");
        assertEquals(EmptyNameAdapter.DEFAULT.getFieldName("TEST_COLUMN"), "TEST_COLUMN");

        assertEquals(ToLowerCaseNameAdapter.DEFAULT.getColumnName("test_column"), "TEST_COLUMN");
        assertEquals(ToLowerCaseNameAdapter.DEFAULT.getFieldName("TEST_COLUMN"), "test_column");

        assertEquals(CamelNameAdapter.DEFAULT.getColumnName("testColumn"), "TEST_COLUMN");
        assertEquals(CamelNameAdapter.DEFAULT.getFieldName("TEST_COLUMN"), "testColumn");


        //map Name adapter


    }

    @Override
    protected void process(Dao dao) {

        Utils.createTestMapNameAdapter(dao);

        NameAdapter adapter = MapNameAdapter.fromEntity(dao.getEntity("test_map"));

        assertEquals(adapter.getFieldName("MP_ID"), "mpId");

        assertEquals(adapter.getColumnName("jpName"), "JP_NAME");

        assertEquals(adapter.getFieldName("NOT EXISTS"), "NOT EXISTS");

        assertEquals(adapter.getColumnName("NOT EXISTS"), "NOT EXISTS");


    }
}
