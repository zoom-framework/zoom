package com.jzoom.zoom.common.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jzoom.zoom.common.Service;

/**
 * 
 * Execute one event at the same time,and skip more job where the event is executing.
 * @author jzoom
 *
 */
public abstract class SingleEventQueue implements JobQueue<Object>, Service {
	
	private static final Log log = LogFactory.getLog(SingleEventQueue.class);

	private ServiceThread thread;
	private Object event;

	public SingleEventQueue() {

	}

	@Override
	public void add(Object job) {
		
		synchronized (this) {
			if(this.event == null) {
				this.event = job;
				this.notify();
			}else {
				if(log.isDebugEnabled()) {
					log.debug("Job is doing, skip new job");
				}
			}
			
		}
	}

	@Override
	public void startup() throws Exception {
		this.event = null;
		this.thread = new ServiceThread() {

			@Override
			protected boolean repetitionRun() {
				Object event = null;
				synchronized (SingleEventQueue.this) {
					try {
						SingleEventQueue.this.wait();
						if(SingleEventQueue.this.event != null  ) {
							event = SingleEventQueue.this.event;
							
						}
					} catch (InterruptedException e) {
						SingleEventQueue.this.event = null;
						return false;
					}
				}
				if(event!=null) {
					try {
						doJob(event);
					}finally {
						SingleEventQueue.this.event = null;
					}
				
				}
				
				return true;
			}
		};
		this.thread.start();
	}

	protected abstract void doJob(Object event);

	@Override
	public void shutdown() {
		this.event = null;
		this.thread.stop();
	}

}
