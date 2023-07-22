package online;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import models.Document;
import models.Paragraph;

/**
 * @description Class responsible for content analysis of a set of web documents or paragraphs
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class ContentAnalyzer {

	public void analyzeDocumentList(String query){
		
		List<String> analyzedDocumentList = new ArrayList<String>();
		ElasticManager indexManager = new ElasticManager();
		File file = new File(ContentAnalyzer.class.getClassLoader().getResource("online.properties").getFile());
		Properties prop = indexManager.readProperties(file.getAbsolutePath());
		int N = Integer.parseInt(prop.getProperty("N"));
		int idCounter = 0;
		List<Document> documentList = indexManager.searchDataset(query, N);
		
		for(Document doc: documentList){
			doc.setId(idCounter++);
			analyzedDocumentList.add(doc.getAnalyzedText());
		}
		
		File folder = new File(ContentAnalyzer.class.getClassLoader().getResource("analyzed").getFile());
		
		System.out.println("Writing "+documentList.size()+" analyzed documents in "+folder.getAbsolutePath()+"\\analyzedDocuments.txt");
		saveAnalyzedList(analyzedDocumentList, folder.getAbsolutePath()+"\\analyzedDocuments.txt");
		indexManager.indexDocumentList(documentList);
	}
	
	public void analyzeParagraphList(List<Integer> docIds){
		
		ElasticManager indexManager = new ElasticManager();
		int N = indexManager.getDocumentsCount();
		List<Document> documentList = indexManager.fetchDocumentListByIds(docIds, N);
		List<Paragraph> paragraphList = new ArrayList<Paragraph>();
		List<String> analyzedParagraphList = new ArrayList<String>();		
		int parCounter = 0;
		
		for(Document doc: documentList){
			for(Paragraph par: doc.getParagraphList()){
				par.setId(parCounter++);
				analyzedParagraphList.add(par.getAnalyzedText());
				paragraphList.add(par);
			}
		}
		
		indexManager.indexParagraphList(paragraphList);
		File folder = new File(ContentAnalyzer.class.getClassLoader().getResource("analyzed").getFile());
		System.out.println("Writing "+documentList.size()+" analyzed paragraphs in "+folder.getAbsolutePath()+"\\analyzedParagraphs.txt");
		saveAnalyzedList(analyzedParagraphList, folder.getAbsolutePath()+"\\analyzedParagraphs.txt");	
	}

	private void saveAnalyzedList(List<String> analyzedList, String fileName){
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		out.println(analyzedList.size());
		for(String content: analyzedList){
			out.println(content);
		}
		
		out.close();
	}

}
