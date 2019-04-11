package org.zoomdev.zoom.web.router.impl;

import org.zoomdev.zoom.http.Destroyable;
import org.zoomdev.zoom.http.utils.Classes;
import org.zoomdev.zoom.web.action.ActionHandler;
import org.zoomdev.zoom.web.action.impl.GroupActionHandler;
import org.zoomdev.zoom.web.exception.StatusException;
import org.zoomdev.zoom.web.router.Router;
import org.zoomdev.zoom.web.router.RouterParamRule;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZoomRouter implements Router, Destroyable {

    Map<String, ActionHandler> actionMap;
    final RouterParamRule rule;
    RouterNode node;
    /**
     * just keep the instance, so that we can visit them in future
     */
    private Map<ActionHandler, Boolean> actionPool;


    public ZoomRouter(){
        this(new BracesRouterParamRule());
    }

    public ZoomRouter(RouterParamRule rule) {
        if (rule == null) {
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

    public RemoveToken register(String url, final ActionHandler action) {
        actionPool.put(action, Boolean.TRUE);
        final RemoveToken removeToken = new RemoveToken() {
            @Override
            public void remove() {
                actionPool.remove(action);
            }
        };
        if (this.rule.match(url)) {
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
            String[] parts = url.split("/");
            if (parts.length == 0) {
                parts = new String[]{""};
            }
            return new ActionHandlerUtils.GroupRemove(
                    removeToken,
                    node.register(parts, getNames(parts), rule, action)
            );
        } else {
            if (!url.startsWith("/")) {
                url = "/" + url;
            }
            ActionHandler src = actionMap.get(url);
            src = GroupActionHandler.from(src, action);
            actionMap.put(url, src);
            return new ActionHandlerUtils.GroupRemove(
                    removeToken,
                    new ActionHandlerUtils.RemoveRouter(actionMap, url, action)
            );

        }
    }


    private String[] getNames(String[] parts) {
        String[] names = new String[parts.length];
        int index = 0;
        for (String part : parts) {
            names[index++] = rule.getParamName(part);
        }
        return names;
    }

    public ActionHandler match(String url, HttpServletRequest request) {
        ActionHandler action = actionMap.get(url);
        boolean matchExact = false;
        if (action != null) {
            matchExact = true;
            //精确匹配，看下方法是否支持
            if (action.supportsHttpMethod(request.getMethod())) {
                return action;
            }
        }
        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        String[] parts = url.split("/");


        ActionHandler handler = node.match(parts, new String[parts.length], request);
        if (handler == null && matchExact) {
            throw new StatusException.NotAllowedHttpMethodException(request.getMethod());
        }
        return handler;
    }


    public ActionHandler match(HttpServletRequest request) {

        String servletPath = request.getServletPath();
        return match(servletPath, request);
    }

    public void destroy() {
        if (actionMap != null) {
            Classes.destroy(actionMap);
            actionMap = null;
        }

        if (node != null) {
            Classes.destroy(node);
            node = null;
        }

        if (actionPool != null) {
            actionPool.clear();
            actionMap = null;
        }
    }


}
