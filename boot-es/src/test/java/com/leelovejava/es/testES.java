package com.leelovejava.es;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class testES {
	
	Client client = null;
	
	@Before
	public void conn() throws Exception{
		
		Settings settings = Settings.settingsBuilder()
		        .put("cluster.name", "bjsxt-es").build();
		client = TransportClient.builder().settings(settings).build()
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node01"), 9300))
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node03"), 9300))
		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("node02"), 9300));
	}
	
	@After
	public void close(){
		client.close();
	}
	
	
	@Test
	public void test01(){
		
		IndicesExistsResponse resp = client.admin().indices().prepareExists("java_test").execute().actionGet();
		
		if(resp.isExists()){
			client.admin().indices().prepareDelete("java_test").execute();
		}
		Map<String, Object> sets = new HashMap<String,Object>();
		sets.put("number_of_replicas", "2");
		
		client.admin().indices().prepareCreate("java_test").setSettings(sets).execute();
	}
	
	
	@Test
	public void test02(){
		Map<String, Object> data = new HashMap<String,Object>();
		data.put("content", "hbase is hadoop");
		data.put("path", "/books/hive");
		data.put("len", 2222);
		IndexResponse resp = client.prepareIndex("java_test", "god").setSource(data).execute().actionGet();
		
		System.out.println(resp.getId());
		
	}
	
	
	@Test
	public void test03(){
		
		QueryBuilder  q  = new MatchQueryBuilder("content", "hadoop");
		
		SearchResponse resp = client.prepareSearch("java_test")
			.setTypes("god")
			.setQuery(q)
			
			
			.addHighlightedField("content")
			.setHighlighterPreTags("<font color=red>")
			.setHighlighterPostTags("</font>")
			
			
			.setFrom(1)
			.setSize(2)
			.execute()
			.actionGet();
			
		SearchHits hits = resp.getHits();
		
		System.out.println(hits.getTotalHits());
		
		for (SearchHit hit : hits) {
			
			
			
			System.out.println(hit.getSourceAsString());
			
			System.out.println(hit.getSource().get("path"));
			
			System.out.println("--------------");
			
			System.out.println(hit.getHighlightFields().get("content").getFragments()[0]);
		}
		
	}

}
