package org.zoomdev.zoom.common.config;

import org.junit.Before;
import org.junit.Test;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.res.ResScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class PropertiesConfigReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testArray() {
        assertFalse(PropertiesConfigReader.ARRAY.matcher("a[]").matches());
        assertFalse(PropertiesConfigReader.ARRAY.matcher("a[].name{}").matches());
        assertTrue(PropertiesConfigReader.ARRAY.matcher("a[0]").matches());
        assertTrue(PropertiesConfigReader.ARRAY.matcher("abcd[0].name").matches());

        assertFalse(PropertiesConfigReader.ARRAY.matcher("abcd{}.name").matches());
        assertFalse(PropertiesConfigReader.ARRAY.matcher("abcd.name").matches());

    }

    @Test
    public void testMap() {
        assertFalse(PropertiesConfigReader.MAP.matcher("a[]").matches());
        assertFalse(PropertiesConfigReader.MAP.matcher("a[].name{}").matches());
        assertFalse(PropertiesConfigReader.MAP.matcher("a[0]").matches());
        assertFalse(PropertiesConfigReader.MAP.matcher("abcd[0].name").matches());

        assertTrue(PropertiesConfigReader.MAP.matcher("abcd{}.name").matches());
        assertTrue(PropertiesConfigReader.MAP.matcher("abcd{}").matches());

    }

    @Test
    public void testParseArray0() {
        Map<String, Object> current = new HashMap<String, Object>();
        PropertiesConfigReader.parseKey("a[0]", "1", current);
        PropertiesConfigReader.parseKey("a[1]", "2", current);

        assertTrue(JSON.stringify(current).equals("{\"a\":[\"1\",\"2\"]}"));
    }

    @Test
    public void testParseArray1() {
        Map<String, Object> current = new HashMap<String, Object>();
        PropertiesConfigReader.parseKey("a[0]", "1", current);
        PropertiesConfigReader.parseKey("a[1]", "2", current);

        assertTrue(JSON.stringify(current).equals("{\"a\":[\"1\",\"2\"]}"));
    }


    @Test
    public void testParseArray3() {
        Map<String, Object> current = new LinkedHashMap<String, Object>();
        PropertiesConfigReader.parseKey("a[0].name", "Sam", current);
        PropertiesConfigReader.parseKey("a[0].id", "1", current);
        PropertiesConfigReader.parseKey("a[1].name", "Suan", current);
        PropertiesConfigReader.parseKey("a[1].id", "2", current);

        assertEquals(current, JSON.parse("{\"a\":[{\"name\":\"Sam\",\"id\":\"1\"},{\"name\":\"Suan\",\"id\":\"2\"}]}", Map.class));
    }


    @Test(expected = RuntimeException.class)
    public void testParseMap0() {
        Map<String, Object> current = new HashMap<String, Object>();
        PropertiesConfigReader.parseKey("a{}", "1", current);

    }


    @Test(expected = RuntimeException.class)
    public void testParseMape() {
        Map<String, Object> current = new HashMap<String, Object>();
        PropertiesConfigReader.parseKey("a{}.name[0]", "1", current);
        PropertiesConfigReader.parseKey("a{}.name", "2", current);

    }


    @Test(expected = RuntimeException.class)
    public void testParseMape1() {
        Map<String, Object> current = new HashMap<String, Object>();
        PropertiesConfigReader.parseKey("a{}.name[0]", "1", current);
        PropertiesConfigReader.parseKey("a{}.name", "2", current);
    }

    @Test()
    public void testParseMap1() {
        Map<String, Object> current = new HashMap<String, Object>();
        PropertiesConfigReader.parseKey("a{}.name", "Sam", current);
        PropertiesConfigReader.parseKey("a{}.id", "1", current);
        PropertiesConfigReader.parseKey("a{}.name", "Susan", current);
        PropertiesConfigReader.parseKey("a{}.id", "2", current);

        assertEquals((current), JSON.parse("{\"a\":{\"name\":\"Susan\",\"id\":\"2\"}}", Map.class));

    }


    @Test()
    public void testParseMap2() {
        Map<String, Object> current = new HashMap<String, Object>();
        PropertiesConfigReader.parseKey("a{}.name", "Sam", current);
        PropertiesConfigReader.parseKey("a{}.id", "1", current);
        PropertiesConfigReader.parseKey("b{}.name", "Susan", current);
        PropertiesConfigReader.parseKey("b{}.id", "2", current);
        assertEquals(current, JSON.parse("{\"a\":{\"name\":\"Sam\",\"id\":\"1\"},\"b\":{\"name\":\"Susan\",\"id\":\"2\"}}", Map.class));

    }

    @Test()
    public void testParseArrayMap0() throws IOException {
        Map<String, Object> current = new LinkedHashMap<String, Object>();
        PropertiesConfigReader.parseKey("a[0].field{}.f1", "v1", current);
        PropertiesConfigReader.parseKey("a[0].field{}.f2", "v2", current);
        PropertiesConfigReader.parseKey("a[1].field{}.f3", "v3", current);
        PropertiesConfigReader.parseKey("a[1].field{}.f4", "v4", current);

        ResScanner scanner = new ResScanner();
        scanner.scan(getClass().getClassLoader());
        File file = scanner.getFile("result0.json").getFile();
        assertEquals(current, JSON.parse(new FileInputStream(file), Map.class));

    }

    @Test()
    public void testParseMapArray0() throws IOException {
        Map<String, Object> current = new LinkedHashMap<String, Object>();
        PropertiesConfigReader.parseKey("a{}.field[0]", "v1", current);
        PropertiesConfigReader.parseKey("a{}.field[1]", "v2", current);
        PropertiesConfigReader.parseKey("b{}.field[0].f3", "v3", current);
        PropertiesConfigReader.parseKey("b{}.field[0].f4", "v4", current);

        ResScanner scanner = new ResScanner();
        scanner.scan(getClass().getClassLoader());
        File file = scanner.getFile("result1.json").getFile();
        assertEquals(current, JSON.parse(new FileInputStream(file), Map.class));

    }

    @SuppressWarnings("unchecked")
    @Test()
    public void testParseArrayArray0() throws IOException {
        Map<String, Object> current = new LinkedHashMap<String, Object>();
        PropertiesConfigReader.parseKey("bean[0].args[0]", "v1", current);
        PropertiesConfigReader.parseKey("bean[0].args[1]", "v2", current);

        Map<String, Object> map = new HashMap<String, Object>();
        List<String> args = new ArrayList<String>();
        args.add("v1");
        args.add("v2");
        Map<String, Object> bean0 = new HashMap<String, Object>();
        bean0.put("args", args);

        map.put("bean", Arrays.asList(bean0));

        assertEquals(current, map);

    }

    @Test()
    public void testReadConfigFile() throws IOException {
        ResScanner scanner = new ResScanner();
        scanner.scan(getClass().getClassLoader());
        ResScanner.Res res = scanner.getFile("application.properties");
        List<ResScanner.Res> cList = scanner.findFile("*");
        for (ResScanner.Res classRes : cList) {
            System.out.println(classRes);
        }

        File file = res.getFile();
        PropertiesConfigReader configReader = new PropertiesConfigReader();
        ConfigReader reader = new ConfigReader();
        Map<String, Object> data = configReader.load(new FileInputStream(file));


        System.out.print(data);

        Set<String> keys = reader.keys("*");

        assertNotNull(keys);


    }
}
