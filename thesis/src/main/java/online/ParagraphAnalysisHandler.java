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

import models.Paragraph;
import models.Topic;

/**
 * @description Class responsible for paragraphs analysis and paragraph-level topic analysis
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class ParagraphAnalysisHandler {

	public List<Topic> getParagraphTopics(List<Integer> topicIds, List<Integer> docIds){
		
		ElasticManager indexManager = new ElasticManager();
		List<Topic> paragraphTopicList = new ArrayList<Topic>();
		
		ContentAnalyzer contentAnalyzer = new ContentAnalyzer();
		contentAnalyzer.analyzeParagraphList(docIds);
		
		TopicAnalyzer topicAnalyzer = new TopicAnalyzer();
		topicAnalyzer.createParagraphTopics();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		/**
		 * Paragraph topics scoring section*/
		List<Integer> paragraphTopicIdList = new ArrayList<Integer>();
		
		File file = new File(ParagraphAnalysisHandler.class.getClassLoader().getResource("online.properties").getFile());
		Properties prop = indexManager.readProperties(file.getAbsolutePath());
		int k = Integer.parseInt(prop.getProperty("kp"));
		ScoringManager scoringManager = new ScoringManager();
		SearchResponse sr = scoringManager.scoreParagraphTopics(topicIds, k);
		
		Terms by_id = sr.getAggregations().get("byId");
		
		for (Bucket bucket : by_id.getBuckets()) {
			Nested topWords = bucket.getAggregations().get("topWords");
			Filter match_terms = topWords.getAggregations().get("matchTerms");
			Sum sum = match_terms.getAggregations().get("sumValues");
			paragraphTopicIdList.add(Integer.parseInt(bucket.getKey()));
		    System.out.println("Pragraph topic with id: "+Integer.parseInt(bucket.getKey())+", value: "+sum.getValue());
		}	
		
		paragraphTopicList = indexManager.fetchParagraphTopicListByIds(paragraphTopicIdList, k);
		
		return paragraphTopicList;
	}
	
	public List<Paragraph> rankParagraphs(List<Integer> topicIds){
		
		System.out.println("Ranking paragraphs by topics: "+topicIds.toString());
		
		ElasticManager indexManager = new ElasticManager();
		File file = new File(ParagraphAnalysisHandler.class.getClassLoader().getResource("online.properties").getFile());
		Properties prop = indexManager.readProperties(file.getAbsolutePath());
		int p = Integer.parseInt(prop.getProperty("p"));

		List<Paragraph> topParagraphList = new ArrayList<Paragraph>();
		List<Integer> topParagraphIdList = new ArrayList<Integer>();
		
		ScoringManager scoringManager = new ScoringManager();
		SearchResponse sr = scoringManager.scoreParagraphs(topicIds, p);	
		
		Filter topicId_filter = sr.getAggregations().get("topicIdFilter");
		Nested documentDist = topicId_filter.getAggregations().get("documentDist");
		Terms by_id = documentDist.getAggregations().get("byId");
		
		for (Bucket bucket : by_id.getBuckets()) {
			Sum sum = bucket.getAggregations().get("sumValues");
			topParagraphIdList.add(Integer.parseInt(bucket.getKey()));
			System.out.println("Paragraph with id: "+bucket.getKey()+", value: "+sum.getValue());
		}	
		
		topParagraphList = indexManager.fetchParagraphListByIds(topParagraphIdList, p);
		
		/*Delete on-line types*/
		indexManager.deleteOnlineData();
		
		return topParagraphList;
	}


}
