package com.bigData.solr.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
/**
 * 
 * 版权所有：2016
 * 项目名称：BigDataDemo   
 * 类描述：SolrJ操作Solr索引工具类
 * 类名称：com.bigData.solr.dao.SolrDaoUtil     
 * 创建人：xLw 
 * 创建时间：2016-2-28 上午10:37:56   
 * 修改人：
 * 修改时间：2016-2-28 上午10:37:56   
 * 修改备注：   
 * @version   V1.0
 */
public class HttpSolrServerDaoUtil {
	private static HttpSolrClient server;
	private static final String SOLR_ID="id";
	/**
	 * 初始化server对象
	 */
	static {
		if (null == server) {
			// 读取配置文件，初始化server
			Properties prop = new Properties();
			InputStream in = HttpSolrServerDaoUtil.class
					.getResourceAsStream("/solr.properties");
			try {
				prop.load(in);
				server = new HttpSolrClient(prop.getProperty("baseUrl"));
				server.setConnectionTimeout(Integer.valueOf(prop
						.getProperty("connectionTimeout")));
				server.setDefaultMaxConnectionsPerHost(Integer.valueOf(prop
						.getProperty("defaultMaxConnectionsPerHost")));
				server.setMaxTotalConnections(Integer.valueOf(prop
						.getProperty("maxTotalConnections")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static QueryResponse Query(SolrQuery params){
		QueryResponse response=null;
		if (params!=null) {
			try {
				response=server.query(params);
			} catch (SolrServerException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response;
	}
	/**
	 * @Title: queryAllSolr
	 * @Description:查询全部索引
	 * @return
	 * @author xLw
	 */
	public static SolrDocumentList queryAllSolr() {
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("q", "*:*");
		params.set("rows",Integer.MAX_VALUE);
		QueryResponse response=null;
		try {
			response = server.query(params);
			// SolrDocumentList list=response.getResults();
			// Iterator iterator=list.iterator();
			// while (iterator.hasNext()) {
			// SolrDocument solrDocument=(SolrDocument) iterator.next();
			// solrDocument.getFieldValue("id");
			// System.out.println(solrDocument);
			// }
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.getResults();
	}
	/**
	 * 
	 * @Title: deleteDocument
	 * @Description: 删除单个Document
	 * @param document
	 * @throws SolrServerException
	 * @throws IOException
	 * @author xLw
	 */
	public static void deleteDocument(SolrDocument document) throws SolrServerException, IOException
	{
		if (null==document) {
			throw new SolrServerException("Document is Null");
		}
		server.deleteById(String.valueOf(document.getFieldValue(SOLR_ID)));
		server.commit(true, true);
		
	}
	/**
	 * @Title: deleteDocument
	 * @Description: 删除Document集合
	 * @param documentList
	 * @throws SolrServerException
	 * @throws IOException
	 * @author xLw
	 */
	public static void deleteDocument(SolrDocumentList documentList) throws SolrServerException, IOException
	{
		if (null==documentList) {
			throw new SolrServerException("DocumentList is Null");
		}
		Iterator iterator=documentList.iterator();
		while (iterator.hasNext()) {
			SolrDocument solrDocument=(SolrDocument) iterator.next();
			deleteDocument(solrDocument);
		}
	}
	public static void deleteDocumentAll()
	{
		try {
			server.deleteByQuery("*:*");
			server.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 添加指定Document到Solr
	 * @Title: addDocument
	 * @Description: 
	 * @param document
	 * @throws SolrServerException
	 * @throws IOException
	 * @author xLw
	 */
	public static void addDocument(SolrInputDocument document) throws SolrServerException, IOException{
		server.add(document);
		server.commit();
	}
	public static void appendField(){
		SolrQuery solrQuery=new SolrQuery();
		solrQuery.setQuery("name");
	}
	public static void main(String[] args) throws SolrServerException, IOException {
//		SolrQuery query=new SolrQuery("*:*").setFacet(true).addFacetField("name");
//		try {
//			QueryResponse response=server.query(query);
//			List  list1=response.getFacetFields();
//			SolrDocumentList list=response.getResults();
//			Iterator iterator=list.iterator();
//			while (iterator.hasNext()) {
//				System.out.println(iterator.next());
//			}
//		} catch (SolrServerException e) {
//			e.printStackTrace();
//		}
		CloudSolrClient server1=new CloudSolrClient("192.168.1.210:2181");
		server1.setDefaultCollection("collection2");
		server1.setZkClientTimeout(20000);
		SolrQuery query=new SolrQuery();
		query.setQuery("id:*");
		QueryResponse response=server1.query(query,METHOD.POST);
		System.out.println(response.getResults());
	}
}
