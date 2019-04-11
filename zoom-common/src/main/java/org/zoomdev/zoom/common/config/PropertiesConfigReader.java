package org.zoomdev.zoom.common.config;

import org.apache.commons.lang3.StringUtils;
import org.zoomdev.zoom.common.io.Io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//
///**
// * 
// * 支持数组、map
// * 数组写法：
// * 
// * z[0]=1
// * z[0]=2						结果z=[2]						[]将z看成数组，并设置z[0]=1 z[1]=1
// * 
// * x[1]=3
// * x[2]=4						结果x=[null,3,4]
// * 
// * b[0].name=value0				
// * b[0].id=1
// * 
// * b[1].name=value1			    
// * b[1].id=2						结果: b=[{name:value0,id:1},{name:value1,id:2}]
// * 								    将b看成数组，并设置下标0上的name属性为value0
// * 								注意这里不能写出:  b[].name=xx  b[].id=xx  
// * 
// * 
// * map写法
// * c{}.name=value0
// * c{}.id=1					     结果:c={ name:value0, id :1 }
// * 
// * d{}.a[0].name=value0			
// * d{}.a[0].id=1                  结果: d={a:[ {name:value0,id:1} ]}
// * 
// * 这样写是为了可以有这样的key
// * 
// * a.b.c{}.a=1					结果:  a.b.c = {a:1,b:1}
// * a.b.c{}.b=1
// *
// * @author jzoom
// *
// */
class PropertiesConfigReader implements ConfigLoader {


    static Pattern ARRAY = Pattern.compile("([a-zA-Z\\.]+)\\[([0-9]+)\\]([a-zA-Z0-9\\.\\[\\]\\{\\}]*)");


    static Pattern MAP = Pattern.compile("([a-zA-Z\\.]+)\\{\\}([a-zA-Z0-9\\.\\[\\]\\{\\}]*)");


    @Override
    public Map<String, Object> load(InputStream in) throws IOException {
        try {
            Properties p = new Properties();
            p.load(new InputStreamReader(in, "utf-8"));
            Map<String, Object> map = new HashMap<String, Object>();
            Enumeration<Object> keys = p.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = p.getProperty(key);
                if (key.contains("[") || key.contains("{")) {
                    parseKey(key, value, map);
                } else {
                    map.put(key, value);
                }
            }
            return map;
        } finally {
            Io.close(in);
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    static Object parseKey(String key, Object value, Map current) {

        Matcher matcher = ARRAY.matcher(key);
        if (matcher.matches()) {
            List list;
            String name = matcher.group(1);
            if (name.startsWith(".")) {
                name = name.substring(1);
            }
            Object maybeList = current.get(name);
            if (maybeList == null) {
                list = new ArrayList();
                current.put(name, list);
            } else {
                if (maybeList instanceof List) {
                    list = (List) maybeList;
                } else {
                    throw new RuntimeException(
                            String.format("同一个名称不能为多种类型 名称:%s 期待类型:%s 现在的类型:%s", name, List.class, maybeList.getClass()));
                }
            }
            String next = matcher.group(3);
            if (next.isEmpty()) {
                list.add(value);
                return list;
            }
            String maybeIndex = matcher.group(2);
            //这个时候一定是有值的，
            if (StringUtils.isEmpty(maybeIndex)) {
                throw new RuntimeException("对于下标[]来说，如果需要支持[].prop的属性，必须为[index].prop");
            }

            int takeIndex = Integer.parseInt(maybeIndex);
            if (takeIndex >= list.size()) {
                for (int i = 0, c = takeIndex - list.size() + 1; i < c; ++i) {
                    list.add(null);
                }
            }

            Map takeCurrent = (Map) list.get(takeIndex);
            if (takeCurrent == null) {
                takeCurrent = new HashMap();
                list.set(takeIndex, takeCurrent);
            }

            parseKey(next, value, takeCurrent);

            return list;
        } else {
            matcher = MAP.matcher(key);
            if (matcher.matches()) {

                String name = matcher.group(1);
                if (name.startsWith(".")) {
                    name = name.substring(1);
                }
                String next = matcher.group(2);

                if (StringUtils.isEmpty(next)) {
                    throw new RuntimeException("name{}的后面必须有key值，如:  bean{}.name=xxx");
                }

                Map map;
                Object maybeMap = current.get(name);
                if (maybeMap == null) {
                    map = new HashMap();
                    current.put(name, map);
                    maybeMap = map;
                } else {
                    if (maybeMap instanceof Map) {
                        map = (Map) maybeMap;
                    } else {
                        throw new RuntimeException(
                                String.format("同一个名称不能为多种类型 名称:%s 期待类型:%s 现在的类型:%s", name, Map.class, maybeMap.getClass()));
                    }
                }
                parseKey(matcher.group(2), value, map);

                return current;
            } else {
                String name = key;
                if (name.startsWith(".")) {
                    name = name.substring(1);
                }
                Object orgValue = current.get(name);
                if (orgValue != null) {
                    if (orgValue instanceof Map) {
                        throw new RuntimeException("覆盖原值,出现这个错误的原因一般是，之前设置了一个Map，而后又尝试设置一个普通值");
                    } else if (orgValue instanceof List) {
                        throw new RuntimeException("覆盖原值,出现这个错误的原因一般是，之前设置了一个List，而后又尝试设置一个普通值");
                    }
                }
                current.put(name, value);
                return current;
            }

        }


    }


}
