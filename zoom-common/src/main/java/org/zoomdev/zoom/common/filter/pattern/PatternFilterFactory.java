package org.zoomdev.zoom.common.filter.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.zoomdev.zoom.common.filter.AlwaysAcceptFilter;
import org.zoomdev.zoom.common.filter.AndFilter;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.OrFilter;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.Strings;

/**
 * 
 * 字符串模式匹配工厂
 * {@see PatternFilter}
 * 
 * @author jzoom
 *
 */
public class PatternFilterFactory {

	private static Map<String, Filter<String>> filterMap = new ConcurrentHashMap<String, Filter<String>>();
	
	/**
	 * 这个用来匹配 *xxx*   *xxx   xxx* 的形式
	 */
	private static Pattern SIMPLE_PATTERN = Pattern.compile("(\\**)([^\\*]+)(\\**)");
	
	/**
	 * 用来替换字符串中的*
	 */
	private static String EXP = "[a-zA-Z0-9_\\.\\/\\+\\-]*";
	public static void clear() {
		Classes.destroy(filterMap);
	}
	public static Filter<String> createFilter(String pattern) {
		
		if(StringUtils.isEmpty(pattern)) {
			return new AlwaysAcceptFilter<String>();
		}
		
		Filter<String> filter = filterMap.get(pattern);
		if(filter==null) {
			filter = createGroup(pattern);
			filterMap.put(pattern, filter);
		}
		return filter;
		
	}
	
	private static Filter<String> createGroup(String pattern) {
		char[] chars = pattern.toCharArray();
		StrReader reader = new StrReader(chars);
		return read(reader,false);
	}
	
	private static class StrReader{
		char[] chars;
		int index = 0;
		
		public StrReader(char[] chars) {
			this.chars = chars;
		}
		
		public char read() {
			if(index >= chars.length) {
				return '\0';
			}
			return chars[index++];
		}
		
		
		public String readPattern() {
			int endIndex = chars.length-1;
			for(int i=index; i < chars.length; ++i) {
				char c= chars[i];
				if(c=='(' || c==')' || c=='!' || c=='&' || c=='|') {
					endIndex = i-1;
					break;
				}
			}
			
			String str =  new String(chars, index-1, endIndex-index+2);
			index = endIndex+1;
		//System.out.println(str);
			return str;
			
		}
	}

	@SuppressWarnings("unchecked")
	private static Filter<String> read( StrReader chars ,boolean quick ){
		List<Filter<String>> list = new ArrayList<Filter<String>>();
		Filter<String> filter ;
		
		WHILE:
		while(true) {
			char c = chars.read();
			if(c == '\0') {
				break;
			}
			switch (c) {
			case '(':{
				filter = read(chars,false);
				break;
			}
				
			case ')':
				//back
				break WHILE;
			case '!':{
				filter = new NotFilter<String>(read(chars,true));
				break;
			}
			case '&':{
				if(list.size() == 0) {
					return read(chars,true);
				}
				Filter<String> left =list.remove(list.size()-1);
				filter = new AndFilter<String>(left , read(chars,true) );
				break;
			}
				
			case '|':{
				filter = read(chars, false);
				break;
			}
			default:{
				filter =create(chars.readPattern());
				break;
			}
				
			}
			
			if(quick) {
				return filter;
			}
			list.add(filter);
		}
		

		if(list.size()==0) {
			throw new RuntimeException();
		}
		
		if(list.size() == 1) {
			return list.get(0);
		}
		
		return new OrFilter<String>(list.toArray( new Filter[ list.size() ] ));
		
	}
	
	private static Filter<String> create(String pattern){
		if(Strings.isAll(pattern,'*')) {
			return new AlwaysAcceptFilter<String>();
		}
		
		//* 替换为[a-zA-Z\\.\\/]*
		
		Matcher matcher = SIMPLE_PATTERN.matcher(pattern);
		if(matcher.matches()) {
			
			if(StringUtils.isEmpty(matcher.group(1))) {
				//空的   xxx* 或者 xxx
				if(StringUtils.isEmpty(matcher.group(3))) {
					//只有非*?
					return new ExactFilter(matcher.group(2));
				}else {
					//只有非*?
					return new StartsWithFilter(matcher.group(2));
				}
			}else {
				if(StringUtils.isEmpty(matcher.group(3))) {
					return new EndsWithFilter(matcher.group(2));
				}else {
					return new ContainsFilter(matcher.group(2));
				}
				
			}
			
		}
		pattern = pattern.replace(".", "\\.");
		pattern = pattern.replace("*", EXP);
		
		return new PatternFilter(pattern);
	}

	
}
