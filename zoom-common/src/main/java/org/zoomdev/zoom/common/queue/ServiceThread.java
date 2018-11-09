package org.zoomdev.zoom.common.queue;



/**
 * 服务线程
 * @author Randy
 *
 */
public abstract class ServiceThread implements Runnable
{
	
	/**
	 * 是否正在运行
	 */
	protected volatile boolean isRunning;
	
	/**
	 * 线程
	 */
	protected Thread thread;
	
	public ServiceThread(){
		this.thread = new Thread(this);
		this.thread.setDaemon(true);
	}
	
	public void setName(String name){
		this.thread.setName(name);
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	/**
	 * 启动
	 */
	public synchronized void start()
	{
		if(isRunning)return;
		isRunning = true;
		thread.start();
	}
	
	
	/**
	 * 停止
	 */
	public synchronized void stop()
	{
		if(!isRunning)return;
		isRunning = false;
		thread.interrupt();
		while(thread.isAlive()){
			try{Thread.sleep(1);}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	protected abstract boolean repetitionRun();
	
	@Override
	public void run()
	{
		while(isRunning)
		{
			try{
				if(!repetitionRun())break;
			}catch(Exception e)
			{
				//当调用线程的thread.interrupt();方法时候
				e.printStackTrace();
			}catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
