package models;

import java.util.ArrayList;
import java.util.List;

/**
 * @description Class that represents a topic
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class Topic {

	private int id;
	private List<WordPdfElement> topWordList;
	private List<PdfElement> topicDocumentDistribution;
	
	public Topic(){
		this.topWordList = new ArrayList<WordPdfElement>();
		this.topicDocumentDistribution = new ArrayList<PdfElement>();
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public List<WordPdfElement> getTopWordsList() {
		return topWordList;
	}

	public void setTopWordsList(List<WordPdfElement> topWordList) {
		this.topWordList = topWordList;
	}

	public List<PdfElement> getTopicDocumentDistribution() {
		return topicDocumentDistribution;
	}

	public void setTopicDocumentDistribution(
			List<PdfElement> topicOverDocsDistribution) {
		this.topicDocumentDistribution = topicOverDocsDistribution;
	}
	
}
