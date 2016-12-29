package com.bigData.zookeeper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ZookeeperDao {
	private static final int SESSION_TIMEOUT = 30000;
	private static Logger logger = Logger.getLogger(ZookeeperDao.class);
	private ZooKeeper zk;
	Watcher watcher = new Watcher() {
		public void process(org.apache.zookeeper.WatchedEvent event) {
			System.out.println("process" + event.getType());
		}
	};

	/**
	 * 连接zookeeper
	 * 
	 * @Title: createZKInstance
	 * @Description:
	 * @throws IOException
	 * @author xLw
	 */
	@Before
	public  void connect() throws IOException {
		zk = new ZooKeeper("192.168.1.210:2181", SESSION_TIMEOUT, watcher);
	}

	/**
	 * 关闭zookeeper连接
	 * 
	 * @Title: close
	 * @Description:
	 * @author xLw
	 */
	@After
	public   void close() {
		try {
			if (zk != null) {
				zk.close();
			}
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
	}

	/**
	 * 创建一个znode
	 * 
	 * @Title: createZkNode
	 * @Description: 1.Ids权限常量 OPEN_ACL_UNSAFE：完全开放。
	 *               事实上这里是采用了world验证模式，由于每个zk连接都有world验证模式，所以znode在设置了
	 *               OPEN_ACL_UNSAFE 时，是对所有的连接开放。 CREATOR_ALL_ACL
	 *               ：给创建该znode连接所有权限。 事实上这里是采用了auth验证模式，使用sessionID做验证。所以设置了
	 *               CREATOR_ALL_ACL 时，创建该znode的连接可以对该znode做任何修改。
	 *               READ_ACL_UNSAFE ：所有的客户端都可读。
	 *               事实上这里是采用了world验证模式，由于每个zk连接都有world验证模式
	 *               ，所以znode在设置了READ_ACL_UNSAFE时，所有的连接都可以读该znode。 2.CreateMode
	 *               创建的节点类型 PERSISTENT：持久化，这个目录节点存储的数据不会丢失
	 *               PERSISTENT_SEQUENTIAL：顺序自动编号的目录节点，这种目录节点会根据当前已近存在的节点数自动加
	 *               1，然后返回给客户端已经成功创建的目录节点名；
	 *               EPHEMERAL：临时目录节点，一旦创建这个节点的客户端与服务器端口也就是
	 *               session过期超时，这种节点会被自动删除 EPHEMERAL_SEQUENTIAL：临时自动编号节点
	 * @author xLw
	 */
	@Test
	public void createZkNode() {
		String result = null;
		try {
			result = zk.create("/zkTest", "你好世界".getBytes(),
					Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		logger.info("create reslut :{}");
	}

	/**
	 * 删除znode
	 * 
	 * @Title: removeZkNode
	 * @Description:
	 * @author xLw
	 */
	@Test
	public void removeZkNode() {
		try {
			if (zk.exists("/zkTest", true) != null) {
				zk.delete("/zkTest", -1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 获取znode数据
	 * 
	 * @Title: getZkNodeData
	 * @Description:
	 * @author xLw
	 */
	@Test
	public void getZkNodeData() {
		String result = null;
		try {
			byte[] bytes = zk.getData("/zkTest", null, null);
			result = new String(bytes, "utf-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 获取znode数据并添加Watch
	 * 
	 * @Title: getZkNodeDataWatch
	 * @Description:
	 * @author xLw
	 */
	@Test
	public void getZkNodeDataWatch() {
		String result = null;
		// 获取数据并添加Watch
		try {
			byte[] bytes = zk.getData("/zkTest", new Watcher() {
				public void process(WatchedEvent event) {
					logger.info(event.getType());
				}
			}, null);
			result = new String(bytes, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("getdata result:{}" + result);
		// 出发watch NodeDataChanged
		try {
			zk.setData("/zkTest", "再见世界".getBytes(), -1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 判断znode是否存在
	 * 
	 * @Title: ZkNodeExists
	 * @Description:
	 * @author xLw
	 */
	@Test
	public void ZkNodeExists() {
		Stat stat = null;
		try {
			zk.exists("/zkTest", false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		Assert.assertNotNull(stat);
		logger.info("exists result:{}" + stat.getCzxid());
	}
	/**
	 * 设置对应znode下的数据,-1表示匹配所有版本
	 * @Title: setZkNode
	 * @Description:
	 * @author xLw
	 */
	@Test
	public void setZkNode() {
		Stat stat = null;
		try {
			stat = zk.setData("/zkTest", "testSetData".getBytes(), -1);
		} catch (Exception e) {
			logger.error(e.getMessage());
			Assert.fail();
		}
		Assert.assertNotNull(stat);
		logger.info("exists result : {}" + stat.getVersion());
	}

	public static void main(String[] args) {

	}
}
