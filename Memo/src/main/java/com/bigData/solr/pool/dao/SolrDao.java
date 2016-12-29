package com.bigData.solr.pool.dao;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class SolrDao extends SolrServerPool {
	public SolrDao() {
		initialize();
	}
	@Override
	public SolrDocumentList queryDocumentList(SolrQuery query) throws Exception {
		SolrDocumentList resultList = null;
		CloudSolrServer cloudSolrServer = pool.borrowObject();
		QueryResponse response = cloudSolrServer.query(query, METHOD.POST);
		resultList = response.getResults();
		pool.returnObject(cloudSolrServer);
		return resultList;
	}
	@Override
	public SolrDocumentList queryDocumentListCollection(SolrQuery query,String collection)
			throws Exception {
		SolrDocumentList resultList = null;
		CloudSolrServer cloudSolrServer = pool.borrowObject();
		cloudSolrServer.setDefaultCollection(collection);//指定collection
		QueryResponse response = cloudSolrServer.query(query, METHOD.POST);
		resultList = response.getResults();
		pool.returnObject(cloudSolrServer);
		return resultList;
	}
	@Override
	public QueryResponse queryResponse(SolrQuery query) throws Exception {
		QueryResponse response=null;
		CloudSolrServer cloudSolrServer=pool.borrowObject();
		response = cloudSolrServer.query(query, METHOD.POST);
		pool.returnObject(cloudSolrServer);
		return response;
	}
	@Override
	public QueryResponse queryResponseCollection(SolrQuery query,String collection)
			throws Exception {
		QueryResponse response=null;
		CloudSolrServer cloudSolrServer = pool.borrowObject();
		cloudSolrServer.setDefaultCollection(collection);//指定collection
		response = cloudSolrServer.query(query, METHOD.POST);
		pool.returnObject(cloudSolrServer);
		return response;
	}
}
