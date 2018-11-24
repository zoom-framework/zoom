package org.zoomdev.zoom.common.caster;
import junit.framework.TestCase;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.DataObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class CasterTest2 extends TestCase {

    public void testDate(){

        Date date = Caster.to("20001010",Date.class);
        assertEquals( new SimpleDateFormat("yyyyMMdd").format(date),"20001010");

        date = Caster.to("2000101010",Date.class);
        assertEquals( new SimpleDateFormat("yyyyMMddHH").format(date),"2000101010");

        date = Caster.to("2000-10-10 10:10:10",Date.class);
        assertEquals( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date),"2000-10-10 10:10:10");

        date = Caster.to("2000-10-10 10:10:10:123",Date.class);
        assertEquals( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(date),"2000-10-10 10:10:10:123");

    }


    public void testFile() throws IOException {

        ResScanner scanner = new ResScanner();
        scanner.scan();
        List<ResScanner.Res> resList = scanner.findFile("*");
        File file = resList.get(0).getFile();
        Caster.to(file,byte[].class);

    }


    public static class A{
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        private List<String> list;

        private String[] array;

        private Set<String> set;

        private Map<String,Object> map;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public String[] getArray() {
            return array;
        }

        public void setArray(String[] array) {
            this.array = array;
        }

        public Set<String> getSet() {
            return set;
        }

        public void setSet(Set<String> set) {
            this.set = set;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }


        public List<B> getListOfB() {
            return listOfB;
        }

        public void setListOfB(List<B> listOfB) {
            this.listOfB = listOfB;
        }

        private List<B> listOfB;

        public B[] getArrayOfB() {
            return arrayOfB;
        }

        public void setArrayOfB(B[] arrayOfB) {
            this.arrayOfB = arrayOfB;
        }

        private B[] arrayOfB;

        public A(){

        }
    }

    public static class B{

        public B(){

        }

        public B(int id,List<C> list){
            this.id = id;
            this.list = list;
        }

        private int id;
        private List<C> list;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<C> getList() {
            return list;
        }

        public void setList(List<C> list) {
            this.list = list;
        }
    }

    public static class C{

        public C(){

        }

        public C(String title) {
            this.title = title;
        }

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public void testBean() throws NoSuchMethodException {



        A  a = new A();
        a.setName("test");
        a.setList(Arrays.asList("1","2"));
        a.setArray(new String[]{"test","test2"});
        a.setMap(DataObject.as("id",1,"title","test"));

        a.setArrayOfB(new B[]{
            new B(
                    1,
                   Arrays.asList( new C(
                        "titleOfC"
                   ))
            )
        });

        Object data = JSON.stringify( a);

        A newA = Caster.to(data,A.class);

        System.out.println(newA);
        assertEquals(newA.name,a.name);
        assertEquals(newA.arrayOfB.length,1);
        assertEquals(newA.arrayOfB[0].id,1);
        assertEquals(newA.arrayOfB[0].list.get(0).title,"titleOfC");


        Field field = Classes.fetchField(B.class,"list");

        Caster.toType(new Map[]{

                DataObject.as(
                        "title","123"
                )

        },field.getGenericType());

    }
}
