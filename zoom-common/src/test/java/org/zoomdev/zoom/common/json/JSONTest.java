package org.zoomdev.zoom.common.json;

import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.zoomdev.zoom.common.utils.MapUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JSONTest {

    private static class A{


        public A(){

        }
        public A(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof A){

                A other = (A)obj;
                return ObjectUtils.equals(name,other.name);

            }
            return super.equals(obj);
        }
    }

    public static class ClassParse{
        public ClassParse(){

        }
        public Map<String, A> getTarget() {
            return target;
        }

        public void setTarget(Map<String, A> target) {
            this.target = target;
        }

        public ClassParse(Map<String, A> target) {
            this.target = target;
        }

        private Map<String,A> target;


        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ClassParse){

                ClassParse other = (ClassParse)obj;
                return ObjectUtils.equals(target,other.target);

            }
            return super.equals(obj);
        }
    }

    private static class TestType extends TypeReference<ClassParse> {


        public TestType() {
        }
    }

    @Test
    public void test() {

        assertEquals(JSON.stringify(new HashMap()),"{}");
        assertEquals(JSON.parse("{}", Map.class),new HashMap());

        Map<String,A> target = new HashMap<String, A>();
        target.put("name",new A("test"));
        ClassParse parse = new ClassParse(
                target
        );

        assertEquals(JSON.parse("{\"target\":{\"name\":{\"name\":\"test\"}}}",new TestType() ),parse);

    }
}
