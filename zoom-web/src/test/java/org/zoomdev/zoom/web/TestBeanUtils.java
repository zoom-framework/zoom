package org.zoomdev.zoom.web;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class TestBeanUtils {

    @Test
    public void testBean2Map() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        long time = System.currentTimeMillis();

        ExamInfo examInfo = new ExamInfo();




    }

    @Test
    public void testBean2() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        long time = System.currentTimeMillis();

        ExamInfo examInfo = new ExamInfo();

        for (int i = 0; i < 1; ++i) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("idCard", examInfo.idCard);
            data.put("areaCode", examInfo.areaCode);
            data.put("birth", examInfo.birth);
            data.put("cardId", examInfo.cardId);
            data.put("cityCode", examInfo.cityCode);
            data.put("custNo", examInfo.custNo);
            data.put("idCardType", examInfo.idCardType);
            data.put("img1", examInfo.img1);
            data.put("img2", examInfo.img2);
            data.put("local", examInfo.local);
            data.put("name", examInfo.name);
            data.put("navCode", examInfo.navCode);
            data.put("phone", examInfo.phone);
            data.put("postCode", examInfo.postCode);
            data.put("savType", examInfo.savType);
            data.put("schoolCode", examInfo.schoolCode);
            data.put("schoolName", examInfo.schoolName);
            data.put("sex", examInfo.sex);
            data.put("status", examInfo.status);
            data.put("type", examInfo.type);

        }

        System.out.println("testBean2:");
        System.out.println(System.currentTimeMillis() - time);


    }

}
