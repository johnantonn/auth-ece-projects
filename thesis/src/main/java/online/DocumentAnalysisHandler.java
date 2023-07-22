package online;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;

import models.Topic;

/**
 * @description Class responsible for web documents analysis and document-level topic analysis
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class DocumentAnalysisHandler {

	public List<Topic> getDocumentTopics(String query){
		
		ElasticManager elasticManager = new ElasticManager();

		/*Prepare data for lda*/
		ContentAnalyzer contentAnalyzer = new ContentAnalyzer();
		contentAnalyzer.analyzeDocumentList(query);
		
		/*Document-level lda*/
		TopicAnalyzer topicAnalyzer = new TopicAnalyzer();
		topicAnalyzer.createDocumentTopics();	
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		/**
		 * Score document-level topics and get the top k*/
		List<Topic> topicList = new ArrayList<Topic>();
		List<Integer> topicIdList = new ArrayList<Integer>();
		
		File file = new File(DocumentAnalysisHandler.class.getClassLoader().getResource("online.properties").getFile());
		Properties prop = elasticManager.readProperties(file.getAbsolutePath());
		int k = Integer.parseInt(prop.getProperty("kd"));
		ScoringManager scoringManager = new ScoringManager();
		SearchResponse sr = scoringManager.scoreDocumentTopics(query, k);	
		
		Terms by_id = sr.getAggregations().get("byId");
		
		for (Bucket bucket : by_id.getBuckets()) {
			Nested topWords = bucket.getAggregations().get("topWords");
			Filter match_terms = topWords.getAggregations().get("matchTerms");
			Sum sum = match_terms.getAggregations().get("sumValues");
			topicIdList.add(Integer.parseInt(bucket.getKey()));
		    System.out.println("Document topic with id: "+Integer.parseInt(bucket.getKey())+", value: "+sum.getValue());
		}	
		
		topicList = elasticManager.fetchDocumentTopicListByIds(topicIdList, k);
			
		return topicList;
	}

	public List<Integer> rankDocuments(List<Integer> topicIds){
		
		System.out.println("Ranking documents by topics: "+topicIds.toString());
		
		ElasticManager indexManager = new ElasticManager();
		File file = new File(DocumentAnalysisHandler.class.getClassLoader().getResource("online.properties").getFile());
		Properties prop = indexManager.readProperties(file.getAbsolutePath());
		int d = Integer.parseInt(prop.getProperty("d"));
		
		ScoringManager scoringManager = new ScoringManager();
		List<Integer> topDocumentIdList = new ArrayList<Integer>();
	
		SearchResponse sr = scoringManager.scoreDocuments(topicIds, d);	
		
		Filter topicId_filter = sr.getAggregations().get("topicIdFilter");
		Nested documentDist = topicId_filter.getAggregations().get("documentDist");
		Terms by_id = documentDist.getAggregations().get("byId");
		
		for (Bucket bucket : by_id.getBuckets()) {
			Sum sum = bucket.getAggregations().get("sumValues");
			topDocumentIdList.add(Integer.parseInt(bucket.getKey()));
		    System.out.println("Document with id: "+Integer.parseInt(bucket.getKey())+", value: "+sum.getValue());
		}	
		
		return topDocumentIdList;
	}
	
}
