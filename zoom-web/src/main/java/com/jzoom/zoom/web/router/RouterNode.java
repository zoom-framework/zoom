package com.jzoom.zoom.web.router;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.web.action.ActionHandler;
import com.jzoom.zoom.web.action.impl.GroupActionHandler;


/**
 * 路由节点
 * @author jzoom
 *
 * @param 
 */
public class RouterNode {

	
	Map<String, RouterNode> children;
	
	int level;
	
	private ActionHandler action;
	
	RouterNode pattern;
	
	
	
	public RouterNode(int level) {
		this.level = level;
	}
	
	public ActionHandler match(String[] parts,String[] values, HttpServletRequest request) {
		final int level = this.level;
		if(parts.length == level) {
			if(action==null) {
				//没有
				return null;
			}
			String[] names = action.getPathVariableNames();
			int indexOfName = 0;
			for(int i=0 ,c=values.length; i < c; ++i) {
				String value = values[i];
				if(value!=null) {
					for(int j=indexOfName; j < names.length; ++j) {
						if(names[j]!=null) {
							request.setAttribute(names[j], value);
							indexOfName = j+1;
							break;
						}
					}
					
				}
			}
			
			return action;
		}
		String current = parts[level];
		if(children==null) {
			//已经是最终节点了
			if(pattern==null) {
				return null;
			}
			//记录一下值，然后在到下一级
			values[level] = current;
			return pattern.match(parts,values,request);
		}
		//查找同级别的
		RouterNode node = children.get(current);
		if(node==null) {
			//模糊匹配的处理,对于同一级别的，如果没有找到模糊匹配，那么就是null
			if(pattern==null) {
				return null;
			}
			//记录一下值，然后在到下一级
			values[level] = current;
			return pattern.match(parts, values,request);
		}
		
		ActionHandler handler = node.match(parts,values, request);
		if(handler==null) {
			if(pattern==null)return null;
			values[level] = current;
			handler = pattern.match(parts, values, request);
		}
		return handler;
	}

	/**
	 * 注册路由
	 * @param parts
	 * @param rule
	 * @param value
	 */
	public void register(String[] parts, String[] names,RouterParamRule rule, ActionHandler value) {
		if(parts ==null || parts.length==0) {
			throw new RuntimeException("parts长度必须大于0");
		}
		final int level = this.level;
		String current = parts[level];
		
		String paramName = names[level];
		if(paramName!=null) {
			//不一定有下级
			if(pattern==null) {
				pattern = new RouterNode(level+1);
			}
			if(parts.length-1==level) {
				pattern.setAction(value);
				return;
			}
			pattern.register(parts,names, rule,value);
		}else {
			//当前的key为下级注册
			if(children==null) {
				children = new ConcurrentHashMap<String, RouterNode>();
			}
			RouterNode child = children.get(current);
			if(child==null) {
				child = new RouterNode(level+1);
				children.put(current, child);
			}
			if(parts.length-1==level) {
				child.setAction(value);
				return;
			}
			child.register(parts,names,rule,value);
		}
		
		
	}

	/**
	 * 清除所有路由
	 */
	public void clear() {
		if(children!=null) {
			for (Entry<String,RouterNode> entry : children.entrySet()) {
				entry.getValue().clear();
			}
			children.clear();
			children = null;
		}
		
		if(pattern!=null) {
			pattern.clear();
			pattern = null;
		}
		
		if(action!=null) {
			Classes.destroy(action);
			action = null;
		}
		
		
	}

	public ActionHandler getAction() {
		return action;
	}

	public void setAction(ActionHandler action) {
		this.action = GroupActionHandler.from(this.action, action);
	}

}
