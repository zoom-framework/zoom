package com.jzoom.zoom.common.filtter;

import com.jzoom.zoom.common.filter.AlwaysAcceptFilter;
import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.common.filter.pattern.*;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class PatternFilterFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Filter<String> filter = PatternFilterFactory.createFilter("*");
		assertEquals(filter.getClass(), AlwaysAcceptFilter.class);
		
		filter = PatternFilterFactory.createFilter("**");
		assertEquals(filter.getClass(), AlwaysAcceptFilter.class);
		
		filter = PatternFilterFactory.createFilter("****");
		assertEquals(filter.getClass(), AlwaysAcceptFilter.class);
		
		filter = PatternFilterFactory.createFilter("");
		assertEquals(filter.getClass(), AlwaysAcceptFilter.class);
		
		filter = PatternFilterFactory.createFilter(null);
		assertEquals(filter.getClass(), AlwaysAcceptFilter.class);
		
		//每个测试包含极限情况
		assertTrue(filter.accept("////"));
		assertTrue(filter.accept(null));
		
		filter = PatternFilterFactory.createFilter("**/controller/**");
		assertEquals(filter.getClass(), ContainsFilter.class);
		assertFalse(filter.accept(null));
		
		assertTrue(filter.accept("xxx/controller/bbb"));
		assertTrue(filter.accept("/controller/"));
		
		
		assertFalse(filter.accept("////"));
		assertFalse(filter.accept("controller"));
		assertFalse(filter.accept("controller/bbb"));
		
		filter = PatternFilterFactory.createFilter("controller");
		assertEquals(filter.getClass(), ExactFilter.class);
		assertFalse(filter.accept(null));
		
		assertTrue(filter.accept("controller"));
		assertFalse(filter.accept("////"));
		
		filter = PatternFilterFactory.createFilter("controller*");
		assertEquals(filter.getClass(), StartsWithFilter.class);
		assertFalse(filter.accept(null));
		
		assertTrue(filter.accept("controller"));
		assertFalse(filter.accept("////"));
		
		assertTrue(filter.accept("controller/xxx"));
		assertFalse(filter.accept("acontroller"));
		
		
		filter = PatternFilterFactory.createFilter("*controller");
		assertEquals(filter.getClass(), EndsWithFilter.class);
		assertFalse(filter.accept(null));
		assertTrue(filter.accept("xxcontroller"));
		assertFalse(filter.accept("controllerxx"));
		
		assertTrue(filter.accept("controller"));
		assertFalse(filter.accept("////"));
		
		
		//很多的*不连续
		filter = PatternFilterFactory.createFilter("*controller*.java");
		
		assertTrue(filter.accept("controller.java"));
		assertFalse(filter.accept("controller.java1"));
		
		assertFalse(filter.accept("1controller.java1"));
		assertTrue(filter.accept("1controller.java"));
	
		
		filter = PatternFilterFactory.createFilter("*controller*/*.java");
		
		assertFalse(filter.accept("controller.java"));
		assertFalse(filter.accept("controller.java1"));
		
		assertFalse(filter.accept("1controller.java1"));
		assertFalse(filter.accept("1controller.java"));
		
		assertTrue(filter.accept("1controller_jkfdf/1.java"));
	}
	
	@Test
	public void testCombin() {
		Filter<String> filter;
		filter = PatternFilterFactory.createFilter("!*");
		assertFalse(filter.accept("jjjj"));
		
		filter = PatternFilterFactory.createFilter("!*aaa");
		assertFalse(filter.accept("aaa"));
		
		filter = PatternFilterFactory.createFilter("!*aaa|!*bbb");
		assertTrue(filter.accept("aaa"));
		assertTrue(filter.accept("bbb"));
		assertTrue(filter.accept("abb"));
		
		filter = PatternFilterFactory.createFilter("!*aaa&!*bbb");
		assertFalse(filter.accept("aaa"));
		assertFalse(filter.accept("bbb"));
		assertTrue(filter.accept("abb"));
		
		
		filter = PatternFilterFactory.createFilter("*aaa&*bbb");
		assertFalse(filter.accept("aaa"));
		assertFalse(filter.accept("bbb"));
		assertFalse(filter.accept("abb"));
		
		filter = PatternFilterFactory.createFilter("aaa*&*bbb");
		assertFalse(filter.accept("aaa"));
		assertFalse(filter.accept("bbb"));
		assertTrue(filter.accept("aaabbb"));
		
		
		filter = PatternFilterFactory.createFilter("aaa*|*bbb");
		assertTrue(filter.accept("aaa"));
		assertTrue(filter.accept("bbb"));
		assertTrue(filter.accept("aaa1111bbb"));
		assertFalse(filter.accept("aa11a111bb222b"));
		
		
		filter = PatternFilterFactory.createFilter("aaa*|*bbb&&ccc*");
		assertTrue(filter.accept("aaa"));
		assertFalse(filter.accept("bbb"));
		assertTrue(filter.accept("aaa1111bbb"));
		assertFalse(filter.accept("aa11a111bb222b"));
		assertFalse(filter.accept("bbb1123123ccc"));
		assertTrue(filter.accept("cccaaabbb"));
		
		
	}
	
	@Test
	public void testCombin2() {
		Filter<String> filter;
		filter = PatternFilterFactory.createFilter("a|b|c");
		assertTrue(filter.accept("a"));
		assertTrue(filter.accept("b"));
		assertTrue(filter.accept("c"));
		assertFalse(filter.accept("aa"));
		
		filter = PatternFilterFactory.createFilter("a*&(*b|*c)");
		assertTrue(filter.accept("ab"));
		assertTrue(filter.accept("ac"));
		assertTrue(filter.accept("a1b"));
		assertTrue(filter.accept("a2c"));
		assertFalse(filter.accept("aaa"));
		assertFalse(filter.accept("bbb"));
		
		
		
		
		filter = PatternFilterFactory.createFilter("a*&(*b|*c)&(*d*|*e*|*f*)");
		assertTrue(filter.accept("aeb"));
		
		
		filter = PatternFilterFactory.createFilter("a*&(*e*&(*b|*c))");
		assertTrue(filter.accept("aeb"));
		
		
		filter = PatternFilterFactory.createFilter("a|b|c*&*d");
		assertTrue(filter.accept("cd"));
		assertTrue(filter.accept("a"));
	}
	
	@Test
	public void test2() {
		Filter<String> filter;
		filter = PatternFilterFactory.createFilter("*api*controllers*");
		assertTrue(filter.accept("com.czc.ecard.api.shimin.controllers.ShiminController"));
	}
	
	@Test
	public void test3() {
		Filter<String> filter;
		filter = PatternFilterFactory.createFilter("!.*&!*.log&!*.db&!*.git*&!*.html*");
		assertTrue(!filter.accept("1.html"));
		assertTrue(!filter.accept(".html"));
		assertTrue(!filter.accept("1.log"));
		assertTrue(!filter.accept("2.db"));
		assertTrue(!filter.accept(".git"));
		assertTrue(filter.accept("2.class"));
	}

    @Test
    public void test4() {
        String patterStr = "id|title|name|key|value|config|set";
        Filter<String> filter;
        filter = PatternFilterFactory.createFilter(patterStr);

        String[] str = new String[]{
                "id", "title", "name", "key", "value", "config", "set",
                "haha", "jlkfjdsf", "1u3", "jjjkfdjf", "jklfdsf", "12jno", ";f]fdfjaf"
        };

        Pattern pattern = Pattern.compile(patterStr);


        long time = System.currentTimeMillis();
        int count = 1;

        for (int i = 0; i < count; ++i) {
            for (String s : str) {
                filter.accept(s);
            }
        }
        System.out.println(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        for (int i = 0; i < count; ++i) {
            for (String s : str) {
                pattern.matcher(s).matches();
            }
        }
        System.out.println(System.currentTimeMillis() - time);
    }

}
