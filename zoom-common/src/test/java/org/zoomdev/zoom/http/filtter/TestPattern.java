package org.zoomdev.zoom.http.filtter;

import junit.framework.TestCase;
import org.zoomdev.zoom.http.annotations.IocBean;
import org.zoomdev.zoom.http.annotations.Module;
import org.zoomdev.zoom.http.filter.ClassAndMethodFilter;
import org.zoomdev.zoom.http.filter.impl.ClassAnnotationFilter;
import org.zoomdev.zoom.http.filter.impl.PatternClassAndMethodFilter;

public class TestPattern extends TestCase {


    @Module
    public static class ModuleTest {


        @IocBean
        public Object getIoc() {
            return null;
        }

        @IocBean
        public Object test() {
            return null;
        }
    }

    public void test() throws NoSuchMethodException {

        ClassAnnotationFilter filter = new ClassAnnotationFilter(
                Module.class
        );


        assertTrue(filter.accept(ModuleTest.class));
        assertFalse(filter.accept(TestPattern.class));


        ClassAndMethodFilter andMethodFilter = new PatternClassAndMethodFilter(
                "*.Test*"
        );

        /// .TestPattern$ModuleTest
        System.out.println(ModuleTest.class.toString());
        assertTrue(andMethodFilter.accept(TestPattern.class));

        assertTrue(andMethodFilter.accept(ModuleTest.class));

        andMethodFilter = new PatternClassAndMethodFilter(
                "*Test*#(getIoc|test)"
        );


        assertTrue(andMethodFilter.accept(ModuleTest.class,
                ModuleTest.class.getMethod("getIoc")));


        assertTrue(andMethodFilter.accept(ModuleTest.class,
                ModuleTest.class.getMethod("test")));
    }
}
