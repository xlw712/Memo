package com.bigData.solr.pool.dao;


public interface CommonConstant{
	/**
	 * zookeeper地址
	 */
	public static final String ZK_HOST = "192.168.1.210:2181";
	/**
	 * 默认collection
	 */
	public static final String DEFAULT_COLLECTION = "collection1";
	/**
	 * zk所有连接超时时间
	 */
	public static final int ZK_CLIENT_TIMEOUT = 5000;
	/**
	 * zk客户端连接超时时间
	 */
	public static final int ZK_CONNECTION_TIMEOUT = 5000;
	/**
	 * 指明能从对象池中借出的对象的最大数目。如果这个值不是正数，表示没有限制。
	 */
	public static final int MAX_ACTIVE_POOL = 30;
	/**
	 * 指明对象池初始的参考大小
	 */
	public static final byte INIT_IDLECAPACITY_POOL = 10;

}
