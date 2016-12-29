package com.j2se.thread.join;

/**
 *          __     __       
 *         /  \~~~/  \    
 *   ,----(     ..    ) 
 *  /      \__     __/   
 * /|         (\  |(
 *^ \   /___\  /\ |   
 *   |__|   |__|-" 
 *
 * 类描述： 演示Join的用法
 * 当 a thread 调用Join方法的时候，MainThread 就被停止执行，直到 a thread 线程执行完毕.
 * 应用场景如在主线程中需要等待其他线程生成的数据.
 * @Description: TODO
 * @author Levin    
 * @since JDK 1.7
 * @date 2016年10月17日 下午1:52:50
 */
public class JoinDemo {
	public static void main(String[] args) {
		Thread t1 = new Thread() {
			public void run() {
				Thread t2 = new Thread() {
					@Override
					public void run() {
						while (true) {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("我先执行,t1需要等我执行完了才可以");
						}
					}
				};
				t2.start();
				try {
					t2.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("t2执行完了我可以开始啦");
				}
			};
		};
		t1.start();

	}
}
