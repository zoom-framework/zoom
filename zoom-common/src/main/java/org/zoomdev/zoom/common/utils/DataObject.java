package org.zoomdev.zoom.common.utils;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.caster.ValueCaster;

import java.sql.Clob;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jzoom
 */
public class DataObject extends LinkedHashMap<String, Object> {


    static {
        Caster.register(Map.class, DataObject.class, new ValueCaster() {
            @Override
            public Object to(Object src) {
                return DataObject.wrap((Map<String, Object>) src);
            }
        });


        Caster.register(String.class, DataObject.class, new ValueCaster() {
            @Override
            public Object to(Object src) {
                return Caster.to(Caster.to(src, Map.class), DataObject.class);
            }
        });

        Caster.register(Clob.class, DataObject.class, new ValueCaster() {
            @Override
            public Object to(Object src) {
                return Caster.to(Caster.to(src, String.class), DataObject.class);
            }
        });
    }

    /**
     *
     */
    private static final long serialVersionUID = -2225042456289890443L;

    public DataObject(Map<? extends String, ? extends Object> m) {
        super(m);
    }

    public DataObject() {
        super();
    }

    public DataObject(int initialCapacity) {
        super(initialCapacity);
    }

    public static DataObject as(Object... args) {
        return wrap(MapUtils.asMap(args));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static DataObject wrap(Map<String, Object> data) {
        DataObject dataObject = new DataObject();
        dataObject.putAll(data);
        return dataObject;
    }


    public DataObject set(String key, Object value) {
        put(key, value);
        return this;
    }

    @SuppressWarnings("rawtypes")
    public DataObject getMap(String key) {
        Object data = get(key);
        if (data == null) {
            return null;
        }
        if (data instanceof DataObject) {
            return (DataObject) data;
        }
        if (data instanceof Map) {
            return DataObject.wrap((Map) get(key));
        }

        throw new Caster.CasterException("Cannot cast data into DataObject");
    }

    public String getString(String key) {
        return Caster.to(get(key), String.class);
    }

    public long getLong(String key) {
        return Caster.to(get(key), long.class);
    }

    public double getDouble(String key) {
        return Caster.to(get(key), double.class);
    }

    public int getInt(String key) {
        return Caster.to(get(key), int.class);
    }

    public boolean getBoolean(String key) {
        return Caster.to(get(key), boolean.class);
    }

    public float getFloat(String key) {
        return Caster.to(get(key), float.class);
    }

    public short getShort(String key) {
        return Caster.to(get(key), short.class);
    }

    public byte getByte(String key) {
        return Caster.to(get(key), Byte.class);
    }

    public char getChar(String key) {
        return Caster.to(get(key), Character.class);
    }

    public byte[] getBytes(String key) {
        return Caster.to(get(key), byte[].class);
    }

    public <T> T get(String key, Class<T> classOfT) {
        return Caster.to(get(key), classOfT);
    }


}
