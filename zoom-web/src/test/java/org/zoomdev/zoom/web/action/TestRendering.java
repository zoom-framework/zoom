package org.zoomdev.zoom.web.action;

import junit.framework.TestCase;
import org.zoomdev.zoom.http.utils.CachedClasses;
import org.zoomdev.zoom.web.WebConfig;
import org.zoomdev.zoom.web.modules.WebModules;
import org.zoomdev.zoom.web.parameter.ParameterParserFactory;
import org.zoomdev.zoom.web.parameter.PreParameterParserManager;
import org.zoomdev.zoom.web.rendering.RenderingFactory;
import org.zoomdev.zoom.web.rendering.TemplateEngineManager;
import org.zoomdev.zoom.web.rendering.impl.TemplateEngineRendering;

public class TestRendering extends TestCase {

    public static class TestController{

        public void test(){

        }

    }


    public void test(){

        WebModules modules = new WebModules();
        modules.configCaster();

        ParameterParserFactory parameterParserFactory = modules.getParameterParserFactory();
        PreParameterParserManager preParameterParserManager = modules.getPreParameterParserManager();
        WebConfig config = modules.getWebConfig();

        TemplateEngineManager templateEngineManager = modules.getTemplateEngineManager(config);
        TemplateEngineRendering templateEngineRendering = modules.getTemplateEngineRendering(templateEngineManager,config);
        RenderingFactory renderingFactoryManager =  modules.getRenderingFactoryManager(templateEngineRendering);

        CachedClasses.getPublicMethods(TestController.class);
        //renderingFactoryManager.add();
    }
}
