package org.zoomdev.zoom.web.router.impl;

import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.web.action.ActionHandler;
import org.zoomdev.zoom.web.action.impl.GroupActionHandler;
import org.zoomdev.zoom.web.router.Router;

import java.util.Map;

class ActionHandlerUtils {
    static class GroupRemove implements Router.RemoveToken {

        private Router.RemoveToken thisToken;
        private Router.RemoveToken nextToken;


        public GroupRemove(Router.RemoveToken thisToken, Router.RemoveToken nextToken) {
            this.thisToken = thisToken;
            this.nextToken = nextToken;
        }

        @Override
        public void remove() {
            nextToken.remove();
            thisToken.remove();
        }
    }

    static class RemoveRouter implements Router.RemoveToken {

        private String key;
        private ActionHandler actionHandler;
        private Map<String, ActionHandler> map;

        public RemoveRouter(Map<String, ActionHandler> map, String key, ActionHandler actionHandler) {
            this.key = key;
            this.map = map;
            this.actionHandler = actionHandler;
        }

        @Override
        public void remove() {
            synchronized (map) {
                removeFromMap(map, key, actionHandler);
            }
        }
    }


    public static void removeFromMap(Map<String, ActionHandler> map,
                                     String key,
                                     ActionHandler target) {
        ActionHandler src = map.get(key);
        if (src != null) {
            if (src instanceof GroupActionHandler) {
                ActionHandler actionHandler = ((GroupActionHandler) src).remove(target);
                if (actionHandler != null) {
                    map.put(key, actionHandler);
                }
                Classes.destroy(target);
            } else {
                if (src == target) {
                    map.remove(key);
                    Classes.destroy(target);
                }
            }
        }
    }

    public static ActionHandler removeAction(ActionHandler src, ActionHandler target) {
        if (src instanceof GroupActionHandler) {
            ActionHandler actionHandler = ((GroupActionHandler) src).remove(target);
            return actionHandler;
        } else {
            if (src == target) {
                return null;
            }
            return src;
        }

    }

}
