package com.gwg.executorService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 创建并执行一个在给定初始延迟后首次启用的定期操作，后续操作具有给定的周期；也就是将在 initialDelay 后开始执行，
 * 然后在 initialDelay+period 后执行，接着在 initialDelay + 2 * period 后执行，依此类推。
 * 注意：给定逻辑的处理时间T可能会很长,当T > period的时候，处理周期应该是T
 *
 */
public class ScheduledExecutorServiceTest {
	//Executors.newSingleThreadScheduledExecutor创建一个使用单个worker线程的Excutor 
	public static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
	        "NSScheduledThread"));
	
	public static void main(String[] args) {
    	 System.out.println("服务启动时间"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		 scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

	            public void run() {
	            	System.out.println("start: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	            	System.out.println("i am gaoweigang!");
	            	try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            	System.out.println("end: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	            }
	        }, 5, 10, TimeUnit.SECONDS);
	}
}
