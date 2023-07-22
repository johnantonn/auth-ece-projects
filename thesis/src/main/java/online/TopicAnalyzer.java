package online;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import online.jgibblda.Estimator;
import online.jgibblda.LDACmdOption;
import models.Topic;
import models.PdfElement;
import models.WordPdfElement;

/**
 * @description Class responsible for topic analysis of a set of web documents or paragraphs
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class TopicAnalyzer {
	
	private int K;
	private int twords;
	private double[][] theta;
	private List<Map<String, Double>> topWordProbList;
	private String[][] topicTopWordClouds;
	
	public TopicAnalyzer(){
		this.topWordProbList = new ArrayList<Map<String, Double>>();
	}
	
	public List<Topic> createDocumentTopics(){
		
		/*Perform document-level lda*/
		File file = new File(TopicAnalyzer.class.getClassLoader().getResource("documentLDA.properties").getFile());
		lda(file.getAbsolutePath());

		ElasticManager indexManager = new ElasticManager();
		int N = indexManager.getDocumentsCount();
		
		return createTopics("document", N);
	}
	
	public List<Topic> createParagraphTopics(){
		
		/*Perform paragraph-level lda*/
		File file = new File(TopicAnalyzer.class.getClassLoader().getResource("paragraphLDA.properties").getFile());
		lda(file.getAbsolutePath());

		ElasticManager indexManager = new ElasticManager();
		int P = indexManager.getParagraphsCount();
	
	    return	createTopics("paragraph", P);
		
	}
	
	private void lda(String propertiesFileName){
		
		System.out.println("LDA Analysis");
		
		LDACmdOption ldaOption = readLDAParams(propertiesFileName);
		
		this.K = ldaOption.K;
		this.twords = ldaOption.twords;
		
		Estimator estimator = new Estimator();
		estimator.init(ldaOption);
		estimator.estimate();
		
		this.theta = estimator.trnModel.theta;
		this.topWordProbList = estimator.trnModel.topicWordProbList;
		this.topicTopWordClouds = estimator.trnModel.wordClouds;
		
	}
	
	private List<Topic> createTopics(String type, int n){
		
		ElasticManager indexManager = new ElasticManager();
		PdfElement pdfElement;
		WordPdfElement wordPdfElement;
		List<Topic> topicList = new ArrayList<Topic>();
		Topic topic;
		
		/*Topic attributes*/
		for(int k=0; k<K; k++){					
			topic = new Topic();
			topic.setId(k);
			for(int i=0; i<n; i++){
				pdfElement = new PdfElement();
				pdfElement.setId(i);
				pdfElement.setValue(theta[i][k]);
				topic.getTopicDocumentDistribution().add(pdfElement);
			}
			
			Map<String, Double> topicTopWordMap = new HashMap<String, Double>();
			topicTopWordMap = topWordProbList.get(k);
	
			for(int j=0; j<twords; j++){
				wordPdfElement = new WordPdfElement();
				wordPdfElement.setText(topicTopWordClouds[k][j]);
				wordPdfElement.setWeight(topicTopWordMap.get(topicTopWordClouds[k][j]));
				topic.getTopWordsList().add(wordPdfElement);	
			}
			
			topicList.add(topic);
		}
		
		indexManager.indexTopicList(topicList, type);
		
		System.out.println("Indexed "+topicList.size()+" "+type+" topics");
		return topicList;
	}

	private LDACmdOption readLDAParams(String fileName) {
		
		ElasticManager indexManager = new ElasticManager();
		Properties prop = indexManager.readProperties(fileName);
		LDACmdOption ldaOption = new LDACmdOption();
		
		File folder = new File(TopicAnalyzer.class.getClassLoader().getResource("analyzed").getFile());
		
		ldaOption.est = true;
		ldaOption.alpha = Double.parseDouble(prop.getProperty("alpha"));
		ldaOption.beta = Double.parseDouble(prop.getProperty("beta"));
		ldaOption.K = Integer.parseInt(prop.getProperty("K"));
		ldaOption.niters = Integer.parseInt(prop.getProperty("niters"));
		ldaOption.savestep = Integer.parseInt(prop.getProperty("savestep"));
		ldaOption.dir = folder.getAbsolutePath();
		ldaOption.dfile = prop.getProperty("dfile");
		ldaOption.twords = Integer.parseInt(prop.getProperty("twords"));
			
		return ldaOption;
	}

}
