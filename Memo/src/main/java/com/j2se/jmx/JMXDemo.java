package com.j2se.jmx;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class JMXDemo {
	/**
	 * 类描述：Jmx注册对象所需要实现的接口
	 * 
	 * 接口名称必须以MBean结尾否则会抛出下面异常
	 * 
	 * MBean class com.test.all.Test$Counter does not implement DynamicMBean,
	 * and neither follows the Standard MBean conventions
	 * 
	 * @Description:
	 * @author xLw
	 * @since JDK 1.7
	 * @date 2016年5月26日 上午11:22:13
	 */
	public interface CounterMBean {
		/*
		 * 以get或者set为前缀命名的带有返回值的方法,在Jmx中会以属性方式显示
		 */
		public int getCounter();

		/*
		 * 没有返回值的方法,在Jmx中会以操作方式显示
		 */
		public void plusCounter();

		public void lessCounter();

		public void stop();

		public void start();

	}

	/**
	 * 类描述：Jmx中注册的对象,需要实现以MBean命名结尾的接口
	 * 
	 * @Description: TODO
	 * @author xLw
	 * @since JDK 1.7
	 * @date 2016年5月26日 上午11:25:50
	 */
	public static class Counter implements CounterMBean {
		// 原子性计数器
		public AtomicInteger atmoicCount = new AtomicInteger();

		public int getCounter() {

			return atmoicCount.get();
		}

		public void plusCounter() {
			atmoicCount.incrementAndGet();
		}

		public void lessCounter() {
			atmoicCount.decrementAndGet();

		}

		public void stop() {
			// TODO Auto-generated method stub

		}

		public void start() {
			// TODO Auto-generated method stub

		}

		/*
		 * 只有在接口中定义的方法才会被Jmx解释
		 */
		public void status() {

		}
	}

	public static void main(String[] args) throws MalformedObjectNameException, InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException {
		// MBeanServer对象是在代理端进行 MBean 操作的接口。它包含创建、注册和删除
		// MBean所需的方法，以及用于已注册MBean的存取方法。
		// 用户代码通常不实现此接口。相反，应该使用 MBeanServerFactory 类中的某个方法获得实现此接口的对象。
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		// 表示 MBean 的对象名，或者能够与多个 MBean 名称相匹配的模式。此类的实例是不可变的。
		ObjectName name = new ObjectName("xlw.lang:type=org.test.management,name=xlw");
		Counter userMBean = new Counter();
		// 注册对象到JMx中
		mBeanServer.registerMBean(userMBean, name);
		while (true) {

		}
	}
}
