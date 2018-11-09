package com.jzoom.zoom.web.router;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.jzoom.zoom.web.action.ActionHandler;

public interface Router {

	void register(String key, ActionHandler action);

	ActionHandler match(HttpServletRequest request);

	Collection<ActionHandler> getActionHandlers();
}
