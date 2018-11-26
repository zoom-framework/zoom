package org.zoomdev.zoom.common.utils;

import junit.framework.TestCase;
import org.junit.Test;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.json.JSON;

import java.io.File;
import java.util.*;

public class CollectionUtilsTest extends TestCase {


    public void test(){
        List<String> counter = new ArrayList<String>();
        counter.add("1");
        counter.add("2");
        counter.add("3");
        counter.add("aa");

        List<Integer> numbers = CollectionUtils.map(counter, new Converter<String, Integer>() {
            @Override
            public Integer convert(String data) {
                if(PatternUtils.isInteger(data)){
                    return Integer.parseInt(data);
                }
                return null;
            }
        });

        assertEquals(numbers.size(),3);

        List<String> filteredNumbers = CollectionUtils.filter(counter, new Filter<String>() {
            @Override
            public boolean accept(String value) {
                return PatternUtils.isInteger(value);
            }
        });

        assertEquals(filteredNumbers.size(),3);


        final List<String> list = new ArrayList<String>();
        CollectionUtils.visit(counter, new Visitor<String>() {
            @Override
            public void visit(String data) {
                list.add(data);
            }
        });



    }


    public void testWithArray(){
        String[] counter = new String[]{"1","2","3","aa"};

        List<Integer> numbers = CollectionUtils.map(counter, new Converter<String, Integer>() {
            @Override
            public Integer convert(String data) {
                if(PatternUtils.isInteger(data)){
                    return Integer.parseInt(data);
                }
                return null;
            }
        });

        assertEquals(numbers.size(),3);

        List<String> filteredNumbers = CollectionUtils.filter(counter, new Filter<String>() {
            @Override
            public boolean accept(String value) {
                return PatternUtils.isInteger(value);
            }
        });

        assertEquals(filteredNumbers.size(),3);


        final List<String> list = new ArrayList<String>();
        CollectionUtils.visit(counter, new Visitor<String>() {
            @Override
            public void visit(String data) {
                list.add(data);
            }
        });

    }


    public void testToMap(){

        List<File> files = Arrays.asList(new File("/").listFiles());

        Map<String,File> map  = CollectionUtils.toMap(files,"path");

        for(String str : map.keySet()){
            assertTrue(!str.isEmpty());
        }

        CollectionUtils.toMap(files,"status");

    }


    public void testSet(){
        assertEquals(JSON.stringify(CollectionUtils.asSet("1","2","3")),JSON.stringify(Arrays.asList("1","2","3")));

        assertEquals(JSON.stringify(CollectionUtils.newSet(
                Arrays.asList("1", "2", "3"), new CollectionUtils.Convert<String, Object>() {
                    @Override
                    public Object convert(String data) {
                        return Integer.parseInt(data);
                    }
                }
        )),JSON.stringify(Arrays.asList(1,2,3)));

    }


    public void testOther(){
        assertTrue(Arrays.equals(
                CollectionUtils.toArray(Arrays.asList("1","2","3")),
                new String[]{"1","2","3"}
        ));


       assertEquals(
               JSON.stringify( CollectionUtils.valueMap(true,"1","2","3")),
               JSON.stringify(new DataObject().set("1",true).set("2",true).set("3",true))
       );

    }



    public void testGet(){

        Integer[] data = new Integer[]{1,2,3};

       assertEquals( (Integer)CollectionUtils.get(data, new Filter<Integer>() {
           @Override
           public boolean accept(Integer value) {
               return value==1;
           }
       }),(Integer)1);


        assertEquals( (Integer)CollectionUtils.get(data, new Filter<Integer>() {
            @Override
            public boolean accept(Integer value) {
                return value==4;
            }
        }),null);



    }

    public void testGetWithIteratable(){

        List<Integer> data = Arrays.asList(new Integer[]{1,2,3});

        assertEquals( (Integer)CollectionUtils.get(data, new Filter<Integer>() {
            @Override
            public boolean accept(Integer value) {
                return value==1;
            }
        }),(Integer)1);


        assertEquals( (Integer)CollectionUtils.get(data, new Filter<Integer>() {
            @Override
            public boolean accept(Integer value) {
                return value==4;
            }
        }),null);



    }

    public void testEnu(){



        Enumeration<String> enumeration = CollectionUtils.toEnumeration(
                Arrays.asList("1","2","3")
        );

        List<String> list = new ArrayList<String>();
        while (enumeration.hasMoreElements()){
            String str = enumeration.nextElement();
            list.add(str);

        }

        assertEquals(list.size(),3);
        assertEquals(list.get(0),"1");
        assertEquals(list.get(1),"2");
        assertEquals(list.get(2),"3");


    }



    public void testAs(){
        List<String> list = CollectionUtils.asList("1","2","3");
        assertEquals(list.size(),3);

        assertEquals(list.getClass(),ArrayList.class);
    }

    public void testListGet(){

        assertNull( CollectionUtils.getAt(null,0) );

        List<String> list = Arrays.asList("1","2","3");
        assertNull(CollectionUtils.getAt(list,list.size()));

        assertEquals(CollectionUtils.getAt(list,0),"1");

    }


}
