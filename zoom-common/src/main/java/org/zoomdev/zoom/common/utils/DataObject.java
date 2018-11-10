package org.zoomdev.zoom.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.zoomdev.zoom.caster.Caster;

/**
 * 
 * @author jzoom
 *
 */
public class DataObject extends HashMap<String, Object> {

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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static DataObject wrap(Map data) {
		DataObject dataObject = new DataObject();
		dataObject.putAll(data);
		return dataObject;
	}


	@SuppressWarnings("rawtypes")
	public DataObject getMap(String key) {
		return DataObject.wrap( (Map)get(key));
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

	public <T> T get(String key, Class<?> classOfT) {
		return Caster.to(get(key), classOfT);
	}
	
	
	
	
}