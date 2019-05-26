package org.zoomdev.zoom.web.action;

import org.zoomdev.zoom.web.action.impl.ActionHolder;

import java.lang.reflect.Method;

/**
 * Action 工厂
 *
 * @author jzoom
 */
public interface ActionFactory {

    Action createAction(ActionHolder holder);
}
