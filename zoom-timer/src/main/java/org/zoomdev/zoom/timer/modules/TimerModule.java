package org.zoomdev.zoom.timer.modules;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.annotations.Inject;
import org.zoomdev.zoom.common.annotations.IocBean;
import org.zoomdev.zoom.common.annotations.Module;
import org.zoomdev.zoom.common.el.ElParser;
import org.zoomdev.zoom.ioc.IocMethodHandler;
import org.zoomdev.zoom.ioc.IocMethodProxy;
import org.zoomdev.zoom.ioc.IocMethodVisitor;
import org.zoomdev.zoom.ioc.IocObject;
import org.zoomdev.zoom.timer.TimerJob;
import org.zoomdev.zoom.timer.TimerService;
import org.zoomdev.zoom.timer.annotation.Timer;
import org.zoomdev.zoom.timer.annotation.TimerEnable;
import org.zoomdev.zoom.timer.impl.QuartzTimerService;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

@Module(TimerEnable.class)
public class TimerModule implements Destroyable {


	@IocBean
	public TimerService getTimerService() {
		return new QuartzTimerService();
	}


    private String getCron(Timer timer) {
        if (timer.every() != Timer.Every.None) {
            assert (timer.everyValue() > 0);
            if (timer.every() == Timer.Every.Second) {
                return String.format("0/%d * * * * ?", timer.everyValue());
            }
            if (timer.every() == Timer.Every.Minute) {
                return String.format("0 0/%d * * * ?", timer.everyValue());
            }
            if (timer.every() == Timer.Every.Hour) {
                return String.format("0 0 0/%d * * ?", timer.everyValue());
            }
        }
        return timer.value();
    }

    @Inject
    public void inject(IocMethodVisitor visitor, final TimerService timerService) {
        visitor.add(new IocMethodHandler() {
            @Override
            public void visit(IocObject target, Method method, IocMethodProxy proxy) {
                Timer timer = method.getAnnotation(Timer.class);

                StringBuilder sb = new StringBuilder(target.getIocClass().getKey().getType().getName())
                        .append("#")
                        .append(method.getName());
                String cron = ElParser.parseConfigValue(getCron(timer));
                if (StringUtils.isEmpty(cron))
                    return;
                timerService.startTimer(sb.toString(), IocTimerJob.class, new TimerData(
                        target,
                        proxy
                ), cron);

            }

            @Override
            public boolean accept(Method method) {
                return method.isAnnotationPresent(Timer.class);
            }
        });
    }


    class TimerData {
        IocMethodProxy injector;
        IocObject target;


        public TimerData(IocObject target, IocMethodProxy injector) {
            this.injector = injector;
            this.target = target;
        }
    }

    public static class IocTimerJob implements TimerJob<TimerData> {


        public IocTimerJob() {

        }

        @Override
        public void execute(TimerData data) {
            data.injector.invoke(data.target);
        }
    }




	@Override
	public void destroy() {
		
	}


	
}
