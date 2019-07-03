package org.zoomdev.zoom.common.caster;

import junit.framework.TestCase;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class JSONTest extends TestCase {


    public void test() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List list = new ArrayList();
        Map map1 = new HashMap();
        map1.put("id", "111");
        map1.put("users", Arrays.asList(1, 2, 3, 4, 5, 5));
        list.add(map1);
        Map map2 = new HashMap();
        map2.put("name", "zqw");
        list.add(map2);
        //普通输出
        System.out.println(mapper.writeValueAsString(list));
        //格式化/美化/优雅的输出
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));


        String str = "[ {\n" +
                "  \"id\" : \"111\",\n" +
                "  \"users\" : [ 1, 2, 3, 4, 5, 5 ]\n" +
                "}, {\n" +
                "  \"name\" : \"zqw\"\n" +
                "} ]";
        Object obj = mapper.readValue(str, Object.class);
        System.out.println(mapper.writeValueAsString(obj));
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));


    }

}
