package org.zoomdev.zoom.web.parameter.parser.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.web.action.ActionContext;
import org.zoomdev.zoom.web.parameter.ParameterParser;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

public class SimpleParameterParser implements ParameterParser, Destroyable {

    public abstract class AbstractParameterAdapterFactory implements ParameterAdapterFactory {
        private ParameterParser parser;
        private ParameterParserFactory factory;

        public AbstractParameterAdapterFactory(ParameterParserFactory factory){
            this.factory = factory;
        }

        protected abstract boolean shouldParse(ActionContext context);

        @Override
        public Object[] decode(ActionContext context) throws Exception {
            if (shouldParse(context)) {
                if (parser == null) {
                    synchronized (this) {
                        if (parser == null) {
                            parser = createParamParser(controllerClass, method, names);
                        }
                    }
                }
                return parser.parse(context);
            }
            return null;
        }

        protected ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names) {
            return factory.createParamParser(controllerClass, method, names);
        }
    }

    public class RequestParameterAdapterFactory extends AbstractParameterAdapterFactory {

        public RequestParameterAdapterFactory(ParameterParserFactory factory) {
            super(factory);
        }

        @Override
        protected boolean shouldParse(ActionContext context) {
            return context.getPreParam() instanceof HttpServletRequest;
        }


    }

    public class MapParameterAdapterFactory extends AbstractParameterAdapterFactory {

        public MapParameterAdapterFactory(ParameterParserFactory factory) {
            super(factory);
        }


        @Override
        protected boolean shouldParse(ActionContext context) {
            return context.getPreParam() instanceof Map;
        }


    }

    private static final MapParameterParserFactory mapFactory = new MapParameterParserFactory();
    private static final FormParameterParserFactory formFactory = new FormParameterParserFactory();


    private Class<?> controllerClass;
    private Method method;
    private String[] names;

    private ParameterAdapterFactory[] decoders;

    public SimpleParameterParser(
            Class<?> controllerClass,
            Method method,
            String[] names
    ) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.names = names;
        this.decoders = new ParameterAdapterFactory[]{
                new RequestParameterAdapterFactory(formFactory),
                new MapParameterAdapterFactory(mapFactory),
        };
    }


    @Override
    public void destroy() {
        this.method = null;
    }


    @Override
    public Object[] parse(ActionContext context) throws Exception {
        for (ParameterAdapterFactory parameterAdapterFactory : decoders) {
            Object[] result = parameterAdapterFactory.decode(context);
            if (result != null)
                return result;
        }
        throw new RuntimeException("不支持的解析参数类型");
    }




}
