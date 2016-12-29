package com.j2se.concurrent.CyclicBarrier;

import java.util.concurrent.CyclicBarrier;

/**
 * 
 * 类描述：
 * 字面意思回环栅栏，通过它可以实现让一组线程等待至某个状态之后再全部同时执行。叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被重用
 * 。我们暂且把这个状态就叫做barrier，当调用await()方法之后，线程就处于barrier了。
 * 
 * @Description: TODO
 * @author xLw
 * @since JDK 1.7
 * @date 2016年6月30日 下午11:15:41
 */
public class CyclicBarrierDemo {
	public static void main(String[] args) {
		//创建一个屏障点阀值为5的同步辅助类,当调用await方法达到5次后线程时间可执行await方法之后的程序
		CyclicBarrier barrier = new CyclicBarrier(5);
		//启动4个线程分别去执行任务,最后一个任务时间设置为相对于之前任务较长的执行时间。
		new Writer(barrier, 1000).start();
		new Writer(barrier, 1000).start();
		new Writer(barrier, 1000).start();
		new Writer(barrier, 1000).start();

	}

	static class Writer extends Thread {
		private CyclicBarrier cyclicBarrier;
		long sleeptime;

		public Writer(CyclicBarrier cyclicBarrier, long sleeptime) {
			this.cyclicBarrier = cyclicBarrier;
			this.sleeptime = sleeptime;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(sleeptime); // 以睡眠来模拟任务时间
				System.out.println("线程" + Thread.currentThread().getName() + "执行中");
				cyclicBarrier.await();
				System.out.println(Thread.currentThread().getName()+"完毕了");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
