package com.j2se.concurrent.Futrue;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 *          __     __       
 *         /  \~~~/  \    
 *   ,----(     ..    ) 
 *  /      \__     __/   
 * /|         (\  |(
 *^ \   /___\  /\ |   
 *   |__|   |__|-" 
 *
 * 类描述： 演示Futrue的使用
 * 应用案例:有时候,如果某个任务无法再指定时间内完成,那么僵不再需要它的结果
 * @Description: TODO
 * @author Levin    
 * @since JDK 1.7
 * @date 2016年10月18日 下午1:56:20
 */
public class FutrueDemo implements Callable<String> {

	@Override
	public String call() throws Exception {
		// 模拟任务占用时间
		int random = new Random().nextInt(10);
		try {
			Thread.sleep(random * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Thread.currentThread().getName() + "线程执行完了";
	}

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 10; i++) {
			Future<String> future = executorService.submit(new FutrueDemo());
			String result = null;
			try {
				result = future.get(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
			}finally {
				//如果任务已经结束,那么执行取消操作也不会带来任何影响
				//如果任务正在运行，那么将被中断
				future.cancel(true);
			}
			System.out.println(result);
		}
		executorService.shutdown();
	}
}
