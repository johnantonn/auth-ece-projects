package offline;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import models.Document;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.exists.ExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.google.gson.Gson;

/**
 * @description Class that contains the elasticsearch server operations of the offline mechanism
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class IndexManager {

	String indexName;
	String datasetTypeName;
	String documentsTypeName;
	String paragraphsTypeName;
	String documentTopicsTypeName;
	String paragraphTopicsTypeName;
	String analyzerName;
	
	public IndexManager(){
		/*Open the .properties file and save the parameters to class attributes*/
		File file = new File(IndexManager.class.getClassLoader().getResource("elasticsearch.properties").getFile());
		Properties prop = readProperties(file.getAbsolutePath());
		this.indexName = prop.getProperty("index");
		this.datasetTypeName = prop.getProperty("datasetType");
		this.documentsTypeName = prop.getProperty("documentsType");
		this.paragraphsTypeName = prop.getProperty("paragraphsType");
		this.documentTopicsTypeName = prop.getProperty("documentTopicsType");
		this.paragraphTopicsTypeName = prop.getProperty("paragraphTopicsType");
		this.analyzerName = prop.getProperty("analyzer");
	}
	
	public void createMappings(){
		
		if(checkIndex(indexName)){
			System.out.println("Index _"+indexName+" already exists!");
			return;
		}
		else{
			try {
				if(createIndex(indexName).isAcknowledged()){	
					if(createDocumentMapping(indexName, datasetTypeName).isAcknowledged()){
						System.out.println("Dataset mapping created succesfully");
					}
					if(createDocumentMapping(indexName, documentsTypeName).isAcknowledged()){
						System.out.println("Documents mapping created succesfully");
					}
					if(createParagraphMapping(indexName, paragraphsTypeName).isAcknowledged()){
						System.out.println("Paragraphs mapping created succesfully");
					}
					if(createTopicMapping(indexName, documentTopicsTypeName).isAcknowledged()){
						System.out.println("Document topics mapping created succesfully");
					}
					if(createTopicMapping(indexName, paragraphTopicsTypeName).isAcknowledged()){
						System.out.println("Paragraph topics mapping created succesfully");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Boolean checkIndex(String indexName){
		
		@SuppressWarnings("resource")	
		Client client = new TransportClient()  	
			.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		boolean hasIndex = client
				.admin()
				.indices()
				.exists(new IndicesExistsRequest(indexName))
				.actionGet()
				.isExists();
		
		return hasIndex; 
	}
	
	private CreateIndexResponse createIndex(String indexName) throws IOException{
		
		@SuppressWarnings("resource")	
		Client client = new TransportClient()  	
			.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		XContentBuilder settings = jsonBuilder()
				.startObject()
					.startObject("analysis")
						.startObject("filter")
							.startObject("my_stop")
								.field("type", "stop")
								.field("stopwords_path", "stopwords.txt")
							.endObject()
						.endObject()
						.startObject("char_filter")
							.startObject("quotes")
								.field("type", "mapping")
								.field("mappings", new String[]{
										"\\u0091=>\\u0027", 
										"\\u0092=>\\u0027", 
										"\\u2018=>\\u0027", 
										"\\u2019=>\\u0027",
										"\\u201B=>\\u0027"})
							.endObject()
						.endObject()
						.startObject("analyzer")
							.startObject("my_analyzer")
								.field("type", "custom")
								.field("tokenizer", "standard")
								.field("char_filter","quotes")
								.field("filter", new String[]{"standard", "asciifolding", "lowercase","my_stop","snowball"})
							.endObject()
						.endObject()
					.endObject()
				.endObject();
		
		CreateIndexResponse createResponse = client
				.admin()
				.indices()
				.prepareCreate(indexName)
				.setSettings(settings)
				.execute()
				.actionGet();
		
		return createResponse;
	}
	
	private PutMappingResponse createDocumentMapping(String indexName, String typeName) throws IOException{
		
		@SuppressWarnings("resource")	
		Client client = new TransportClient()  	
			.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	
			XContentBuilder mapping = jsonBuilder()
			         .startObject()
			              .startObject(typeName)// index type
			              		.startObject("properties")
			              			.startObject("id")
			                           .field("type", "integer")
			                        .endObject()
			                        .startObject("url")
			                        	.field("type","string")
			                        	.field("index","not_analyzed")
			                        .endObject()
			                         .startObject("title")
			                        	.field("type","string")
			                        	.field("index","not_analyzed")
			                        .endObject()
			                         .startObject("text")
			                        	.field("type","string")
			                        	.field("analyzer", "standard")
			                        .endObject()
			                         .startObject("analyzedText")
			                        	.field("type","string")
			                        	.field("index", "no")
			                        .endObject()
			                        .startObject("paragraphList")
			                        	.startObject("properties")
			                        		.startObject("id")
			                        			.field("type", "integer")
			                        		.endObject()
			                        		.startObject("parentId")
			                        			.field("type","integer")
			                        		.endObject()
			                        		.startObject("url")
			                        			.field("type","string")
			                        			.field("index","not_analyzed")
			                        		.endObject()
			                        		.startObject("text")
			                        			.field("type", "string")
			                        			.field("analyzer", analyzerName)
			                        		.endObject()
			                        		.startObject("analyzedText")
			                        			.field("type", "string")
			                        			.field("index", "no")
			                        		.endObject()
			                        	.endObject()
			                        .endObject()
			                   .endObject()
			               .endObject()
			            .endObject();
			
			PutMappingResponse response = client
				  	.admin()
				  	.indices()
	                .preparePutMapping(indexName)
	                .setType(typeName)
	                .setSource(mapping)
	                .execute()
	                .actionGet();
		  
		  return response;
	}
	
	private PutMappingResponse createParagraphMapping(String indexName, String typeName) throws IOException {
		
		@SuppressWarnings("resource")	
		Client client = new TransportClient()  	
			.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	
			XContentBuilder mapping = jsonBuilder()
			         .startObject()
			              .startObject(typeName)// index type
			              		.startObject("properties")
			              			.startObject("id")
			                           .field("type", "integer")
			                        .endObject()
			                        .startObject("parentId")
			                        	.field("type","integer")
			                        .endObject()
			                        .startObject("url")
			                        	.field("type","string")
			                        	.field("index","not_analyzed")
			                        .endObject()
			                        .startObject("text")
			                        	.field("type", "string")
			                        	.field("index", "no")
			                        .endObject()
			                        .startObject("analyzedText")
			                        	.field("type", "string")
			                        	.field("index", "no")
			                        .endObject()
			                   .endObject()
			               .endObject()
			            .endObject();
			
			PutMappingResponse response = client
				  	.admin()
				  	.indices()
	                .preparePutMapping(indexName)
	                .setType(typeName)
	                .setSource(mapping)
	                .execute()
	                .actionGet();
		  
		  return response;

	}
	
	private PutMappingResponse createTopicMapping(String indexName, String typeName) throws IOException{
		
		@SuppressWarnings("resource")	
		Client client = new TransportClient()  	
			.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	
			XContentBuilder mapping = jsonBuilder()
			         .startObject()
			              .startObject(typeName)
			              		.startObject("properties")
			                   		.startObject("id")
			                           .field("type", "integer")
			                        .endObject()
			                        .startObject("topWordList")
			                        	.field("type", "nested")//!!!
			                        	.startObject("properties")
			                        		.startObject("text")
			                        			.field("type","string")
			                        			.field("index", "not_analyzed")
			                        			//.field("search_analyzer", analyzerName)//!!!
			                        		.endObject()
			                        		.startObject("weight")
			                        			.field("type","double")
			                        		.endObject()
			                        	.endObject()
			                        .endObject()
			                        .startObject("topicDocumentDistribution")
			                        	.field("type", "nested")
			                        	.startObject("properties")
			                        		.startObject("id")
			                        			.field("type", "integer")
			                        		.endObject()
			                        		.startObject("value")
			                        			.field("type", "double")
			                        		.endObject()
			                        	.endObject()
			                        .endObject()
			                   .endObject()
			               .endObject()
			            .endObject();
		
		  PutMappingResponse response = client
				  	.admin()
				  	.indices()
	                .preparePutMapping(indexName)
	                .setType(typeName)
	                .setSource(mapping)
	                .execute()
	                .actionGet();
		  
		  return response;
	}
	
	public Boolean checkDocumentExistance(String url){
		
		@SuppressWarnings("resource")	
		Client client = new TransportClient()  	
			.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	
		ExistsResponse sr = client.prepareExists(indexName)
		        .setTypes(datasetTypeName)  
		        .setQuery(QueryBuilders.termQuery("url", url))
		        .execute()
		        .actionGet();
		
		return sr.exists();
	}
	
	public void indexDocument(Document doc){
		
		@SuppressWarnings("resource")	
		Client client = new TransportClient()  	
			.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
	
		Gson gson = new Gson();	
		String json = gson.toJson(doc);		
			
		client.prepareIndex(indexName, datasetTypeName,
				Integer.toString(doc.getId()))	    							
		.setSource(json)								
		.execute()								
		.actionGet();			
		
		client.close();
	}

	public int getDatasetDocumentsCount(){
		
		@SuppressWarnings("resource")
		Client client = new TransportClient()  	
		.addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

		CountResponse response = client.prepareCount(indexName)
				.setTypes(datasetTypeName)
		        .execute()
		        .actionGet();
		
		client.close();
		
		return (int) response.getCount();
	}
	
	public String analyze(String text){

		@SuppressWarnings("resource")
		Client client = new TransportClient()
	    .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
		
		StringBuilder builder = new StringBuilder();		
			AnalyzeResponse analyzeResponse = client.admin()
					.indices()
					.prepareAnalyze(text)
					.setIndex(indexName)
					.setAnalyzer(analyzerName)
					.execute()
					.actionGet();
			for(AnalyzeToken token: analyzeResponse.getTokens()){		
				builder.append(token.getTerm()+" ");
			}	
		
		client.close();
		return builder.toString();	
	}
	
	public Properties readProperties(String fileName) {
		
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			
			input = new FileInputStream(fileName);
			prop.load(input);	
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();}}}
		
		return prop;
	}
	
}
