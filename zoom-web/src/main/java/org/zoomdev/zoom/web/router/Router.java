package org.zoomdev.zoom.web.router;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.zoomdev.zoom.web.action.ActionHandler;

public interface Router {

	void register(String key, ActionHandler action);

	ActionHandler match(HttpServletRequest request);

	Collection<ActionHandler> getActionHandlers();
}
