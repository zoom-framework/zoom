package org.zoomdev.zoom.http.caster;

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


}
