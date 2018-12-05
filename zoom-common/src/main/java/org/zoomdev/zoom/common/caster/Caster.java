package org.zoomdev.zoom.common.caster;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.BeanUtils;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.DataObject;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This tool box can cast a value into another class. Caster.to( value ,
 * Integer.class)
 * <p>
 * <p>
 * 目前支持基本数据
 *
 * @author Administrator
 */
public class Caster {

    //日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    //时间格式
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //毫秒
    public static final String DATE_TIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 默认模式
     */
    private static ObjectMapper mapper = JSON.getMapper();

    /**
     * 包装一个首次非空的值之后缓存的caster
     * 一开始不知道源类型，但是知道目标类型，源类型在之后的程序中不会改变了。
     *
     * @param dest
     * @return
     */
    public static ValueCaster wrapFirstVisit(Type dest) {
        return new WrapExceptionCaster(new FirstVisitValueCaster(dest));
    }


    static class WrapExceptionCaster implements ValueCaster {

        public WrapExceptionCaster(ValueCaster valueCaster) {
            this.valueCaster = valueCaster;
        }

        protected ValueCaster valueCaster;

        @Override
        public Object to(Object src) {
            try {
                return valueCaster.to(src);
            } catch (Throwable t) {
                if (t instanceof CasterException) {
                    throw (CasterException) t;
                }
                throw new CasterException(t);
            }

        }
    }

    static class FirstVisitValueCaster implements ValueCaster {

        Type dest;

        ValueCaster caster;

        public FirstVisitValueCaster(Type dest) {
            assert (dest != null);
            this.dest = dest;
        }

        @Override
        public Object to(Object src) {
            ValueCaster caster = this.caster;
            if (caster == null) {
                if (src == null) {
                    if (dest instanceof Class)
                        return Caster.to(null, (Class) dest);
                    return null;
                }
                caster = wrapType(src.getClass(), dest);
                this.caster = caster;
            }

            return caster.to(src);
        }
    }

    /**
     * 只知道要转化成toType，不知道源类型，源类型可能会改变
     *
     * @param toType
     * @return
     */
    public static ValueCaster wrap(Class<?> toType) {
        if (toType == null) {
            throw new CasterException("必须提供一个目标类型");
        }

        ValueCaster caster = anyCasters.get(toType);
        if (caster == null) {
            caster = new AnyCaster(toType);
            anyCasters.put(toType, caster);
        }
        return caster;
    }

    private static Map<Class<?>, ValueCaster> anyCasters = new ConcurrentHashMap<Class<?>, ValueCaster>();

    static class AnyCaster implements ValueCaster {

        AnyCaster(Class<?> toType) {
            this.toType = toType;
        }

        Class<?> toType;


        @Override
        public Object to(Object src) {
            return Caster.to(src, toType);
        }
    }


    static class EmptyValueCaster implements ValueCaster {

        @Override
        public Object to(Object src) {
            return src;
        }
    }

    static EmptyValueCaster EMPTY = new EmptyValueCaster();

    /**
     * Get a wrapped ValueCaster that check null value and return default value when value is null.
     *
     * @param srcType
     * @param toType
     * @return
     */
    public static ValueCaster wrap(Class<?> srcType, Class<?> toType) {
        if (toType == null) {
            throw new CasterException("toType must not be null");
        }
        if (srcType == null) {
            return wrapFirstVisit(toType);
        }

        if (srcType == toType || srcType.isAssignableFrom(toType)) {
            return EMPTY;
        }

        if (toType.isPrimitive()) {
            //转化的类型指定类的包装类
            return new WrapPriValueCaster(get(srcType, getWrapClass(toType)), toType);
        }

        if (srcType.isPrimitive()) {
            return wrap(getWrapClass(srcType), toType);
        }


        return new CheckNullCaster(get(srcType, toType));
    }

    public static class CasterException extends ZoomException {

        public CasterException(String message) {
            super(message);
        }

        public CasterException(String message, Throwable e) {
            super(message, e);
        }

        public CasterException(Throwable e) {
            super(e);
        }

        /**
         *
         */
        private static final long serialVersionUID = -4063979897278480246L;

    }


    public static interface CasterProvider {
        ValueCaster getCaster(Class<?> srcType, Class<?> toType);
    }

    public static CasterProvider[] providers;

    public static synchronized void registerCastProvider(CasterProvider provider) {
        if (providers == null) {
            providers = new CasterProvider[]{provider};
        } else {
            CasterProvider[] oldCasterProviders = providers;
            providers = new CasterProvider[providers.length + 1];

            for (int i = 0, c = oldCasterProviders.length; i < c; ++i) {
                providers[i] = oldCasterProviders[i];
            }
            providers[oldCasterProviders.length] = provider;
        }
    }

    private static Map<String, ValueCaster> map;
    static List<ParameterizedTypeCasterfactory> typeCanbeConvertToParameterizedType = new ArrayList<ParameterizedTypeCasterfactory>();

