package com.jzoom.zoom.web.modules;

import com.jzoom.zoom.caster.Caster;
import com.jzoom.zoom.caster.ValueCaster;
import com.jzoom.zoom.common.annotations.Inject;
import com.jzoom.zoom.common.annotations.Module;
import com.jzoom.zoom.common.utils.Classes;
import com.jzoom.zoom.web.utils.RequestUtils;
import org.apache.commons.beanutils.BeanUtils;

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
        Caster.registerCastProvider(new Map2BeanProvider());
        //	Caster.registerCastProvider(new Map2BeanProvider());

    }
    private static class Map2Bean implements ValueCaster {
        private Class<?> toType;
        public Map2Bean(Class<?> toType) {
            this.toType = toType;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public Object to(Object src) {
            Map data = (Map)src;
            try {
                Object result = toType.newInstance();
                BeanUtils.populate(result, data);
                return result;
            }catch (Exception e) {
                throw new Caster.CasterException(e);
            }

        }

    }
    static class Request2BeanProvider implements Caster.CasterProvider {

        @Override
        public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
            if(!srcType.isAssignableFrom(HttpServletRequest.class)) {
                return null;
            }
            return null;
        }

    }

    static class Map2BeanProvider implements Caster.CasterProvider {

        @Override
        public ValueCaster getCaster(Class<?> srcType, Class<?> toType) {
            if(Map.class.isAssignableFrom(srcType)) {
                if(Classes.isSimple(toType)) {
                    //转化简单类型应该是不行的
                    return null;
                }
                //java开头的一律略过
                if(toType.getName().startsWith("java"))return null;

                return new Map2Bean(toType);
            }

            return null;

        }

    }

    static class Request2Map implements ValueCaster{

        @Override
        public Object to(Object src) {
            HttpServletRequest request = (HttpServletRequest)src;
            return RequestUtils.getParameters( request );
        }

    }


}
