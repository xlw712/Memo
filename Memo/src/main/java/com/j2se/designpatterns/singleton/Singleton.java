package com.j2se.designpatterns.singleton;

public class Singleton {
	private Singleton() {
	}

	private volatile static Singleton singleton;

	public static Singleton getSingleton() {
		synchronized (Singleton.class) {
			if (singleton == null) {
				singleton = new Singleton();
			}
		}
		return singleton;
	}
}
