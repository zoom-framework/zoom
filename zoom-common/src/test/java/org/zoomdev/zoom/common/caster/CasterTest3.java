package org.zoomdev.zoom.common.caster;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CasterTest3 {

    /**
     *
     */
    @Test
    public void testArray() {

        String[] arr = new String[]{"1", "2", "3"};

        int[] arr2 = Caster.to(arr, int[].class);

        assertEquals(arr2[0], 1);

    }

    @Test
    public void testArray2() {

        String[] arr = new String[]{"1", "2", "3"};

        Integer[] arr2 = Caster.to(arr, Integer[].class);

        assertEquals(arr2[0], (Integer) 1);

    }

    public void it2it() {

    }

    public void set2list() {

    }


    static enum MyEnum {
        V1,
        V2
    }

    static enum MyEnum2 {


        V3(1),
        V4(2);

        private final int value;

        MyEnum2(int value) {
            this.value = value;
        }
    }

    public void testEnum() {

        assertEquals(MyEnum.V1, Caster.to("V1", MyEnum.class));
        assertEquals(MyEnum2.V3, Caster.to("V3", MyEnum2.class));


    }


}
