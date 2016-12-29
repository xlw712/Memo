package com.j2se.concurrent.Callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 *          __     __       
 *         /  \~~~/  \    
 *   ,----(     ..    ) 
 *  /      \__     __/   
 * /|         (\  |(
 *^ \   /___\  /\ |   
 *   |__|   |__|-" 
 *
 * 类描述： 该类用来测试Callable和Future的使用
 * Callable可以当做是有返回值的Runnable
 * Future可以获取Executors.submit后线程返回的对象
 * @Description: TODO
 * @author Levin    
 * @since JDK 1.7
 * @date 2016年10月13日 下午5:32:41
 */
public class CallableDemo implements Callable<String> {
	public static void main(String[] args) {
		CallableDemo callableDemo = new CallableDemo();
		try {
			ExecutorService executors = Executors.newFixedThreadPool(10);
			Future<String> future = executors.submit(callableDemo);
			Thread.sleep(2000);
			//是否允许线程在运行时中断,true为允许,false为不允许
			future.cancel(true);
//			executors.shutdown();
			if (future.isCancelled()) {
				System.out.println("线程退出了");
			}else if (future.isDone()) {
				System.out.println("Done"+future.get());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String call() throws Exception {
		while (true) {
			Thread.sleep(1000);
			System.out.println("Hello World");
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
		}
		return "Hello World";
	}
}
