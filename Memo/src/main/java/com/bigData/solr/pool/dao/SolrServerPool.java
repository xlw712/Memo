package com.bigData.solr.pool.dao;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 * solrServer对象池
 * 版权所有：2016
 * 项目名称：qqDemo   
 *
 * 类描述：减少solr对象初始化次数
 * 类名称：com.cn.qq.index.Factory.SolrServerPool     
 * 创建人：xLw 
 * 创建时间：2016-3-13 下午4:50:36   
 * 修改人：
 * 修改时间：2016-3-13 下午4:50:36   
 * 修改备注：   
 * @version   V1.0
 */
public abstract class SolrServerPool implements CommonConstant {
	public static ObjectPool<CloudSolrServer> pool;
	public SolrServerPool() {
	}
	protected void initialize() {
		if (pool == null) {
			pool = new StackObjectPool<CloudSolrServer>(new PoolSolrFactory(),
					MAX_ACTIVE_POOL, INIT_IDLECAPACITY_POOL);
			for (int i = 0; i < INIT_IDLECAPACITY_POOL; i++) {
				try {
					pool.addObject();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class PoolSolrFactory implements PoolableObjectFactory {
		public void activateObject(Object obj) throws Exception {

		}

		public void destroyObject(Object obj) throws Exception {
			CloudSolrServer server = (CloudSolrServer) obj;
			server.shutdown();
		}

		public Object makeObject() throws Exception {
			CloudSolrServer cloudSolrServer = new CloudSolrServer(ZK_HOST);
			cloudSolrServer.setDefaultCollection(DEFAULT_COLLECTION);
			cloudSolrServer.setZkClientTimeout(ZK_CLIENT_TIMEOUT);
			cloudSolrServer.setZkConnectTimeout(ZK_CONNECTION_TIMEOUT);
			return cloudSolrServer;
		}

		public void passivateObject(Object obj) throws Exception {

		}

		public boolean validateObject(Object obj) {
			boolean flag = false;
			CloudSolrServer cloudSolrServer = (CloudSolrServer) obj;
			if (obj != null && cloudSolrServer.getDefaultCollection() != null) {
				flag = true;
//				cloudSolrServer.connect();
			}
			return flag;
		}
	}
	/**
	 * @Title: query
	 * @Description: 查询solr默认的collection
	 * @param query 构造好的query对象
	 * @return 返回solrDocumentList
	 * @throws Exception
	 * @author xLw
	 */
	public abstract SolrDocumentList queryDocumentList(SolrQuery query) throws Exception;

	/**
	 * @Title: querySpecifyCollection
	 * @Description: 查询指定collection
	 * @param query
	 *            构造好的query对象
	 * @param collection
	 *            指定collection名
	 * @return 返回solrDocumentList
	 * @throws Exception
	 * @author xLw
	 */
	public abstract SolrDocumentList queryDocumentListCollection(SolrQuery query,
			String collection) throws Exception;
	/**
	 * @Title: queryResponse
	 * @Description: 查询solr默认的collection
	 * @param query
	 * @return	返回QueryResponse
	 * @throws Exception
	 * @author xLw
	 */
	public abstract QueryResponse queryResponse(SolrQuery query) throws Exception;
	/**
	 * @Title: queryResponseCollection
	 * @Description: 查询指定collection
	 * @param query
	 * @return	返回QueryResponse
	 * @throws Exception
	 * @author xLw
	 */
	public abstract QueryResponse queryResponseCollection(SolrQuery query,String collection) throws Exception;
}
