package org.zoomdev.zoom.web.router.impl;

import org.zoomdev.zoom.web.action.ActionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MockAction implements ActionHandler {
    private String name;
    private String[] pathVariableNames;

    public MockAction(String name, String... pathVariableNames) {
        this.name = name;
        this.pathVariableNames = pathVariableNames;
    }

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response) {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MockAction) {
            return ((MockAction) obj).getName().equals(getName());
        }
        return super.equals(obj);
    }

    @Override
    public boolean supportsHttpMethod(String method) {
        return true;
    }

    @Override
    public String getMapping() {
        return name;
    }

    @Override
    public String[] getMethods() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getPathVariableNames() {
        return pathVariableNames;
    }

}
