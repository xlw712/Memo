package com.j2se.concurrent.CompletionService;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *          __     __       
 *         /  \~~~/  \    
 *   ,----(     ..    ) 
 *  /      \__     __/   
 * /|         (\  |(
 *^ \   /___\  /\ |   
 *   |__|   |__|-" 
 *
 * 类描述： CompletionService将Executor和BlockingQueue的功能融合一起.
 * ExecutorCompletionsService实现了CompletionService并将计算部分委托给一个Executor.
 * CompletionService.take方法为阻塞IO,用来获取提交到Executor中的任务的结果
 * @Description: TODO
 * @author Levin    
 * @since JDK 1.7
 * @date 2016年10月17日 上午10:48:13
 */
public class CompletionServiceDemo implements Callable<String> {

	@Override
	public String call() throws Exception {
		int sleepcount = new Random().nextInt(10) * 1000;
		Thread.sleep(sleepcount);
		return "睡了好久大约：" + sleepcount;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
		int iCount = 10;// 提交的任务数
		int JConnt = 10;// 收集结果的数
		/*
		 * 如非必要,不要将收集结果的数改变,如果completionService.take();一直拿不到结果会一直阻塞,造成任务不关闭的现象
		 */
		for (int i = 0; i < iCount; i++) {
			completionService.submit(new CompletionServiceDemo());
		}
		for (int i = 0; i < JConnt; i++) {
			Future<String> resultFuture = completionService.take();
			try {
				System.out.println(resultFuture.get(1,TimeUnit.SECONDS));
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}
}
