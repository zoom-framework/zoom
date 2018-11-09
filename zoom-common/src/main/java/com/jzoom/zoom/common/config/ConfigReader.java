package com.jzoom.zoom.common.config;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.common.ConfigurationConstants;
import com.jzoom.zoom.common.filter.Filter;
import com.jzoom.zoom.common.filter.pattern.PatternFilterFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * properties或者yml配置
 * 
 * 
 * 
 * 
 * @author jzoom
 *
 */
public class ConfigReader implements ConfigValueParser{

	private static final Log logger = LogFactory.getLog(ConfigReader.class);
	

	private static ConfigReader applicationReader = new ConfigReader() {
		/**
		 * 对于一个主配置类型来说,最主要的一个属性是env,表示当前的环境， 比如 env=test,那么在解析完 application.xx的主配置文件之后，
		 * 会再去解析application-test.xxx文件,并把本配置文件的配置合并到主配置里面 注意是覆盖合并。
		 * 
		 */
		@Override
		public void load(File file){
			super.load(file);
			String env = getString( ConfigurationConstants.ENV );
			if (!StringUtils.isEmpty(env)) {
				String name = file.getName();
				int l = name.lastIndexOf('.');
				assert (l > 0);
				String prev = name.substring(0, l);
				String ext = name.substring(l);

				super.load(new File(file.getParent(),
						new StringBuilder()
							.append(prev)
							.append("-")
							.append(env)
							.append(ext).toString()));

			}

			
		}

	};

	private Map<String, Object> data;

	ConfigReader() {
		/**
		 * 注意这个配置理论上在程序运行期间是不能修改的,所以用HashMap
		 * 
		 */
		data = new LinkedHashMap<String, Object>();
	}

	public Object get(String key) {
		return data.get(key);
	}

	public String getString(String key) {
		return Caster.to(data.get(key), String.class);
	}

	/**
	 * 获取默认reader
	 * 
	 * @return
	 */
	public static ConfigReader getDefault() {
		return applicationReader;
	}

	/**
	 * 加载配置
	 * 
	 * @param is
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public void load(File file) {
		assert (file != null);
		
		String name = file.getName();

		logger.info("加载配置" + file.getAbsolutePath());

		if (name.endsWith(".yml")) {
			
		}
		if (name.endsWith(".json")) {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

			try {
				Map<String, Object> data = mapper.readValue(file, Map.class);
				for (Entry<String, Object> entry : data.entrySet()) {
					this.data.put(entry.getKey(), parse(entry.getValue()));
				}
			}catch (Exception e) {
				throw new RuntimeException( String.format("配置文件%s加载失败", file) );
			}
			
		} else if (name.endsWith(".properties")) {
			try {
				data.putAll( new PropertiesConfigReader().load(new FileInputStream(file), this) );
			} catch (Exception e) {
				throw new RuntimeException( String.format("配置文件%s加载失败", file) );
			}
		} else {
			throw new RuntimeException("不支持的配置类型" + file);
		}
		
		
	}

	
	public Object parse( String value ) {
		return decodeStringValue(value);
	}

	private static Pattern PARAM_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9_]+)\\:([a-zA-Z0-9_]+)\\}");

	private static Object decodeStringValue(String value) {
		Matcher matcher = PARAM_PATTERN.matcher(value);
		if (matcher.matches()) {
			String prefix = matcher.group(1);
			String key = matcher.group(2);
			if ("env".equals(prefix)) {
				return System.getenv(key);
			} else if ("".equals(prefix)) {
				
			}
		}
		
		if(value.contains("{") || value.contains("}") || value.contains("$")) {
			logger.warn(String.format("值%s含有特殊字符{},如果是一个变量则格式为${},如果确认没有写错，那么请忽略",value));
		}
		
		return value;
	}

	
	
	/**
	 * 获取对应的key
	 * @param pattern   模式，类似redis的keys命令 ,  可以用 *\/key*等
	 */
	public Set<String> keys( String pattern ) {
		//注意是有顺序的
		Set<String> keys = new LinkedHashSet<String>();
		Filter<String> keyFilter = PatternFilterFactory.createFilter(pattern);
		for (String key : data.keySet()) {
			if(keyFilter.accept(key)) {
				keys.add(key);
			}
		}
		return keys;
	}

	@Override
	public Object parse(Object value) {
		if (value instanceof String) {
			return decodeStringValue((String) value);
		}
		return value;
	}
}
