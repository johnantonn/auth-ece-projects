package online;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import models.Topic;
import models.WordPdfElement;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

/**
 * @description Class that contains all scoring operations done by elasticsearch aggregations
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class ScoringManager {

	String indexName;
	String documentTopicsTypeName;
	String paragraphTopicsTypeName;
	
	public ScoringManager(){
		/*Opens the .properties file and save the variables*/
		File file = new File(ScoringManager.class.getClassLoader().getResource("elasticsearch.properties").getFile());
		Properties prop = readProperties(file.getAbsolutePath());
		this.indexName = prop.getProperty("index");
		this.documentTopicsTypeName = prop.getProperty("documentTopicsType");
		this.paragraphTopicsTypeName = prop.getProperty("paragraphTopicsType");
	}
	
	public SearchResponse scoreDocumentTopics(String query, int k){
		
		ElasticManager indexManager = new ElasticManager();
		int K = indexManager.getDocumentTopicsCount();
		System.out.println("There are "+K +" document topcis");
		
		List<String> analyzedQuery = indexManager.analyzeQuery(query);
		System.out.println(analyzedQuery);
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse response = client.prepareSearch(indexName)
		        .setTypes(documentTopicsTypeName)
		        .setSearchType(SearchType.COUNT)
				.addAggregation(AggregationBuilders.terms("byId")
					.field("id")
					.size(k)
					.order(Terms.Order.aggregation("topWords>matchTerms>sumValues", false))
					.subAggregation(AggregationBuilders.nested("topWords")
						.path("topWordList")
							.subAggregation(AggregationBuilders.filter("matchTerms")
								.filter(FilterBuilders.termsFilter("topWordList.text", analyzedQuery))							
									.subAggregation(AggregationBuilders.sum("sumValues")	
										.field("topWordList.weight")))))
				.execute()
		        .actionGet();
		
		client.close();
		//System.out.println(response.toString());
		
		return response;	
	}
	
	public SearchResponse scoreParagraphTopics(List<Integer> topicIds, int k){
		
		ElasticManager indexManager = new ElasticManager();
		int K = indexManager.getParagraphTopicsCount();
		System.out.println("There are "+K+" paragraph topcis");
		
		List<Topic> selectedTopics = indexManager.fetchDocumentTopicListByIds(topicIds, topicIds.size());
		List<String> topWords = new ArrayList<String>();

		for(Topic topic: selectedTopics){
			for(WordPdfElement element: topic.getTopWordsList()){
				topWords.add(element.getText());
			}
		}
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse response = client.prepareSearch(indexName)
		        .setTypes(paragraphTopicsTypeName)
		        .setSearchType(SearchType.COUNT)
				.addAggregation(AggregationBuilders.terms("byId")
					.field("id")
					.size(k)
					.order(Terms.Order.aggregation("topWords>matchTerms>sumValues", false))
					.subAggregation(AggregationBuilders.nested("topWords")
						.path("topWordList")
							.subAggregation(AggregationBuilders.filter("matchTerms")
								.filter(FilterBuilders.termsFilter("topWordList.text", topWords))							
									.subAggregation(AggregationBuilders.sum("sumValues")	
										.field("topWordList.weight")))))
				.execute()
		        .actionGet();
		
		client.close();
		//System.out.println(response.toString());
		
		return response;
	}
	
	public SearchResponse scoreDocuments(List<Integer> topicIds, int d){
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse response = client.prepareSearch(indexName)
		        .setTypes(documentTopicsTypeName)
		        .setSearchType(SearchType.COUNT)
				.addAggregation(AggregationBuilders.filter("topicIdFilter")
					.filter(FilterBuilders.termsFilter("id", topicIds))
					.subAggregation(AggregationBuilders.nested("documentDist")
						.path("topicDocumentDistribution")
							.subAggregation(AggregationBuilders.terms("byId")
								.field("topicDocumentDistribution.id")
								.size(d)
								.order(Terms.Order.aggregation("sumValues", false))
									.subAggregation(AggregationBuilders
										.sum("sumValues")
										.field("topicDocumentDistribution.value")))))
				.execute()
		        .actionGet();
		
		client.close();
		
		//System.out.println(response.toString());
		return response;
	}
	
	public SearchResponse scoreParagraphs(List<Integer> topicIds, int p) {
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()
        .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse response = client.prepareSearch(indexName)
		        .setTypes(paragraphTopicsTypeName)
		        .setSearchType(SearchType.COUNT)
				.addAggregation(AggregationBuilders.filter("topicIdFilter")
					.filter(FilterBuilders.termsFilter("id", topicIds))
					.subAggregation(AggregationBuilders.nested("documentDist")
						.path("topicDocumentDistribution")
							.subAggregation(AggregationBuilders.terms("byId")
								.field("topicDocumentDistribution.id")
								.size(p)
								.order(Terms.Order.aggregation("sumValues", false))
									.subAggregation(AggregationBuilders
										.sum("sumValues")
										.field("topicDocumentDistribution.value")))))
				.execute()
		        .actionGet();
		
		client.close();
		
		//System.out.println(response.toString());
		return response;
		
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
