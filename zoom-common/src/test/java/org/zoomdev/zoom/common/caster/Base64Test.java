package org.zoomdev.zoom.common.caster;

import junit.framework.TestCase;

public class Base64Test extends TestCase {

    public void test(){


        String str1 = Base64.encodeToString("艰苦奋斗健身房");

        String str2 =  Base64.encodeToString("可交付剑荡四方".getBytes(),false);

        byte[] bytes1 = Base64.encodeToByte("见附件加23浮点十分十分".getBytes(),false);

        char[] char1 = Base64.encodeToChar("接口类附近的律师费经济法的".getBytes(),false);


        new String(Base64.decode(str1));

        new String(Base64.decode(bytes1));

        new String(Base64.decode(char1));

        new String(Base64.decodeFast(str1));

        new String(Base64.decodeFast(bytes1));

        new String(Base64.decodeFast(char1));



        assertEquals("艰苦奋斗就是发放",
            Base64.decodeBase64(
                    Base64.encodeToString("艰苦奋斗就是发放")
            )    );




    }
}
