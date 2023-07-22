package models;

import java.util.ArrayList;
import java.util.List;

/**
 * @description Class that represents a web document
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class Document {

	private int id;
	private String url;
	private String title;
	private String text;
	private String analyzedText;
	private List<Paragraph> paragraphList; 

	public Document(){
		this.paragraphList = new ArrayList<Paragraph>();
	}
	
	public List<Paragraph> getParagraphList() {
		return paragraphList;
	}

	public void setParagraphList(List<Paragraph> paragraphList) {
		this.paragraphList = paragraphList;
	}
	
	public String getAnalyzedText() {
		return analyzedText;
	}

	public void setAnalyzedText(String analyzedText) {
		this.analyzedText = analyzedText;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId(){
		return this.id;
		}
	
	public void setId(int id){
		this.id = id;
		}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl(){
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
}
