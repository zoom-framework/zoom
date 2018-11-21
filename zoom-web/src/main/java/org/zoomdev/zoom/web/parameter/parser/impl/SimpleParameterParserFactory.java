package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.web.parameter.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SimpleParameterParserFactory implements ParameterParserFactory, Destroyable {


    private List<ParameterAdapterFactory> factories = new ArrayList<ParameterAdapterFactory>();


    public SimpleParameterParserFactory() {
        factories.add(new MapParameterAdapterFactory());
        factories.add(new FormParameterAdapterFactory());
    }

    @Override
    public void add(ParameterAdapterMaker maker) {

        for (ParameterAdapterFactory factory : factories) {
            factory.addAdapterMaker(maker);
        }

    }

    private ParameterParserContainer[] createContainers() {
        ParameterParserContainer[] proxies = new ParameterParserContainer[factories.size()];
        int index = 0;
        for (ParameterAdapterFactory factory : factories) {
            ParameterParserContainer proxy = new ParameterParserContainerImpl(factory);
            proxies[index++] = proxy;
        }

        return proxies;

    }

    @Override
    public ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names) {
        if (names.length == 0) {
            return EmptyParameterParser.DEFAULT;
        }
        return new SimpleParameterParser(
                createContainers()
        );
    }


    @Override
    public void destroy() {

    }

}
