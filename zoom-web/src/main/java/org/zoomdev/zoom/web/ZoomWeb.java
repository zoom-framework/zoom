package org.zoomdev.zoom.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zoomdev.zoom.common.ConfigurationConstants;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.config.ConfigReader;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.OrFilter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.res.ClassResolvers;
import org.zoomdev.zoom.common.res.ResLoader;
import org.zoomdev.zoom.common.res.ResScanner;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.ioc.IocContainer;
import org.zoomdev.zoom.ioc.configuration.SimpleConfigBuilder;
import org.zoomdev.zoom.ioc.impl.ZoomIocContainer;
import org.zoomdev.zoom.web.action.ActionFactory;
import org.zoomdev.zoom.web.action.ActionHandler;
import org.zoomdev.zoom.web.action.impl.SimpleActionBuilder;
import org.zoomdev.zoom.web.router.Router;
import org.zoomdev.zoom.web.router.impl.BracesRouterParamRule;
import org.zoomdev.zoom.web.router.impl.ZoomRouter;
import org.zoomdev.zoom.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class ZoomWeb {
    private Router router;

    private IocContainer ioc;

    private ActionFactory actionFactory;

    private ResScanner scanner;

    private static Log log = LogFactory.getLog(ZoomWeb.class);
    long first = System.currentTimeMillis();


    public ZoomWeb() {
        this.ioc = new ZoomIocContainer();
        WebUtils.setIoc(ioc);
    }

    public void init() {

        printLogo();
        /// 加载整个项目的主配置,这个改成在Module加载
        ioc.getIocClassLoader().append(ConfigReader.class,ConfigReader.getDefault(),true,IocBean.SYSTEM);
        //loadApplicationConfig();
        // 扫描整个资源, .class .jar 其他配置文件等
        scanResources();
        createRouter();
        // 初始化router
        resolveClasses();


        printTime();


    }

    private void resolveClasses() {
        SimpleConfigBuilder configBuilder= new SimpleConfigBuilder(ioc);
        SimpleActionBuilder actionBuilder= new SimpleActionBuilder(
                ioc,
                router,
                null
        );

        ClassResolvers classResolvers = new ClassResolvers(configBuilder,actionBuilder);
        classResolvers.visit(scanner);

        WebUtils.setStartupSuccess();
    }

    private void createRouter() {
        router = new ZoomRouter();
        ioc.getIocClassLoader().append(Router.class,router,true,IocBean.SYSTEM);
        router = ioc.fetch(Router.class);
    }


    private void printLogo() {
        log.info("==============================Startup Zoom==============================");

        System.out.println("____  ___    ___\n" + "  /  / _ \\  / _ \\ |\\    /|\n"
                + " /  | (_) || (_) || \\  / |    \n" + "/___ \\___/  \\___/ |  \\/  |\n");
    }

    private void printTime() {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        long startTime = bean.getStartTime();
        long now = System.currentTimeMillis();
        log.info(String.format(
                "==============================Startup Zoom  in [%d] ms , JVM runing time [%d] ms==============================",
                now - first, now - startTime));
    }


    class JarFilter implements Filter<File> {

        private Filter<String> filter;

        public JarFilter(Filter<String> filter) {
            this.filter = filter;
        }

        @Override
        public boolean accept(File value) {
            return filter.accept(value.getName());
        }

    }

    @SuppressWarnings("unchecked")
    private void scanResources() {
        Filter<File> jarFilter = null;
        String jar = ConfigReader.getDefault().getString(ConfigurationConstants.SCAN_JAR);
        if (!StringUtils.isEmpty(jar)) {
            jarFilter = new OrFilter<File>(ResScanner.fastFilter,
                    new JarFilter(PatternFilterFactory.createFilter(jar)));
        } else {
            // 不扫描jar
            jarFilter = ResScanner.fastFilter;
        }

        long time = System.currentTimeMillis();
        try {
            scanner = new ResScanner();
            ioc.getIocClassLoader().append(ResScanner.class,scanner,true,IocBean.SYSTEM);
            scanner.scan(ZoomWeb.class.getClassLoader(), jarFilter);
        } catch (IOException e) {
            throw new RuntimeException("扫描解析文件出错", e);
        }
       // System.out.println("======Scan time :" + (System.currentTimeMillis()-time));

    }

    /**
     * 获取应用程序全局配置
     *
     * @return
     */
    private void loadApplicationConfig() {
        // 加载全局配置

        File file = ResLoader.getResourceAsFile("application.properties");
        if (file == null) {
            file = ResLoader.getResourceAsFile("application.json");
        }

        if (file == null) {
            // 目前这个版本支持两种主配置，properties/yml
            //throw new RuntimeException("启动失败，请确认application.properties或application.json存在");
            return;
        }
        ConfigReader.getDefault().load(file);

    }

    public void destroy() {

        if (ioc != null) {
            ioc.destroy();
            ioc = null;
        }

        router = null;

        WebUtils.setIoc(null);

        CachedClasses.clear();
        PatternFilterFactory.clear();
        scanner.destroy();
    }

    public boolean handle(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 路由

        ActionHandler action = router.match(request);
        if (action == null) {
            return false;
        }

        action.handle(request, response);
        //throw new StatusException.NotAllowedHttpMethodException(request.getMethod());

        return true;
    }

    public IocContainer getIoc() {
        return ioc;
    }

}
