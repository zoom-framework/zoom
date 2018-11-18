package org.zoomdev.zoom.common.pattern;

import junit.framework.TestCase;
import org.zoomdev.zoom.common.utils.PatternUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPatternUtils extends TestCase {

    public void testPatternVisitor() {

        final StringBuilder sb = new StringBuilder();
        final List<String> list = new ArrayList<String>();

        PatternUtils.visit("tpId=testId,test=kfjdsf,*jkfd",
                Pattern.compile("([a-zA-Z_]+)"),
                new PatternUtils.PatternVisitor() {
                    @Override
                    public void onGetPattern(Matcher matcher) {
                        sb.append(matcher.group(1));
                        list.add(matcher.group(1));
                    }

                    @Override
                    public void onGetRest(String rest) {
                        sb.append(rest);

                    }
                });

        assertEquals(sb.toString(),"tpId=testId,test=kfjdsf,*jkfd");

        assertEquals(list.size(),5);
    }


    public void testPattern1(){

        assertTrue(PatternUtils.isInteger("123456"));
        assertFalse(PatternUtils.isInteger("123456.0"));
        assertFalse(PatternUtils.isInteger("aa"));

        assertTrue(PatternUtils.isNumber("12"));
        assertTrue(PatternUtils.isNumber("0.12"));
        assertTrue(PatternUtils.isNumber("123456.0"));
        assertFalse(PatternUtils.isNumber("aa"));
        assertFalse(PatternUtils.isNumber(".12"));
        assertFalse(PatternUtils.isNumber("aa.bb"));
        assertFalse(PatternUtils.isNumber("12."));





    }
}
