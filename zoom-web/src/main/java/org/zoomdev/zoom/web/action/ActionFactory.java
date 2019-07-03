package org.zoomdev.zoom.web.action;

import org.zoomdev.zoom.web.action.impl.ActionHolder;

/**
 * Action 工厂
 *
 * @author jzoom
 */
public interface ActionFactory {

    Action createAction(ActionHolder holder);
}
