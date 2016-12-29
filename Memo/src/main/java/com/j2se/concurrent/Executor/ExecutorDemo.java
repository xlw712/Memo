package com.j2se.concurrent.Executor;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * 类描述: 实现都对调度任务的方式和时间强加了某种限制。以下执行程序使任务提交与第二个执行程序保持连续，这说明了一个复合执行程序。
 * 
 * @Description: TODO
 * @author xLw
 * @since JDK 1.7
 * @date 2016年7月2日 下午11:29:42
 */
public class ExecutorDemo implements Executor {
	Queue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
	Executor executor;
	Runnable active;

	public ExecutorDemo(Executor executor) {
		this.executor = executor;
	}
	@Override
	public synchronized void execute(final Runnable r) {
		tasks.offer(new Runnable() {
			public void run() {
				r.run();
			}
		});
		if ((active = tasks.poll()) != null) {
			executor.execute(active);
		}
	}

	public void initTasks(Queue<Runnable> queue) {
		this.tasks = queue;
	}
}
