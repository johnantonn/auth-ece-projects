package models;

/**
 * @description Class that represents a paragraph segment of a web document
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class Paragraph {

	private int id;
	private int parentId;
	private String url;
	private String text;
	private String analyzedText;

	public Paragraph(){
	}
	
	public String getAnalyzedText() {
		return analyzedText;
	}

	public void setAnalyzedText(String analyzedText) {
		this.analyzedText = analyzedText;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