    static {
        map = new ConcurrentHashMap<String, ValueCaster>();

        register(Object.class, String.class, new Object2String());


        Caster.register(Double.class, boolean.class, new Double2Boolean());
        Caster.register(Float.class, boolean.class, new Float2Boolean());

        //数字到其他
        Caster.register(Number.class, double.class, new Number2Double());
        Caster.register(Number.class, int.class, new Number2Integer());
        Caster.register(Number.class, boolean.class, new Number2Boolean());
        Caster.register(Number.class, float.class, new Number2Float());
        Caster.register(Number.class, short.class, new Number2Short());
        Caster.register(Number.class, char.class, new Number2Char());
        Caster.register(Number.class, byte.class, new Number2Byte());
        Caster.register(Number.class, long.class, new Number2Long());

        //String 到 基本类型
        Caster.register(String.class, char.class, new String2Char());
        Caster.register(String.class, int.class, new String2Integer());
        Caster.register(String.class, boolean.class, new String2Boolean());
        Caster.register(String.class, long.class, new String2Long());
        Caster.register(String.class, short.class, new String2Short());
        Caster.register(String.class, byte.class, new String2Byte());
        Caster.register(String.class, double.class, new String2Double());
        Caster.register(String.class, float.class, new String2Float());

        //boolean
        Caster.register(boolean.class,int.class,new Boolean2Int());
        Caster.register(boolean.class,long.class,new Boolean2Long());
        Caster.register(boolean.class,byte.class,new Boolean2Byte());
        Caster.register(boolean.class,float.class,new Boolean2Float());
        Caster.register(boolean.class,double.class,new Boolean2Double());
        Caster.register(boolean.class,char.class,new Boolean2Char());
        //database


        //date
        Caster.register(java.util.Date.class, java.sql.Date.class, new Date2SqlDate());
        //除非你想用toString,否则下面这条不必要s
        //Caster.register(java.sql.Date.class, java.util.Date.class, new SqlDate2Date());


        //CLASS
        Caster.register(String.class, Class.class, new String2Class());

        //json
        Caster.register(String.class, Map.class, new String2Map());
        Caster.register(String.class, List.class, new String2List());

        //这里可以看情况是否要覆盖默认情况
        Caster.register(InputStream.class, Map.class, new InputStream2Map());
        Caster.register(InputStream.class, List.class, new InputStream2List());
        Caster.register(Reader.class, Map.class, new Reader2Map());
        Caster.register(Reader.class, List.class, new Reader2List());
        Caster.register(List.class, String.class, new JsonObject2String());
        Caster.register(Set.class, String.class, new JsonObject2String());
        Caster.register(Collection.class, String.class, new JsonObject2String());
        Caster.register(Map.class, String.class, new JsonObject2String());


        //BigDecimal
        Caster.register(BigDecimal.class, boolean.class, new BigDecimal2Boolean());
        Caster.register(Integer.class, BigDecimal.class, new Integer2BigDecimal());
        Caster.register(Double.class, BigDecimal.class, new Double2BigDecimal());
        Caster.register(Long.class, BigDecimal.class, new Long2BigDecimal());
        Caster.register(Short.class, BigDecimal.class, new Short2BigDecimal());
        Caster.register(Boolean.class, BigDecimal.class, new Boolean2BigDecimal());
        Caster.register(String.class, BigDecimal.class, new String2BigDecimal());

        //time
        Caster.register(java.util.Date.class, java.sql.Time.class, new Date2SqlTime());
        //timestamp
        Caster.register(java.util.Date.class, java.sql.Timestamp.class, new Date2Timestamp());
        Caster.register(java.sql.Timestamp.class, String.class, new Timestamp2String());
        //规则是 转成Integer表示的是秒， 而long和double表示的是毫秒，其他数字类型不接受
        Caster.register(java.util.Date.class, int.class, new Date2Integer());
        Caster.register(java.util.Date.class, long.class, new Date2Long());


        //clob
        Caster.register(Clob.class, String.class, new Clob2String());
        Caster.register(NClob.class, String.class, new Clob2String());
        //blob
        Caster.register(Blob.class, String.class, new Blob2String());
        Caster.register(Blob.class, byte[].class, new Blob2ByteArray());


        Caster.register(String.class, byte[].class, new String2ByteArray());

        Caster.registerParameterizedType(
                new MapperCasterFactory<Clob, List>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperClobCaster(javaType);
                    }
                },
                new MapperCasterFactory<Clob, Map>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperClobCaster(javaType);
                    }
                },
                new MapperCasterFactory<Clob, Set>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperClobCaster(javaType);
                    }
                },
                new MapperCasterFactory<Clob, Collection>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperClobCaster(javaType);
                    }
                }
        );

        Caster.registerParameterizedType(
                new MapperCasterFactory<CharSequence, List>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperStringCaster(javaType);
                    }
                },
                new MapperCasterFactory<CharSequence, Map>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperStringCaster(javaType);
                    }
                },
                new MapperCasterFactory<CharSequence, Set>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperStringCaster(javaType);
                    }
                },
                new MapperCasterFactory<CharSequence, Collection>() {
                    @Override
                    protected ValueCaster create(JavaType javaType) {
                        return new MapperStringCaster(javaType);
                    }
                }
        );

        Caster.registerCastProvider(new Map2BeanProvider());
        Caster.registerCastProvider(new String2BeanProvider());

        Caster.register(String.class, Date.class, new String2Date());

        Caster.register(File.class, byte[].class, new File2ByteArray());

        Caster.register(File.class, InputStream.class, new File2InputStream());

        Caster.register(Timestamp.class, Date.class, new Timestamp2Date());

        Caster.register(long.class,Timestamp.class,new Long2Timestamp());

        Caster.register(Map.class,DataObject.class,new Map2DataObject());

    }

    /**
     * If A can cast to B
     * And B can cast to C
     * <p>
     * You can register like this
     * <p>
     * Caster.register(  A.class,C.class,new CasterBridge( A.class,B.class,C.class )  )
     */
    static class CasterBridge implements ValueCaster {

        private ValueCaster caster1;
        private ValueCaster caster2;

        CasterBridge(Class<?> srcType, Class<?> bridgetType, Class<?> destType) {
            this.caster1 = Caster.get(srcType, bridgetType);
            this.caster2 = Caster.wrap(bridgetType, destType);
        }

        @Override
        public Object to(Object src) {
            //dest to bridget  bridget to src
            return caster1.to(caster2.to(src));
        }
    }

    static class String2BeanProvider implements Caster.CasterProvider {

        @Override
        public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
            if (CharSequence.class.isAssignableFrom(srcType) && !isSimple(toType)) {
                ValueCaster caster = Caster.get(Map.class, toType);
                return new String2Bean(caster);
            }
            return null;
        }


    }

    private static boolean isSimple(Class<?> toType) {
        if (Classes.isSimple(toType)) {
            //转化简单类型应该是不行的
            return true;
        }
        //java开头的一律略过
        if (toType.getName().startsWith("java")) return true;

        return false;

    }

    private static class String2Bean implements ValueCaster {

        private ValueCaster caster;

        public String2Bean(ValueCaster caster) {
            this.caster = caster;
        }

        @Override
        public Object to(Object src) {
            String str = (String) src;
            Map<String, Object> data = JSON.parse(str, Map.class);
            return caster.to(data);
        }
    }

    static class Map2BeanProvider implements Caster.CasterProvider {


        @Override
        public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
            if (Map.class.isAssignableFrom(srcType) && !isSimple(toType)) {
                return new Map2Bean(toType);
            }

            return null;

        }

    }

    private static class Map2Bean implements ValueCaster {
        private Class<?> toType;

        public Map2Bean(Class<?> toType) {
            this.toType = toType;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public Object to(Object src) {
            Map data = (Map) src;
            try {
                Object result = toType.newInstance();
                return BeanUtils.mergeMap(result, data);
            } catch (Exception e) {
                throw new Caster.CasterException(e);
            }

        }

    }

    private static void registerParameterizedType(ParameterizedTypeCasterfactory... factory) {
        Collections.addAll(typeCanbeConvertToParameterizedType, factory);
    }

    static class MapperStringCaster extends MapperCaster {


        MapperStringCaster(JavaType javaType) {
            super(javaType);
        }

        @Override
        public Object to(Object src) {
            String str = (String) src;
            try {
                return mapper.readValue((String) src, javaType);

            } catch (IOException e) {
                throw new CasterException(e);
            }
        }
    }

    /**
     * convert clob to JavaType
     */
    static class MapperClobCaster extends MapperCaster {

        private ValueCaster clobCaster;

        MapperClobCaster(JavaType javaType) {
            super(javaType);
        }

        @Override
        public Object to(Object src) {
            ValueCaster clobCaster = this.clobCaster;
            if (clobCaster == null) {
                clobCaster = Caster.get(Clob.class, String.class);
                this.clobCaster = clobCaster;
            }
            String str = (String) clobCaster.to(src);
            try {
                return mapper.readValue(str, javaType);
            } catch (IOException e) {
                throw new CasterException(e);
            }

        }
    }

    static abstract class MapperCaster implements ValueCaster {
        JavaType javaType;

        MapperCaster(JavaType javaType) {
            this.javaType = javaType;
        }


    }

    private static abstract class MapperCasterFactory<T, E> extends ParameterizedTypeCasterfactory<T, E> {

        @Override
        public ValueCaster create(ParameterizedType targetType) {
            JavaType javaType = TypeFactory.defaultInstance().constructType(targetType);
            return create(javaType);
        }

        protected abstract ValueCaster create(JavaType javaType);
    }

    private static class Clob2String implements ValueCaster {
        @Override
        public Object to(Object src) {
            Clob clob = (Clob) src;
            Reader reader = null;
            try {
                reader = clob.getCharacterStream();
                char[] buffer = new char[(int) clob.length()];
                reader.read(buffer);
                return new String(buffer);
            } catch (Exception e) {
                throw new CasterException(e);
            } finally {
                close(reader);
            }
        }

    }

    private static class String2ByteArray implements ValueCaster {

        @Override
        public Object to(Object src) {
            String str = (String) src;
            return Base64.decodeFast(str);
        }
    }

    private static class Blob2ByteArray implements ValueCaster {
        @Override
        public Object to(Object src) {
            return readBytes((Blob) src);
        }

    }

    private static byte[] readBytes(Blob blob) {
        InputStream is = null;
        try {
            is = blob.getBinaryStream();
            byte[] data = new byte[(int) blob.length()];        // byte[] data = new byte[is.available()];
            is.read(data);
            return data;
        } catch (IOException e) {
            throw new CasterException(e);
        } catch (SQLException e) {
            throw new CasterException(e);
        } finally {
            close(is);
        }
    }

    private static void close(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException e) {
        }
    }

    private static class Blob2String implements ValueCaster {
        @Override
        public Object to(Object src) {
            return Base64.encodeToString(readBytes((Blob) src), false);
        }

    }

    private static class Date2Long implements ValueCaster {

        @Override
        public Object to(Object src) {
            Date date = (Date) src;
            return date.getTime();
        }

    }

    private static class Date2Integer implements ValueCaster {

        @Override
        public Object to(Object src) {
            Date date = (Date) src;
            return (int) (date.getTime() / 1000);
        }

    }

    private static class Timestamp2String implements ValueCaster {
        @Override
        public Object to(Object src) {
            java.sql.Timestamp date = (java.sql.Timestamp) src;
            return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
        }
    }

    private static class Date2Timestamp implements ValueCaster {

        @Override
        public Object to(Object src) {
            java.util.Date date = (java.util.Date) src;
            return new java.sql.Timestamp(date.getTime());
        }
    }

    private static class Date2SqlTime implements ValueCaster {

        @Override
        public Object to(Object src) {
            java.util.Date date = (java.util.Date) src;
            return new java.sql.Time(date.getTime());
        }
    }

    private static class Boolean2BigDecimal implements ValueCaster {

        @Override
        public Object to(Object src) {
            Boolean number = (Boolean) src;
            return new BigDecimal(number ? 1 : 0);
        }
    }

    private static class Short2BigDecimal implements ValueCaster {

        @Override
        public Object to(Object src) {
            Short number = (Short) src;
            return new BigDecimal(number);
        }
    }

    private static class Long2BigDecimal implements ValueCaster {

        @Override
        public Object to(Object src) {
            Long number = (Long) src;
            return new BigDecimal(number);
        }
    }

    private static class String2BigDecimal implements ValueCaster {

        @Override
        public Object to(Object src) {
            String number = (String) src;
            return new BigDecimal(number);
        }
    }

    private static class Integer2BigDecimal implements ValueCaster {

        @Override
        public Object to(Object src) {
            Integer number = (Integer) src;
            return new BigDecimal(number);
        }

    }


    private static class Double2BigDecimal implements ValueCaster {

        @Override
        public Object to(Object src) {
            Double number = (Double) src;
            return new BigDecimal(number);
        }
    }

    private static class String2Map implements ValueCaster {

        @Override
        public Object to(Object src) {
            try {
                return mapper.readValue((String) src, Map.class);
            } catch (Exception e) {
                throw new CasterException(e);
            }
        }

    }

    private static class InputStream2Map implements ValueCaster {

        @Override
        public Object to(Object src) {
            InputStream is = (InputStream) src;
            try {
                return mapper.readValue(is, Map.class);
            } catch (Exception e) {
                throw new CasterException(e);
            } finally {
                close(is);
            }
        }

    }

    private static class InputStream2List implements ValueCaster {

        @Override
        public Object to(Object src) {
            InputStream is = (InputStream) src;
            try {
                return mapper.readValue(is, List.class);
            } catch (Exception e) {
                throw new CasterException(e);
            } finally {
                close(is);
            }
        }

    }

    private static class Reader2Map implements ValueCaster {

        @Override
        public Object to(Object src) {
            Reader is = (Reader) src;
            try {
                return mapper.readValue(is, Map.class);
            } catch (Exception e) {
                throw new CasterException(e);
            } finally {
                close(is);
            }
        }

    }

    private static class Reader2List implements ValueCaster {

        @Override
        public Object to(Object src) {
            Reader is = (Reader) src;
            try {
                return mapper.readValue(is, List.class);
            } catch (Exception e) {
                throw new CasterException(e);
            } finally {
                close(is);
            }
        }

    }


    private static class String2List implements ValueCaster {

        @Override
        public Object to(Object src) {
            try {
                return mapper.readValue((String) src, List.class);
            } catch (Exception e) {
                throw new CasterException(e);
            }
        }
    }

    private static class JsonObject2String implements ValueCaster {

        @Override
        public Object to(Object src) {
            try {
                return mapper.writeValueAsString(src);
            } catch (Exception e) {
                throw new CasterException(e);
            }
        }
    }


    private static class String2Class implements ValueCaster {

        @Override
        public Object to(Object src) {
            try {
                return Class.forName((String) src);
            } catch (ClassNotFoundException e) {
                throw new CasterException(e);
            }
        }

    }


    private static class Date2SqlDate implements ValueCaster {

        @Override
        public Object to(Object src) {
            java.util.Date date = (java.util.Date) src;
            return new java.sql.Date(date.getTime());
        }

    }


    private static class String2Byte implements ValueCaster {

        @Override
        public Object to(Object src) {
            return Byte.parseByte((String) src);
        }

    }

    private static class String2Char implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((String) src).charAt(0);
        }

    }

    private static class String2Short implements ValueCaster {

        @Override
        public Object to(Object src) {
            return Short.parseShort((String) src);
        }

    }


    private static class String2Float implements ValueCaster {

        @Override
        public Object to(Object src) {
            return Float.parseFloat((String) src);
        }

    }

    private static class String2Boolean implements ValueCaster {

        @Override
        public Object to(Object src) {
            return Boolean.parseBoolean((String) src);
        }

    }


    private static class String2Integer implements ValueCaster {

        @Override
        public Object to(Object src) {
            String str = ((String) src).trim();
            if (str.contains(".")) {
                return (int) (double) Double.parseDouble(str);
            }
            if (str.length() == 0) {
                return 0;
            }
            return Integer.parseInt(str);
        }

    }

    private static class String2Long implements ValueCaster {

        @Override
        public Object to(Object src) {
            String str = ((String) src).trim();
            if (str.contains(".")) {
                return (long) (double) Double.parseDouble(str);
            }
            if (str.length() == 0) {
                return 0;
            }
            return Long.parseLong(str);
        }

    }

    private static class String2Double implements ValueCaster {

        @Override
        public Object to(Object src) {
            return Double.parseDouble((String) src);
        }

    }

    private static class Number2Long implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Number) src).longValue();
        }
    }

    private static class Number2Byte implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Number) src).byteValue();
        }
    }

    private static class Number2Char implements ValueCaster {

        @Override
        public Object to(Object src) {
            return (char) ((Number) src).shortValue();
        }

    }

    private static class Number2Integer implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Number) src).intValue();
        }

    }

    private static class Number2Short implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Number) src).shortValue();
        }

    }


    private static class Number2Float implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Number) src).floatValue();
        }

    }

    private static class Double2Boolean implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Double) src).equals(0.0d) ? Boolean.FALSE : Boolean.TRUE;
        }
    }

    private static class Float2Boolean implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Float) src).equals(0.0f) ? Boolean.FALSE : Boolean.TRUE;
        }
    }
    //BigDecemil?


    private static class BigDecimal2Boolean implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((BigDecimal) src).doubleValue() == 0.0d ? Boolean.FALSE : Boolean.TRUE;
        }
    }

    private static class Number2Boolean implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Number) src).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
        }

    }

    private static class Number2Double implements ValueCaster {

        @Override
        public Object to(Object src) {
            return ((Number) src).doubleValue();
        }

    }


    /**
     * T cast some class to another class, you must register the tow classes and
     * a caster .
     *
     * @param src
     * @param to
     * @param caster
     */
    public static void register(Class<?> src, Class<?> to, ValueCaster caster) {
        if (to == null || src == null || caster == null) {
            throw new NullPointerException();
        }

        if (to.isPrimitive()) {
            map.put(getKey(src, getWrapClass(to)), caster);
        }

        if (src.isPrimitive()) {
            map.put(getKey(getWrapClass(src), to), caster);
        }

        if(to.isPrimitive() && src.isPrimitive()){
            map.put(getKey(getWrapClass(src), getWrapClass(to)), caster);
        }

        map.put(getKey(src, to), caster);
    }

    /**
     * Get convrt src and target key in map
     *
     * @param src
     * @param to
     * @return
     */
    private static String getKey(Class<?> src, Class<?> to) {
        return new StringBuilder().append(src.getName()).append("2").append(to.getName()).toString();
    }

    /**
     * Extra class to array. Including self, super class, interfaces
     *
     * @param src
     * @return
     */
    private static String[] extraClass(Class<?> src) {
        List<Class<?>> set = new ArrayList<Class<?>>();

        extraClass(set, src);

        String[] result = new String[set.size()];
        int i = 0;
        for (Class<?> type : set) {
            result[i++] = type.getName();
        }
        return result;
    }

    private static void extraClass(List<Class<?>> set, Class<?> src) {
        if (src == null) {
            return;
        }
        if (src.isInterface()) {
            extraInterface(set, src);
        } else {
            if (src == Object.class) {
                set.add(Object.class);
                return;
            }
            set.add(src);
            Class<?>[] interfaces = src.getInterfaces();
            for (Class<?> inter : interfaces) {
                extraInterface(set, inter);
            }
            extraClass(set, src.getSuperclass());

        }

    }

    private static void extraInterface(List<Class<?>> set, Class<?> src) {
        set.add(src);
        src = src.getSuperclass();
        if (src == null)
            return;
        extraInterface(set, src);
    }

    private static Class<?> getWrapClass(Class<?> type) {
        if (type == int.class) {
            return Integer.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        throw new CasterException("impossible");
    }

    private static Object getDefaultValue(Class<?> targetClass) {
        if (targetClass == int.class) {
            return 0;
        }
        if (targetClass == boolean.class) {
            return false;
        }
        if (targetClass == double.class) {
            return (double) 0;
        }
        if (targetClass == float.class) {
            return (float) 0;
        }
        if (targetClass == long.class) {
            return 0L;
        }
        if (targetClass == short.class) {
            return (short) 0;
        }
        if (targetClass == byte.class) {
            return (byte) 0;
        }
        if (targetClass == char.class) {
            return (char) 0;
        }
        throw new CasterException("impossible");
    }

    @SuppressWarnings("rawtypes")
    public static <T> T toType(Object src, Type targetType) {
        if (targetType == null) {
            //这里没有什么好办法，只能先返回null再说
            return null;
        }

        if (targetType instanceof ParameterizedType) {
            return toParameterizedType(src, (ParameterizedType) targetType);
        }

        if (targetType instanceof Class) {
            return (T) to(src, (Class) targetType);
        }


        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T to(Object src, Class<T> targetClass) {
        if (src == null) {
            if (targetClass.isPrimitive()) {
                // default of primitive value
                return (T) getDefaultValue(targetClass);
            }
            return null;
        }

        if (targetClass == null) {

            //这里没有什么好办法，只能先返回null再说
            return null;
        }

        Class<?> srcType = src.getClass();
        if (srcType == targetClass || targetClass.isAssignableFrom(srcType)) {
            return (T) src;
        }
        String key = getKey(srcType, targetClass);
        ValueCaster caster = map.get(key);
        if (caster == null) {
            caster = get(srcType, targetClass);
            map.put(key, caster);
        }

        return (T) caster.to(src);
    }

    static final class CheckNullCaster implements ValueCaster {
        private final ValueCaster caster;

        CheckNullCaster(ValueCaster caster) {
            this.caster = caster;
        }

        @Override
        public final Object to(Object src) {
            if (src == null)
                return null;
            return caster.to(src);
        }
    }

    static final class WrapPriValueCaster implements ValueCaster {
        private final ValueCaster caster;
        private final Class<?> type;

        WrapPriValueCaster(ValueCaster caster, Class<?> toType) {
            this.caster = caster;
            this.type = toType;
        }

        @Override
        public final Object to(Object src) {
            if (src == null) {
                return getDefaultValue(type);
            }
            return caster.to(src);
        }
    }


    private static class EqValueCaster implements ValueCaster {

        @Override
        public Object to(Object src) {
            return src;
        }

    }

    /**
     * dest 是否 是 src的包装类
     * <p>
     * <p>
     * isWrapClass(int.class,Integer.class)==true
     * isWrapClass(Integer.class,int.class)==false
     *
     * @param src  如 int.class
     * @param dest 如 Integer.class
     * @return
     */
    private static boolean isWrapClass(Class<?> src, Class<?> dest) {
        try {

            return ((Class<?>) dest.getField("TYPE").get(null)) == src;
        } catch (Exception e) {
            return false;
        }
    }

    /// 一定是Array类型
    private static ValueCaster getWithIterable(Class<?> toType) {
        return new Iterable2Array(toType.getComponentType());
    }

    private static ValueCaster getWithArrayParameterizedType(ParameterizedType type){
        Class<?> rawType =Classes.getClass(type);
        if (!Collection.class.isAssignableFrom(rawType)) {
             throw new CasterException("Cannot cast array to " + type + " dest type must be Collection ");
        }

        Type[] types = type.getActualTypeArguments();
        if(types.length != 1){
            throw new CasterException("Cannot cast array to " + type + " dest type must be Collection ");
        }

        return new Array2ParameterizedType( rawType,types[0]);
    }

    /// 一定是Array类型
    private static ValueCaster getWithArray(Class<?> toType) {

        Class<?> componentType = toType.getComponentType();
        if(componentType.isPrimitive()){

            return new Array2Primitive(componentType);

        }else{
            return new Array2Array(componentType);
        }

    }

    private static Collection newCollection(Class<?> type){
        if(type.isInterface()){

            if(List.class.isAssignableFrom(type)){
                return new ArrayList();
            }else if(Set.class.isAssignableFrom(type)){
                return new LinkedHashSet();
            }else{
                throw new CasterException("Cannot initialize "+type);
            }


        }else{
            return (Collection) Classes.newInstance(type);
        }
    }

    private static class Array2ParameterizedType implements ValueCaster{

        private final Class<?> collectionType;
        private final Type elementType;

        public Array2ParameterizedType(Class<?> collectionType,Type elementType){
            this.collectionType = collectionType;
            this.elementType =elementType;
        }

        @Override
        public Object to(Object src) {
            Object[] it = (Object[])src;
            Collection collection = newCollection(collectionType);
            for(Object i : it){
                collection.add(Caster.toType(i,elementType));
            }

            return collection;
        }
    }
    private static class Array2Primitive implements ValueCaster {

        private Class<?> toType;

        public Array2Primitive(Class<?> toType) {
            this.toType = toType;
        }

        @Override
        public Object to(Object src) {
            Object[] iterable = (Object[]) src;
            Object array = java.lang.reflect.Array.newInstance(toType, iterable.length);
            int i = 0;
            for (Object data : iterable) {
                java.lang.reflect.Array.set(array, i++,Caster.toType(data, toType));
            }
            //array

            return array;
        }
    }
    private static class Array2Array implements ValueCaster {

        private Class<?> toType;

        public Array2Array(Class<?> toType) {
            this.toType = toType;
        }

        @Override
        public Object to(Object src) {
            Object[] iterable = (Object[]) src;
            Object[] array = (Object[])java.lang.reflect.Array.newInstance(toType, iterable.length);
            int i = 0;
            for (Object data : iterable) {
                array[i++]= Caster.toType(data, toType);
            }
            //array

            return array;
        }
    }

    private static class Iterable2Array implements ValueCaster {

        private Class<?> toType;

        public Iterable2Array(Class<?> toType) {
            this.toType = toType;
        }

        @Override
        public Object to(Object src) {
            Iterable iterable = (Iterable) src;
            List list = new ArrayList();
            for (Object data : iterable) {
                list.add(Caster.toType(data, toType));
            }
            //array
            Object array = java.lang.reflect.Array.newInstance(toType, list.size());
            list.toArray((Object[]) array);
            return array;
        }
    }

    private static EqValueCaster eqValueCaster = new EqValueCaster();

    private static ValueCaster get(Class<?> srcType, Class<?> toType) {
        /**
         * 如果是int 转 Integer 或者是Integer 转 int,则可以相互转化
         */
        if (isWrapClass(srcType, toType) || isWrapClass(toType, srcType)) {
            return eqValueCaster;
        }


        if (toType.isArray()) {
            if (Iterable.class.isAssignableFrom(srcType)) {
                return getWithIterable(toType);
            } else if (srcType.isArray()) {
                if (srcType.getComponentType() == toType.getComponentType()) {
                    return eqValueCaster;
                }
                return getWithArray(toType);
            }
        }


        ValueCaster caster = getPosible(srcType,toType,map);
        if(caster!=null){
            return caster;
        }

        final CasterProvider[] providers = Caster.providers;
        if (providers != null) {
            for (CasterProvider casterProvider : providers) {
                caster = casterProvider.getCaster(srcType, toType);
                if (caster != null) {
                    String key = new StringBuilder(srcType.toString()).append("2").append(toType.toString()).toString();
                    map.put(key, caster);
                    return caster;
                }
            }
        }


        throw new CasterException(String.format("Cannot cast %s to %s ", srcType.getName(), toType.getName()));
    }

    public static <V> V getPosible(Class<?> srcType,Class<?> toType,Map<String,V> map){

        /**
         * 否则需要将class全部解出来，以便加以判断
         *
         * 解出来是指：
         *
         * 超类（递归） 接口 、接口超类（递归）
         *
         */
        String[] srcs = extraClass(srcType);
        String[] tos = extraClass(toType);

        for (String s : srcs) {
            for (String t : tos) {
                String key = new StringBuilder(s).append("2").append(t).toString();
                V caster = map.get(key);
                if (caster == null) {
                    continue;
                }
                return caster;
            }
        }
        return null;
    }

    private static class Object2String implements ValueCaster {

        @Override
        public Object to(Object src) {
            return String.valueOf(src);
        }

    }

    private static abstract class ParameterizedTypeCasterfactory<SRC, DEST> {
        private Class<?> src;
        private Class<?> dest;

        public ParameterizedTypeCasterfactory() {
            Type superclass = getClass().getGenericSuperclass();
            Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
            this.src = (Class<?>) types[0];
            this.dest = (Class<?>) types[1];
        }

        public boolean is(Object obj, ParameterizedType targetType) {
            return src.isInstance(obj) && targetType.getRawType() == dest;
        }

        public boolean is(Class<?> srcType, ParameterizedType targetType) {
            return src.isAssignableFrom(srcType) && targetType.getRawType() == dest;
        }

        public abstract ValueCaster create(ParameterizedType targetType);
    }


    /**
     * @param src
     * @param targetType
     * @return
     */
    private static <T> T toParameterizedType(Object src, ParameterizedType targetType) {
        assert (targetType != null);

        if (src == null) {
            return null;
        }

        ValueCaster caster = getWithObject(src, targetType);
        if (caster == null) {
            throw new CasterException(String.format("Cannot cast %s to %s", src, targetType));
        }
        return (T) caster.to(src);
    }

    private static ValueCaster getWithType(Class<?> srcType, ParameterizedType targetType) {
        String key = new StringBuilder().append(srcType.getName()).append("2").append(targetType.toString()).toString();

        ValueCaster caster = map.get(key);
        if (caster == null) {
            for (ParameterizedTypeCasterfactory type : typeCanbeConvertToParameterizedType) {
                if (type.is(srcType, targetType)) {
                    caster = type.create(targetType);
                    map.put(key, caster);
                    break;
                }
            }
        }
        return caster;
    }

    public static ValueCaster wrapType(Class<?> srcType, Type toType) {
        if (toType == null) {
            throw new NullPointerException("srcType and toType must not be null");
        }
        if (srcType == null) {
            //如果一开始并不知道要转什么
            return wrapFirstVisit(toType);
        }


        if (toType instanceof ParameterizedType) {
            return wrapParameterizedType(srcType, (ParameterizedType) toType);
        }

        if (toType instanceof Class) {
            return wrap(srcType, (Class<?>) toType);
        }


        throw new CasterException("Cannot cast " + srcType + " to " + toType);
    }

    private static ValueCaster wrapParameterizedType(Class<?> srcType, ParameterizedType toType) {
        if (srcType == null || toType == null) {
            throw new NullPointerException("srcType and toType must not be null");
        }

        ValueCaster caster = getWithType(srcType, toType);
        if (caster == null) {
            throw new CasterException("Cannot cast " + srcType + " to " + toType);
        }

        return new CheckNullCaster(caster);

    }

    private static ValueCaster getWithObject(Object src, ParameterizedType targetType) {


        String key = new StringBuilder().append(src.getClass().getName())
                .append("2")
                .append(targetType.toString())
                .toString();

        ValueCaster caster = map.get(key);
        if (caster == null) {
            Class<?> srcType = src.getClass();
            if (srcType.isArray()) {
                caster = getWithArrayParameterizedType(  targetType);
            } else if (src instanceof Iterable) {
                caster = getWithIterable(targetType);
            } else if (src instanceof Map) {
                caster = getWithMap(targetType);
            } else {
                for (ParameterizedTypeCasterfactory type : typeCanbeConvertToParameterizedType) {
                    if (type.is(src, targetType)) {
                        caster = type.create(targetType);
                        break;
                    }
                }
            }
            if (caster != null) {
                map.put(key, caster);
            }

        }
        return caster;
    }

    private static ValueCaster getWithMap(ParameterizedType targetType) {
        Type rowType = targetType.getRawType();
        if (rowType instanceof Class) {
            if (!Map.class.isAssignableFrom((Class) rowType)) {
                throw new CasterException("Cannot cast Map to type:" + targetType + " targetType must be Map");
            }
            return new MapValueCaster(targetType);
        } else {
            throw new CasterException("Cannot cast Map to type:" + targetType + " targetType must be Map");
        }

    }

    private static ValueCaster getWithIterable(
            ParameterizedType targetType) {
        Type[] types = targetType.getActualTypeArguments();
        if (types.length > 1) {
            throw new CasterException("Cannot cast src:Iterable to type:" + targetType);
        }

        Type collectionType = targetType.getRawType();
        //如果还是一个泛型?
        if (collectionType instanceof ParameterizedType) {
            throw new CasterException("Cannot cast src:Iterable to type:" + targetType + " targetType must be some kind of Iterable");
        }

        Class<?> collectionClass = (Class<?>) collectionType;
        if (!Collection.class.isAssignableFrom(collectionClass)) {
            throw new CasterException("Cannot cast src:Iterable to type:" + targetType + " targetType must be some kind of Iterable");
        }

        return new IterableValueCaster((Class<? extends Collection>) collectionClass, types[0]);
    }

    private static Collection newInstanceOfCollection(Class<? extends Collection> type) {
        if (!type.isInterface()) {
            try {
                return type.newInstance();
            } catch (Exception e) {
                throw new CasterException("Cannot initialize type " + type);
            }
        }
        if (Set.class.isAssignableFrom(type)) {
            return new LinkedHashSet();
        }

        return new ArrayList();
    }

    private static class IterableValueCaster implements ValueCaster {

        private Class<? extends Collection> collectionClass;
        private Type objectType;


        public IterableValueCaster(Class<? extends Collection> collectionClass, Type objectType) {
            this.collectionClass = collectionClass;
            this.objectType = objectType;
        }

        @Override
        public Object to(Object src) {
            Iterable it = (Iterable) src;
            Collection result = newInstanceOfCollection(collectionClass);
            for (Object data : it) {
                result.add(Caster.toType(data, objectType));
            }
            return result;
        }
    }

    private static class MapValueCaster implements ValueCaster {

        private Type[] types;

        public MapValueCaster(ParameterizedType targetType) {
            types = targetType.getActualTypeArguments();
        }

        @Override
        public Object to(Object src) {
            Map map = (Map) src;
            Map dest = new LinkedHashMap();
            Iterator<Map.Entry> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                dest.put(Caster.toType(entry.getKey(), types[0]),
                        Caster.toType(entry.getValue(), types[1]));
            }

            return dest;
        }
    }

    /**
     * 注意日期转换比较特殊，有如下模式:
     * <p>
     * yyyyMMdd   总结一下 : 8-12位数字，偶数位数  或者 4-2-2[ 2[:2[:2]]]
     * yyyy-MM-dd
     * yyyyMMddhhmmss
     * yyyy-MM-dd hh:mm:ss:SSS
     * yyyy-MM-dd hh:mm
     * yyyy-MM-dd hh
     */
    private static final String SHORT_DATE_TIME = "yyyyMMddHHmmssSSS";
    private static final String LONG_DATE_TIME = "yyyy-MM-dd HH:mm:ss:SSS";

    private static class String2Date implements ValueCaster {
        @Override
        public Object to(Object src) {
            String str = (String) src;
            if (str.length() < 8) {
                throw new CasterException("Not a valid date :" + str);
            }

            if (StringUtils.isNumeric(str)) {
                //纯数字
                if (str.length() % 2 > 0 && str.length() != SHORT_DATE_TIME.length()) {
                    throw new CasterException("Not a valid date :" + str);
                }

                try {
                    return new SimpleDateFormat(SHORT_DATE_TIME.substring(0, str.length()))
                            .parse(str);
                } catch (ParseException e) {
                    throw new CasterException("Not a valid date :" + str);
                }
            }

            //长时间

            try {
                return new SimpleDateFormat(LONG_DATE_TIME.substring(0, str.length()))
                        .parse(str);
            } catch (ParseException e) {
                throw new CasterException("Not a valid date :" + str);
            }

        }
    }

    private static class File2ByteArray implements ValueCaster {
        @Override
        public Object to(Object src) {
            try {
                return Io.readBytes((File) src);
            } catch (IOException e) {
                throw new CasterException("Cannot read file " + src, e);
            }
        }
    }


    private static class File2InputStream implements ValueCaster {
        @Override
        public Object to(Object src) {
            File file = (File) src;
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new CasterException("Cannot open file as stream ,file not found" + file, e);
            }
        }
    }

    private static class Timestamp2Date implements ValueCaster {
        @Override
        public Object to(Object src) {
            Timestamp timestamp = (Timestamp) src;
            return new Date(timestamp.getTime());
        }
    }

    private static class Long2Timestamp implements ValueCaster {
        @Override
        public Object to(Object src) {
            Long value = (Long)src;     //毫秒
            return new Timestamp(value );
        }
    }

    private static class Boolean2Int implements ValueCaster {
        @Override
        public Object to(Object src) {
            return ((Boolean)src) ? 1 : 0;
        }
    }

    private static class Boolean2Char implements ValueCaster {
        @Override
        public Object to(Object src) {
            return ((Boolean)src)?'1':'\0';
        }
    }

    private static class Boolean2Long implements ValueCaster {
        @Override
        public Object to(Object src) {
            return ((Boolean)src) ? 1L : 0L;
        }
    }

    private static class Boolean2Byte implements ValueCaster {
        @Override
        public Object to(Object src) {
            return ((Boolean)src) ? (byte)1 : (byte)0;
        }
    }

    private static class Boolean2Float implements ValueCaster {
        @Override
        public Object to(Object src) {
            return ((Boolean)src) ? (float)1 : (float)0;

        }
    }

    private static class Boolean2Double implements ValueCaster {
        @Override
        public Object to(Object src) {
            return ((Boolean)src) ? (double)1 : (double)0;
        }
    }

    private static class Map2DataObject implements ValueCaster {
        @Override
        public Object to(Object src) {

            return DataObject.wrap((Map)src);
        }
    }
}
