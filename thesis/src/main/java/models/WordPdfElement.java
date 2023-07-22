package models;

/**
 * @description Class that represents a word probability density function element
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class WordPdfElement {

	private String text;
	private double weight;
	
	public WordPdfElement(){
		
	}
	
	public String getText() {
		return text;
	}
	public void setText(String term) {
		this.text = term;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double p) {
		this.weight = p;
	}
	
}
