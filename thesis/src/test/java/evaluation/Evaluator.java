package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.google.gson.Gson;

import models.Document;
import models.PdfElement;
import models.Topic;
import online.DocumentAnalysisHandler;
import online.ElasticManager;

/**
 * @description Class that contains the metrics calculation methods of the evaluation process
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class Evaluator {

	String indexName;
	String documentTopicsTypeName;
	String documentsTypeName;
	int d;
	int kRank;
	
	double meanPrecisionDefault;
	double meanRecallDefault;
	double meanPrecisionProposed;
	double meanRecallProposed;
	double meanImprovementPrecision;
	double meanImprovementRecall;
	
	public Evaluator(){
		ElasticManager elasticManager = new ElasticManager();
		Properties prop = elasticManager.readProperties("C:\\Users\\giannis\\eclipse-workspace\\insight\\evaluation.properties");
		this.d = Integer.parseInt(prop.getProperty("d"));
		this.kRank = Integer.parseInt(prop.getProperty("kRank"));
		this.indexName = prop.getProperty("index");
		this.documentTopicsTypeName = prop.getProperty("documentTopicsType");
		this.documentsTypeName = prop.getProperty("documentsType");
		meanPrecisionDefault=0;
		meanRecallDefault=0;
		meanImprovementPrecision=0;
	}
	
	public void evaluate(String query) throws IOException {
		
		ElasticManager elasticManager = new ElasticManager();
		List<Integer> rankTopicIds;
		
		/*Run the document-level analysis to create the topics
		 * return the top-k topics that will be used in ranking*/
		DocumentAnalysisHandler documentAnalysisHandler = new DocumentAnalysisHandler();
		List<Topic> topicList = documentAnalysisHandler.getDocumentTopics(query);
		
		/*********************************************************************/
		
		/*Create the initial vector*/
		ICombinatoricsVector<String> initialVector = Factory.createVector(
		      new String[] { "1", "2", "3", "4", "5", "6" } );

		// Create a simple combination generator to generate 3-combinations of the initial vector
		Generator<String> gen = Factory.createSimpleCombinationGenerator(initialVector, kRank);
		
		/********************************************************************/
		
		for (ICombinatoricsVector<String> combination : gen) {
			
			rankTopicIds = new ArrayList<Integer>(); 
			for(int i=0; i<kRank; i++){
				rankTopicIds.add(Integer.parseInt(combination.getValue(i)));
			}

			/*Rank the documents using the default practical scoring function*/
			List<Document> defaultDocumentList = searchDocuments(query, d);		
			List<Integer> defaultDocumentIds = new ArrayList<Integer>();
			for(Document doc: defaultDocumentList){defaultDocumentIds.add(doc.getId());}
		
			/*Rank the documents using topics*/
			List<Integer> proposedDocumentIds = documentAnalysisHandler.rankDocuments(rankTopicIds);

			/*Fetch the whole topic list*/
			int K = elasticManager.getDocumentTopicsCount();
			topicList = fetchDocumentTopicList(K);
		
			/*Calculate the metrics for each case*/
			double precisionDefault = calculatePrecision(topicList, defaultDocumentIds, rankTopicIds);
			double recallDefault = calculateRecall(topicList, defaultDocumentIds, rankTopicIds);
		
			double precisionProposed = calculatePrecision(topicList, proposedDocumentIds, rankTopicIds);
			double recallProposed = calculateRecall(topicList, proposedDocumentIds, rankTopicIds);
		
			double precisionRate = (double) Math.abs(precisionDefault-precisionProposed)/precisionDefault;
			double recallRate = (double) Math.abs(recallDefault-recallProposed)/recallDefault;
		
			meanPrecisionDefault += precisionDefault;
			meanPrecisionProposed += precisionProposed;
			meanRecallDefault += recallDefault;
			meanRecallProposed += recallProposed;
			meanImprovementPrecision += precisionRate;
			meanImprovementRecall += recallRate;
			
		}
		
		/*Average*/
		meanPrecisionDefault = (double) meanPrecisionDefault/gen.getNumberOfGeneratedObjects();
		meanPrecisionProposed = (double) meanPrecisionProposed/gen.getNumberOfGeneratedObjects();
		meanRecallDefault = (double) meanRecallDefault/gen.getNumberOfGeneratedObjects();
		meanRecallProposed = (double) meanRecallProposed/gen.getNumberOfGeneratedObjects();
		meanImprovementPrecision = (double) meanImprovementPrecision/gen.getNumberOfGeneratedObjects();
		meanImprovementRecall = (double) meanImprovementRecall/gen.getNumberOfGeneratedObjects();
		
		System.out.println("meanPrecisionDefault: "+meanPrecisionDefault);
		System.out.println("meanRecallDefault: "+meanRecallDefault);
		System.out.println("meanPrecisionProposed: "+meanPrecisionProposed);
		System.out.println("meanRecallProposed: "+meanRecallProposed);
		System.out.println("meanImprovementPrecision: "+meanImprovementPrecision);
		System.out.println("meanImprovementRecall: "+meanImprovementRecall);
		
		/*Delete data from 'documents' and 'documentTopics' types*/
		elasticManager.deleteDocuments();
		elasticManager.deleteDocumentTopics();
	}

	
	private double calculatePrecision(List<Topic> topicList, List<Integer> documentIds, List<Integer> topicIds){
		
		double precision;
		double partialSumOfRelativeTopics = 0;  //sum of relative topics in d documents
		double fullSumOfAllTopics = d; 			//sum of all topics in documents
		
		/*Calculate the default metrics*/
		for(Topic topic: topicList){
			if(topicIds.contains(topic.getId())){
				for(PdfElement element: topic.getTopicDocumentDistribution()){
					if(documentIds.contains(element.getId())){
						partialSumOfRelativeTopics += element.getValue();
					}
				}
			}
		}
		
		precision = (double) partialSumOfRelativeTopics/fullSumOfAllTopics;
		
		return precision;
	}
	
	private double calculateRecall(List<Topic> topicList, List<Integer> documentIds, List<Integer> topicIds){
		
		double recall;
		double partialSumOfRelativeTopics = 0;  //sum of relative topics in d documents
		double fullSumOfRelativeTopics = 0;		//sum of relative topics in N documents
		
		/*Calculate the default metrics*/
		for(Topic topic: topicList){
			if(topicIds.contains(topic.getId())){
				for(PdfElement element: topic.getTopicDocumentDistribution()){
					if(documentIds.contains(element.getId())){
						partialSumOfRelativeTopics += element.getValue();
					}
					fullSumOfRelativeTopics += element.getValue();
				}
			}
		}
		
		recall = (double) partialSumOfRelativeTopics/fullSumOfRelativeTopics;
		
		return recall;
	}
	
	
	/*******************/
	
	private List<Document> searchDocuments(String query, int N) {
		
		List<Document> documentList = new ArrayList<Document>();
			
		@SuppressWarnings("resource")
		Client client = new TransportClient()
	       .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
			
		SearchResponse sr = client.prepareSearch(indexName)
				.setTypes(documentsTypeName)  
				.setQuery(QueryBuilders.matchQuery("text", query))
				.setSize(N)
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
	
	private List<Topic> fetchDocumentTopicList(int K) {

		@SuppressWarnings("resource")
		Client client = new TransportClient()
	    .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		SearchResponse sr = client.prepareSearch(indexName)
				.setTypes(documentTopicsTypeName)
				.setSize(K)
				.execute()
				.actionGet();
		
		client.close();
		
		List<Topic> topicList = new ArrayList<Topic>();
		Topic topic;
		Gson gson = new Gson();
		
		for(SearchHit hit: sr.getHits()){
			topic = new Topic();
			String source=hit.sourceAsString();
			topic = gson.fromJson(source, Topic.class);
			topicList.add(topic);
		}
		
		return topicList;
	}

	public double getMeanPrecisionDefault() {
		return meanPrecisionDefault;
	}

	public double getMeanRecallDefault() {
		return meanRecallDefault;
	}


	public double getMeanPrecisionProposed() {
		return meanPrecisionProposed;
	}



	public double getMeanRecallProposed() {
		return meanRecallProposed;
	}



	public double getMeanImprovementRecall() {
		return meanImprovementRecall;
	}

	public double getMeanImprovementPrecision() {
		return meanImprovementPrecision;
	}
	
}
