package org.zoomdev.zoom.web.modules;

import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.caster.ValueCaster;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.web.utils.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Module
public class WebModules {


    /**
     * 对caster进行配置，增加参数解析的部分
     */
    @Inject
    public void configCaster() {
        Caster.register(HttpServletRequest.class, Map.class, new Request2Map());
        Caster.registerCastProvider(new Request2BeanProvider());

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
            return Caster.to(RequestUtils.getParameters(request), toType);
        }

    }

    static class Request2Map implements ValueCaster {

        @Override
        public Object to(Object src) {
            HttpServletRequest request = (HttpServletRequest) src;
            return RequestUtils.getParameters(request);
        }

    }


}
