package com.jzoom.zoom.web.parameter.parser.impl;

import com.jzoom.zoom.common.Destroyable;
import com.jzoom.zoom.web.action.ActionContext;
import com.jzoom.zoom.web.parameter.ParameterParser;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

public class AutoParameterParser implements ParameterParser ,Destroyable {

    public abstract class AbstractParameterDecoder implements ParameterParser.HttpParameterDecoder {
        private ParameterParser parser;


        protected abstract boolean canParse(ActionContext context);

        @Override
        public Object[] decode(ActionContext context) throws Exception {
            if (canParse(context)) {
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

        protected abstract ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names);
    }

    public class RequestParameterDecoder extends AbstractParameterDecoder {


        @Override
        protected boolean canParse(ActionContext context) {
            return context.getPreParam() instanceof HttpServletRequest;
        }

        @Override
        protected ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names) {
            return formFactory.createParamParser(controllerClass, method, names);
        }

    }

    public class MapParameterDecoder extends AbstractParameterDecoder {
		private ParameterParser map;
		
		public MapParameterDecoder() {
			
		}


        @Override
        protected boolean canParse(ActionContext context) {
            return context.getPreParam() instanceof Map;
        }

        @Override
        protected ParameterParser createParamParser(Class<?> controllerClass, Method method, String[] names) {
            return mapFactory.createParamParser(controllerClass, method, names);
        }

	}
	
	private static final MapParameterParserFactory mapFactory = new MapParameterParserFactory();
	private static final FormParameterParserFactory formFactory = new FormParameterParserFactory();
	
	
	private Class<?> controllerClass;
	private Method method;
	private String[] names;
	
	private ParameterParser.HttpParameterDecoder[] decoders;

	public AutoParameterParser(Class<?> controllerClass, Method method, String[] names) {
		this.controllerClass = controllerClass;
		this.method = method;
		this.names = names;
		for (String name : names) {
			if(name==null) {
				throw new RuntimeException("name 不能为null " + controllerClass + ":" + method);
			}
		}
		this.decoders = new ParameterParser.HttpParameterDecoder[] {
				new RequestParameterDecoder(),
				new MapParameterDecoder(),
				
		};
	}
	
	
	
	@Override
	public void destroy() {
		this.method = null;
	}


	@Override
	public Object[] parse(ActionContext context) throws Exception {
		for (HttpParameterDecoder httpParameterDecoder : decoders) {
			Object[] result = httpParameterDecoder.decode(context);
			if(result!=null)return result;
		}
		throw new RuntimeException("不支持的解析参数类型");
	}


}
