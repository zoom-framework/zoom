package org.zoomdev.zoom.web.action.impl;

import org.zoomdev.zoom.web.action.ActionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class GroupActionHandler implements ActionHandler {

    private ActionHandler[] actionHandlers;

    public GroupActionHandler(ActionHandler... actionHandlers) {
        this.actionHandlers = actionHandlers;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroupActionHandler) {

            GroupActionHandler target = (GroupActionHandler) obj;
            if (target.actionHandlers.length != this.actionHandlers.length) {
                return false;
            }

            for (int i = 0; i < actionHandlers.length; ++i) {
                if (!actionHandlers[i].equals(target.actionHandlers[i])) {
                    return false;
                }
            }
            return true;

        }
        return false;
    }

    public static ActionHandler from(ActionHandler handler1, ActionHandler handler2) {
        if (handler1 == null)
            return handler2;
        if (handler1 instanceof GroupActionHandler) {
            return from((GroupActionHandler) handler1, handler2);
        }
        return new GroupActionHandler(handler2, handler1);
    }

    public static GroupActionHandler from(GroupActionHandler groupActionHandler, ActionHandler handler) {
        int len = groupActionHandler.actionHandlers.length;
        ActionHandler[] actionHandlers = new ActionHandler[len + 1];
        for (int i = 1; i <= len; ++i) {
            actionHandlers[i] = groupActionHandler.actionHandlers[i - 1];
        }
        actionHandlers[0] = handler;
        Arrays.fill(groupActionHandler.actionHandlers, null);
        groupActionHandler.actionHandlers = null;
        return new GroupActionHandler(actionHandlers);
    }


    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response) {
        for (ActionHandler actionHandler : actionHandlers) {
            if (actionHandler.supportsHttpMethod(request.getMethod())) {
                actionHandler.handle(request, response);
                return true;
            }

        }
        return false;
    }


    public ActionHandler[] getActionHandlers() {
        return actionHandlers;
    }


    public void setActionHandlers(ActionHandler[] actionHandlers) {
        this.actionHandlers = actionHandlers;
    }

    @Override
    public boolean supportsHttpMethod(String method) {
        for (ActionHandler actionHandler : actionHandlers) {
            if (actionHandler.supportsHttpMethod(method)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将指定的action处理器移除，并返回一个被移除的ActionHandler
     *
     * @param handler
     * @return
     */
    public ActionHandler remove(ActionHandler handler) {
        List<ActionHandler> list = new ArrayList<ActionHandler>(this.actionHandlers.length);
        for (ActionHandler actionHandler : actionHandlers) {
            if (actionHandler == handler) {
                continue;
            }
            list.add(actionHandler);
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        if (list.size() == 0) {
            return null;
        }

        return new GroupActionHandler(
                list.toArray(new ActionHandler[list.size()])
        );
    }


    @Override
    public String[] getPathVariableNames() {
        return actionHandlers[0].getPathVariableNames();
    }


}
