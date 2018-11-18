package org.zoomdev.zoom.web.action;

public class ActionInterceptorAdapter implements ActionInterceptor {

    @Override
    public boolean preParse(ActionContext context) throws Exception {
        return true;
    }

    @Override
    public void parse(ActionContext context) throws Exception {

    }


    @Override
    public void complete(ActionContext context) throws Exception {

    }

    @Override
    public boolean whenError(ActionContext context) throws Exception {
        return true;
    }

    @Override
    public void whenResult(ActionContext context) throws Exception {

    }

}
