package org.zoomdev.zoom.web.modules;

import org.zoomdev.zoom.common.ConfigurationConstants;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.caster.Caster;
import org.zoomdev.zoom.common.caster.ValueCaster;
import org.zoomdev.zoom.common.exceptions.ZoomException;
import org.zoomdev.zoom.common.res.ClassResolvers;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.DataObject;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.web.WebConfig;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionInterceptorFactory;
import org.zoomdev.zoom.web.action.impl.SimpleActionBuilder;
import org.zoomdev.zoom.web.action.impl.SimpleActionFactory;
import org.zoomdev.zoom.web.action.impl.SimpleActionInterceptorFactory;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;
import org.zoomdev.zoom.web.parameter.parser.impl.SimpleParameterParserFactory;
import org.zoomdev.zoom.web.parameter.pre.impl.SimplePreParameterParserManager;
import org.zoomdev.zoom.web.rendering.RenderingFactory;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;
import org.zoomdev.zoom.web.rendering.impl.*;
import org.zoomdev.zoom.web.router.Router;
import org.zoomdev.zoom.web.router.impl.ZoomRouter;

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
        Caster.register(HttpServletRequest.class, DataObject.class, new Request2DataObject());
        Caster.registerCastProvider(new Request2BeanProvider());
    }

    @IocBean
    public ParameterParserFactory getParameterParserFactory() {
        return new SimpleParameterParserFactory();
    }


    @IocBean
    public WebConfig getWebConfig(){
        return new WebConfig();
    }

    @IocBean
    public TemplateEngineManager getTemplateEngineManager(WebConfig webConfig){
        return new SimpleTemplateEngineManager(webConfig);
    }

    @IocBean
    public TemplateEngineRendering getTemplateEngineRendering(
            TemplateEngineManager manager,
            WebConfig config){
        return new TemplateEngineRendering(manager,config);
    }


    /**
     * 涉及拦截器都是系统级别
     * @return
     */
    @IocBean(order = IocBean.SYSTEM)
    public ActionInterceptorFactory getActionInterceptorFactory(){
        return new SimpleActionInterceptorFactory();
    }


    @IocBean(order = IocBean.SYSTEM)
    public ActionFactory getActionFactory(){
        return new SimpleActionFactory();
    }

    @IocBean
    public RenderingFactory getRenderingFactoryManager(
            TemplateEngineRendering rendering
    ){
        SimpleRenderingFactory factory = new SimpleRenderingFactory();
        JsonRendering jsonRendering = new JsonRendering();
        JsonErrorRendering jsonErrorRendering = new JsonErrorRendering();
        RedirectRendering redirectRendering = new RedirectRendering();
        ViewRendering viewRendering = new ViewRendering();

        factory.add(viewRendering);
        factory.add(jsonRendering);
        factory.add(redirectRendering);
        factory.add(rendering);

        factory.addError(viewRendering);
        factory.addError(jsonErrorRendering);
        factory.addError(redirectRendering);
        factory.addError(rendering);
        return factory;
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
                    if(field.getType().isArray()
                            || Iterable.class.isAssignableFrom(field.getType())){
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
    static class Request2DataObject implements ValueCaster {

        @Override
        public Object to(Object src) {
            HttpServletRequest request = (HttpServletRequest) src;
            return DataObject.wrap(getParameters(request));
        }
    }
    static class Request2Map implements ValueCaster {

        @Override
        public Object to(Object src) {
            HttpServletRequest request = (HttpServletRequest) src;
            return getParameters(request);
        }

    }


}
