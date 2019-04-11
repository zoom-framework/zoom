package org.zoomdev.zoom.common.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderedListTest {

    @Test
    public void test() {

        OrderedList<Object> list = new OrderedList<Object>();

        list.add(1, 0);
        list.add(2, -1);
        list.add(3, 2);


        Object[] objects = list.toArray(new Object[list.size()]);

        assertEquals(objects[0], 2);
        assertEquals(objects[1], 1);
        assertEquals(objects[2], 3);

        list.addAll(
                4, 5, 6
        );

        objects = list.toArray(new Object[list.size()]);

        assertEquals(objects[0], 2);
        assertEquals(objects[1], 1);
        assertEquals(objects[2], 3);
        assertEquals(objects[3], 4);
        assertEquals(objects[4], 5);
        assertEquals(objects[5], 6);


        list.clear();

    }
}
