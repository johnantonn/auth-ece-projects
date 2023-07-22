package online;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import models.Paragraph;
import models.Topic;

/**
 * @description Application resources class
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

@Path("/")
public class Controller {
	
	@GET
	@Path("/documentTopics")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Topic> getDocumentTopics(@QueryParam("q") String query){
			
		List<Topic> documentTopicList = new ArrayList<Topic>();
		DocumentAnalysisHandler documentAnalysisHandler = new DocumentAnalysisHandler();
		documentTopicList = documentAnalysisHandler.getDocumentTopics(query);

		return documentTopicList;
	}
	
	@GET
	@Path("/paragraphTopics")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Topic> getParagraphTopics(@QueryParam("topicIds") List<Integer> topicIds){		
		
		List<Integer> topDocumentIds = new ArrayList<Integer>();
		DocumentAnalysisHandler documentAnalysisHandler = new DocumentAnalysisHandler();
		topDocumentIds = documentAnalysisHandler.rankDocuments(topicIds);
		
		List<Topic> paragraphTopicList = new ArrayList<Topic>();
		ParagraphAnalysisHandler paragraphAnalysisHandler = new ParagraphAnalysisHandler();
		paragraphTopicList = paragraphAnalysisHandler.getParagraphTopics(topicIds, topDocumentIds);
		
		return paragraphTopicList;
	}
	
	@GET
	@Path("/paragraphs")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Paragraph> getParagraphs(@QueryParam("topicIds") List<Integer> topicIds){
		
		List<Paragraph> topParagraphList = new ArrayList<Paragraph>();
		ParagraphAnalysisHandler paragraphAnalysisHandler = new ParagraphAnalysisHandler();
		topParagraphList = paragraphAnalysisHandler.rankParagraphs(topicIds);
		
		return topParagraphList;
	}

}
