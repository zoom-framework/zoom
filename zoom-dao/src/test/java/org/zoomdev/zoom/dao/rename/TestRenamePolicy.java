package org.zoomdev.zoom.dao.rename;

import junit.framework.TestCase;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyFactory;
import org.zoomdev.zoom.dao.alias.impl.*;

public class TestRenamePolicy extends TestCase {


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

    public void testAliasPolicyFactory(){

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
    }

}
