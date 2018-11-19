package org.zoomdev.zoom.common.utils;

import org.junit.Test;
import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.exceptions.ZoomException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MapUtilsTest {


    @Test
    public void test(){

        Map<String,Object> data = new HashMap<String, Object>();
        data.put("id","1");
        data.put("label","1");

        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
        list.add(data);

        Map<String, Map<?, ?>> map  = MapUtils.toMap(list,"id");



    }


    @Test
    public void testAs(){
        Map<String,Object> data = MapUtils.asMap(
                "id",1,
                "name","张三",
                "title","test");

        assertEquals(data,DataObject.as(
                "id",1,
                "name","张三",
                "title","test"
        ));


    }

    @Test(expected = ZoomException.class)
    public void testAsError(){
        MapUtils.asMap(
                "id"
        );
    }


    // 实际上是提取key和value
    @Test
    public void toKeyAndLabel(){

        List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();

        Map<String,Object> product1 = DataObject.as("id",1,"title","测试1");
        Map<String,Object> product2 = DataObject.as("id",2,"title","测试2");
        Map<String,Object> product3 = DataObject.as("id",3,"title","测试3");

        list.add(product1);
        list.add(product2);
        list.add(product3);

        Map<String,String> map = MapUtils.toKeyAndLabel(list,"id","title");

        assertEquals(map,MapUtils.asMap("1","测试1",
                "2","测试2","3","测试3"));

    }

    public static class TestClass{
        private String id;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public TestClass(){

        }

        public TestClass(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    @Test
    public void testConvert(){

        Map<String,Map<String,Object>> map = new HashMap<String, Map<String, Object>>();

        map.put("1",DataObject.as("id",1,"title","测试1"));
        map.put("2",DataObject.as("id",2,"title","测试2"));
        map.put("3",DataObject.as("id",3,"title","测试3"));



        Map<String,TestClass> result = MapUtils.convert(map, new Converter<Map<String,Object>, TestClass>() {
            @Override
            public TestClass convert(Map<String, Object> data) {
                return Caster.to(data,TestClass.class);
            }
        });


        TestClass testClass = result.get("1");
        assertEquals(testClass.id,"1");
        assertEquals(testClass.title,"测试1");
    }

}
