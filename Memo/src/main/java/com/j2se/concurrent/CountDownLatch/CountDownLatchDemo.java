package com.j2se.concurrent.CountDownLatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 
 * 类描述： CountDownLatch，一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
 * CountDownLatch的一个非常典型的应用场景是：有一个任务想要往下执行，但必须要等到其他的任务执行完毕后才可以继续往下执行。
 * 假如我们这个想要继续往下执行的任务调用一个CountDownLatch对象的await()方法，
 * 其他的任务执行完自己的任务后调用同一个CountDownLatch对象上的countDown()方法，这个调用await()方法的任务将一直阻塞等待，
 * 直到这个CountDownLatch对象的计数值减到0为止。
 * 
 * @Description: TODO
 * @author xLw
 * @since JDK 1.7
 * @date 2016年6月29日 下午10:40:17
 */
public class CountDownLatchDemo {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		int tCount = 4;
		ExecutorService executor = Executors.newFixedThreadPool(tCount);
		//创建一个计数器长度为4的线程辅助类,当所有线程都执行完后,主线程进入就绪态
		CountDownLatch latch = new CountDownLatch(4);
		Worker worker1 = new Worker("work1", 1000, latch);
		Worker worker2 = new Worker("work2", 2000, latch);
		Worker worker3 = new Worker("work3", 3000, latch);
		Worker worker4 = new Worker("work4", 8000, latch);
		executor.execute(worker1);
		executor.execute(worker2);
		executor.execute(worker3);
		executor.execute(worker4);
		try {
			latch.await();
			latch.countDown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("work end off work");
		executor.shutdown();
	}

	/**
	 * 
	 * 类描述：工作者类,用来模拟工人工作
	 * 
	 * @Description: TODO
	 * @author xLw
	 * @since JDK 1.7
	 * @date 2016年6月29日 下午11:05:43
	 */
	static class Worker implements Runnable {
		String workname;
		long worktime;
		CountDownLatch latch;

		public Worker(String workname, long worktime, CountDownLatch latch) {
			super();
			this.workname = workname;
			this.worktime = worktime;
			this.latch = latch;
		}

		public void run() {
			System.out.println("Worker" + workname + " do work begin at" + sdf.format(new Date()));
			doWork();
			System.out.println("Worker" + workname + " do work complete at" + sdf.format(new Date()));
			latch.countDown();
			System.out.println(latch.getCount());
		}

		private void doWork() {
			try {
				Thread.sleep(worktime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
