package com.jzoom.zoom.web.router.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.web.action.ActionHandler;
import com.jzoom.zoom.web.action.impl.GroupActionHandler;
import com.jzoom.zoom.web.exception.StatusException;
import com.jzoom.zoom.web.router.Router;
import com.jzoom.zoom.web.router.RouterNode;
import com.jzoom.zoom.web.router.RouterParamRule;

public class SimpleRouter implements Router, Destroyable {
	
	private Map<String, ActionHandler> actionMap;
	private final RouterParamRule rule;
	private RouterNode node;
	/**
	 * just keep the instance
	 */
	private Map<ActionHandler, Boolean> actionPool;
	
	public SimpleRouter(RouterParamRule rule) {
		if(rule==null) {
			throw new NullPointerException();
		}
		
		actionMap = new ConcurrentHashMap<String, ActionHandler>();
		actionPool = new ConcurrentHashMap<ActionHandler, Boolean>();
		this.rule = rule;
		this.node = new RouterNode(0);
	}
	

	@Override
	public Collection<ActionHandler> getActionHandlers() {
		return actionPool.keySet();
	}

	public void register( String url, ActionHandler action ) {
		actionPool.put(action, Boolean.TRUE);
		if(this.rule.match(url)) {
			if(url.startsWith("/")) {
				url = url.substring(1);
			}
			String[] parts = url.split("/");
			if(parts.length==0) {
				parts = new String[] {""};
			}
			node.register(parts,getNames(parts),rule,action);
		}else {
			if(!url.startsWith("/")) {
				url = "/" + url;
			}
			ActionHandler src = actionMap.get(url);
			actionMap.put(url, GroupActionHandler.from(src, action));
			
		}
	}
	
	private String[] getNames(String[] parts) {
		String[] names = new String[parts.length];
		int index=0;
		for (String part : parts) {
			names[index++] = rule.getParamName(part);
		}
		return names;
	}
	
	public ActionHandler match(String url,HttpServletRequest request) {
		ActionHandler action = actionMap.get(url);
		boolean matchExact = false;
		if(action!=null) {
			matchExact = true;
			//精确匹配，看下方法是否支持
			if(action.supportsHttpMethod(request.getMethod())) {
				return action;
			}
		}
		if(url.startsWith("/")) {
			url = url.substring(1);
		}
		
		String[] parts = url.split("/");
		
		
		ActionHandler handler = node.match(parts,new String[parts.length], request);
		if(handler==null && matchExact) {
			throw new StatusException.NotAllowedHttpMethodException(request.getMethod());
		}
		return handler;
	}
	
	public ActionHandler match( HttpServletRequest request ) {
		String url = request.getServletPath();
		return match(url,request);
	}

	public void destroy() {
		if( actionMap!=null) {
			Classes.destroy(actionMap);
			actionMap = null;
		}
		
		if(node!=null) {
			Classes.destroy(node);
			node = null;
		}
		
		if(actionPool!=null) {
			actionPool.clear();
			actionMap = null;
		}
	}



}
