package org.zoomdev.zoom.web.router;

import org.zoomdev.zoom.web.action.ActionHandler;

import javax.servlet.http.HttpServletRequest;

public interface Router {

    /**
     * 用于在将来移除这个路由
     */
    interface RemoveToken {
        void remove();
    }

    /**
     * 注册一个路由
     *
     * @param key
     * @param action
     * @return 调用destroy，将这次注册的处理器删除掉。并销毁对象
     */
    RemoveToken register(String key, ActionHandler action);

    ActionHandler match(HttpServletRequest request);

    /**
     * 获取所有的
     *
     * @return
     */
    Iterable<ActionHandler> getActionHandlers();
}
