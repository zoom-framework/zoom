package org.zoomdev.zoom.timer.impl;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.utils.MapUtils;
import org.zoomdev.zoom.timer.TimerJob;
import org.zoomdev.zoom.timer.TimerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class QuartzTimerService implements TimerService,Destroyable {
	SchedulerFactory schedulerFactory;
	Scheduler scheduler;
	Map<String, JobCreateInfo> map = new ConcurrentHashMap<String, JobCreateInfo>();
	private static final Log log = LogFactory.getLog(QuartzTimerService.class);

	public QuartzTimerService() {
		schedulerFactory = new StdSchedulerFactory();

		try {
			scheduler = schedulerFactory.getScheduler();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}

		if (scheduler == null) {
			throw new RuntimeException("scheduler is null");
		}
	}

	@Override
	public void stopAll() {
		Set<String> set = new HashSet<String>();
		set.addAll(map.keySet());
		
		for (String jobName : set) {
			stopTimer(jobName);
		}
	}


	@Override
	public void destroy() {
		stopAll();
		if (map != null) {
			map.clear();
			map = null;
		}
		if (scheduler != null) {
			try {
				scheduler.shutdown();
			} catch (SchedulerException e) {
				log.error("关闭定时任务发生异常",e);
			}
		}
	}

	private static class JobCreateInfo implements Destroyable{
		public JobCreateInfo(Trigger trigger,JobDetail detail,String cron,String name){
			this.trigger = trigger;
			this.detail = detail;
			this.cron = cron;
			this.name = name;
		}
		
		Trigger trigger;
		JobDetail detail;
		String name;
		String cron;

		@Override
		public void destroy() {
			trigger = null;
			detail = null;
			
		}
	
	
	}
	
	public static class SimpleTimerJob implements Job{

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			JobDataMap map = context.getJobDetail().getJobDataMap();
			Object data = map.get(JOB_DATA);
			Class<? extends TimerJob> clazz = (Class<? extends TimerJob>) map.get(JOB_CLASS);
			try {
				TimerJob job = clazz.newInstance();
				job.execute(data);
			}catch (Exception e) {
				throw new JobExecutionException(e);
			}
		}
	}
	
	public static final String JOB_DATA = "job_data";
	public static final String JOB_CLASS = "job_class";
	
	
	@Override
	public <T> void startTimer(String jobName, Class<? extends TimerJob<T>> jobClass, T data, String cron) {
		if(map.containsKey(jobName)){
			throw new RuntimeException(String.format("Job with name %s is already exists!", jobName));
		}
		JobBuilder jobBuilder = JobBuilder.newJob(SimpleTimerJob.class).withIdentity("jobName", jobName);
		Map<String, Object> jobData = 	MapUtils.asMap(JOB_DATA,data,JOB_CLASS,jobClass);
		jobBuilder.setJobData(JobDataMapSupport.newJobDataMap(jobData));
		JobDetail job = jobBuilder.build();
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", jobName)
				.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
		try {
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		}catch (Exception e) {
			throw new RuntimeException("创建定时任务失败",e);
		}
		
		map.put(jobName, new JobCreateInfo(trigger,job ,cron,jobName));
	}

	@Override
	public void stopTimer(String jobName) {
		synchronized (map) {
			if (!map.containsKey(jobName)) {
				return;
			}
			TriggerKey key = map.get(jobName).trigger.getKey();
			try {
				scheduler.unscheduleJob(key);
                log.info("关闭任务成功" + key.getName());
			} catch (SchedulerException e) {
				log.error("unscheduleJob异常",e);
			}
			JobCreateInfo info = map.remove(jobName);
			info.destroy();
		}
	}



	
	
}
