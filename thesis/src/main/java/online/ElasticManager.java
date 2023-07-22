package online;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import com.google.gson.Gson;

import models.Document;
import models.Paragraph;
import models.Topic;

/**
 * @description Class that contains the elasticsearch server operations of the online mechanism
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class ElasticManager {

	String indexName;
	String datasetTypeName;
	String documentsTypeName;
	String paragraphsTypeName;
	String documentTopicsTypeName;
	String paragraphTopicsTypeName;
	String analyzerName;
	
	public ElasticManager(){
		/*Opens the .properties file and save the variables*/
		File file = new File(ElasticManager.class.getClassLoader().getResource("elasticsearch.properties").getFile());
		Properties prop = readProperties(file.getAbsolutePath());
		this.indexName = prop.getProperty("index");
		this.datasetTypeName = prop.getProperty("datasetType");
		this.documentsTypeName = prop.getProperty("documentsType");
		this.paragraphsTypeName = prop.getProperty("paragraphsType");
		this.documentTopicsTypeName = prop.getProperty("documentTopicsType");
		this.paragraphTopicsTypeName = prop.getProperty("paragraphTopicsType");
		this.analyzerName = prop.getProperty("analyzer");
	}
	
	public void deleteOnlineData(){
		
		deleteDocuments();
		deleteParagraphs();
		deleteDocumentTopics();
		deleteParagraphTopics();
	}
	
	public void deleteDocuments(){
		
		int N = getDocumentsCount();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		// either use client#prepare, or use Requests# to directly build index/delete requests
		for(int i=0; i<N; i++){
			bulkRequest.add(client.prepareDelete(indexName, documentsTypeName, Integer.toString(i)));
		}
		
		bulkRequest.execute().actionGet();
	}
	
	public void deleteParagraphs(){
		
		int P = getParagraphsCount();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		// either use client#prepare, or use Requests# to directly build index/delete requests
		for(int i=0; i<P; i++){
			bulkRequest.add(client.prepareDelete(indexName, paragraphsTypeName, Integer.toString(i)));
		}
		
		bulkRequest.execute().actionGet();
	}
	
	public void deleteDocumentTopics(){
	
		int kd = getDocumentTopicsCount();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		// either use client#prepare, or use Requests# to directly build index/delete requests
		for(int i=0; i<kd; i++){
			bulkRequest.add(client.prepareDelete(indexName, documentTopicsTypeName, Integer.toString(i)));
		}
		
		bulkRequest.execute().actionGet();
	}
	
	public void deleteParagraphTopics(){
		
		int kp = getParagraphTopicsCount();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		// either use client#prepare, or use Requests# to directly build index/delete requests
		for(int i=0; i<kp; i++){
			bulkRequest.add(client.prepareDelete(indexName, paragraphTopicsTypeName, Integer.toString(i)));
		}
		
		bulkRequest.execute().actionGet();
	}
	
	public List<Document> searchDataset(String query, int N) {
		
		List<Document> documentList = new ArrayList<Document>();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse sr = client.prepareSearch(indexName)
		        .setTypes(datasetTypeName)  
		        .setQuery(QueryBuilders.matchQuery("text", query))
		        .setSize(N)
		        .setMinScore((float)0.001)
		        .execute()
		        .actionGet();
		
		client.close();
		
		Gson gson = new Gson();
		Document doc;
		for(SearchHit hit: sr.getHits()){
			doc = new Document();
			String source=hit.sourceAsString();
			doc = gson.fromJson(source, Document.class);
			documentList.add(doc);
		}
		
		return documentList;
	}
	
	public void indexDocumentList(List<Document> documentList) {

		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		Gson gson = new Gson();
		// either use client#prepare, or use Requests# to directly build index/delete requests
		for(Document doc: documentList){
			bulkRequest.add(client.prepareIndex(indexName, documentsTypeName, Integer.toString(doc.getId()))
		        .setSource(gson.toJson(doc)));
		}
		
		bulkRequest.execute().actionGet();
	}
	
	public void indexParagraphList(List<Paragraph> paragraphList) {

		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		Gson gson = new Gson();
		// either use client#prepare, or use Requests# to directly build index/delete requests
		for(Paragraph par: paragraphList){
			bulkRequest.add(client.prepareIndex(indexName, paragraphsTypeName, Integer.toString(par.getId()))
		        .setSource(gson.toJson(par)));
		}
		
		bulkRequest.execute().actionGet();
	}
	
	public void indexTopicList(List<Topic> topicList, String type) {

		if(type=="document"){
			type = documentTopicsTypeName;
		}
		else if(type=="paragraph"){
			type = paragraphTopicsTypeName;
		}
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		Gson gson = new Gson();
		// either use client#prepare, or use Requests# to directly build index/delete requests
		for(Topic topic: topicList){
			bulkRequest.add(client.prepareIndex(indexName, type, Integer.toString(topic.getId()))
		        .setSource(gson.toJson(topic)));
		}
		
		bulkRequest.execute().actionGet();
	}

	public List<Document> fetchDocumentList(int d){
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
	    .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse sr = client.prepareSearch(indexName)
				.setTypes(documentsTypeName)
				.addSort("id", SortOrder.ASC)
				.setSize(d)
				.execute()
				.actionGet();
		
		client.close();
		
		List<Document> documentList = new ArrayList<Document>();
		Document doc;
		Gson gson = new Gson();
		
		for(SearchHit hit: sr.getHits()){
			doc = new Document();
			String source=hit.sourceAsString();
			doc = gson.fromJson(source, Document.class);
			documentList.add(doc);
		}
		
		return documentList;	
	}
	
	public List<Document> fetchDocumentListByIds(List<Integer> docIds, int d){
		
		List<Document> documentList = new ArrayList<Document>();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse sr = client.prepareSearch(indexName)
		        .setTypes(documentsTypeName)  
		        .setPostFilter(FilterBuilders.termsFilter("id", docIds))
		        .setSize(d)
		        .execute()
		        .actionGet();
		
		client.close();
		
		Gson gson = new Gson();
		Document doc;
		for(SearchHit hit: sr.getHits()){
			doc = new Document();
			String source=hit.sourceAsString();
			doc = gson.fromJson(source, Document.class);
			documentList.add(doc);
		}
		
		return documentList;
	}

	public List<Paragraph> fetchParagraphListByIds(List<Integer> parIds, int p){
		
		List<Paragraph> paragraphList = new ArrayList<Paragraph>();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse sr = client.prepareSearch(indexName)
		        .setTypes(paragraphsTypeName)  
		        .setPostFilter(FilterBuilders.termsFilter("id", parIds))
		        .setSize(p)
		        .execute()
		        .actionGet();
		
		client.close();
		
		Gson gson = new Gson();
		Paragraph par;
		for(SearchHit hit: sr.getHits()){
			par = new Paragraph();
			String source=hit.sourceAsString();
			par = gson.fromJson(source, Paragraph.class);
			paragraphList.add(par);
		}
		
		return paragraphList;
	}

	public List<Topic> fetchDocumentTopicListByIds(List<Integer> topicIds, int k){
	
		List<Topic> topicList = new ArrayList<Topic>();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse sr = client.prepareSearch(indexName)
		        .setTypes(documentTopicsTypeName)  
		        .setPostFilter(FilterBuilders.termsFilter("id", topicIds))
		        .setSize(k)
		        .execute()
		        .actionGet();
		
		client.close();
		
		Gson gson = new Gson();
		Topic topic;
		for(SearchHit hit: sr.getHits()){
			topic = new Topic();
			String source=hit.sourceAsString();
			topic = gson.fromJson(source, Topic.class);
			topicList.add(topic);	
		}
		
		return topicList;
	}

	public List<Topic> fetchParagraphTopicListByIds(List<Integer> topicIds, int k){
		
		List<Topic> topicList = new ArrayList<Topic>();
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse sr = client.prepareSearch(indexName)
		        .setTypes(paragraphTopicsTypeName)  
		        .setPostFilter(FilterBuilders.termsFilter("id", topicIds))
		        .setSize(k)
		        .execute()
		        .actionGet();
		
		client.close();
		
		Gson gson = new Gson();
		Topic topic;
		for(SearchHit hit: sr.getHits()){
			topic = new Topic();
			String source=hit.sourceAsString();
			topic = gson.fromJson(source, Topic.class);
			topicList.add(topic);	
		}
		
		return topicList;
	}
	
	public int getDocumentsCount(){
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()  	
		.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

		CountResponse response = client.prepareCount(indexName)
				.setTypes(documentsTypeName)
		        .execute()
		        .actionGet();
		
		client.close();
		
		return (int) response.getCount();
	}
	
	public int getParagraphsCount(){
	
		@SuppressWarnings("resource")
		Client client = new TransportClient()  	
		.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

		CountResponse response = client.prepareCount(indexName)
				.setTypes(paragraphsTypeName)
		        .execute()
		        .actionGet();
		
		client.close();
		
		return (int) response.getCount();
	}
	
	public int getDocumentTopicsCount() {
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()  	
		.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

		CountResponse response = client.prepareCount(indexName)
				.setTypes(documentTopicsTypeName)
		        .execute()
		        .actionGet();
		
		client.close();
		
		return (int) response.getCount();
	}

	public int getParagraphTopicsCount() {

		@SuppressWarnings("resource")
		Client client = new TransportClient()  	
		.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

		CountResponse response = client.prepareCount(indexName)
				.setTypes(paragraphTopicsTypeName)
		        .execute()
		        .actionGet();
		
		client.close();
		
		return (int) response.getCount();
	}

	public List<String> analyzeQuery(String query){

		@SuppressWarnings("resource")
		Client client = new TransportClient()
		   .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
			
		List<String> analyzedQuery = new ArrayList<String>();
					
		AnalyzeResponse analyzeResponse = client.admin()
				.indices()
				.prepareAnalyze(query)
				.setIndex(indexName)
				.setAnalyzer(analyzerName)
				.execute()
				.actionGet();
		for(AnalyzeToken token: analyzeResponse.getTokens()){		
			analyzedQuery.add(token.getTerm());
		}	
			
		client.close();
		return analyzedQuery;	
	}
	
	public Properties readProperties(String fileName) {
		
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			
			input = new FileInputStream(fileName);
			prop.load(input);	
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();}}}
		
		return prop;
	}
	
}
