package org.zoomdev.zoom.web.modules;

import org.zoomdev.zoom.common.ConfigurationConstants;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.caster.ValueCaster;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;
import org.zoomdev.zoom.web.parameter.parser.impl.SimpleParameterParserFactory;
import org.zoomdev.zoom.web.parameter.pre.impl.SimplePreParameterParserManager;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Module
public class WebModules {

    @Inject(config = ConfigurationConstants.SERVER_ENCODING)
    private String encoding;

    /**
     * 对caster进行配置，增加参数解析的部分
     */
    @Inject
    public void configCaster() {
        Caster.register(HttpServletRequest.class, Map.class, new Request2Map());
        Caster.registerCastProvider(new Request2BeanProvider());

    }

    @IocBean
    public ParameterParserFactory getParameterParserFactory() {
        return new SimpleParameterParserFactory();
    }


    @IocBean
    public PreParameterParserManager getPreParameterParserManager() {
        return new SimplePreParameterParserManager();
    }


    static class Request2BeanProvider implements Caster.CasterProvider {

        @Override
        public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
            if (!HttpServletRequest.class.isAssignableFrom(srcType)) {
                return null;
            }
            if (Classes.isSimple(toType)) {
                //转化简单类型应该是不行的
                return null;
            }
            //java开头的一律略过
            if (toType.getName().startsWith("java"))
                return null;


            return new Request2Bean(toType);
        }

    }

    static class Request2Bean implements ValueCaster {

        private Class<?> toType;

        public Request2Bean(Class<?> toType) {
            this.toType = toType;
        }

        @Override
        public Object to(Object src) {
            HttpServletRequest request = (HttpServletRequest) src;

            try {
                Object data = toType.newInstance();
                Field[] fields = CachedClasses.getFields(toType );
                for(Field field : fields){
                    Object value;
                    if(field.getType().isArray()){
                        value = request.getParameterValues(field.getName());
                    }else{
                        value = request.getParameter(field.getName());
                    }
                    if(value==null){
                        continue;
                    }
                    field.set(data,Caster.toType(value,field.getGenericType()));
                }
                return data;
            } catch (Exception e) {
               throw new ZoomException(e);
            }
        }

    }

    static class Request2Map implements ValueCaster {

        @Override
        public Object to(Object src) {
            HttpServletRequest request = (HttpServletRequest) src;
            return getParameters(request);
        }

        /**
         * 将request转成map
         *
         * @param request
         * @return
         */
        public static Map<String, Object> getParameters(HttpServletRequest request) {
            Map<String, String[]> params = request.getParameterMap();
            Map<String, Object> data = new HashMap<String, Object>();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                data.put(entry.getKey(), entry.getValue()[0]);
            }
            return data;
        }
    }


}
