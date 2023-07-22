package offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * @description Class that initiates the offline mechanism through a main method
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class Controller {

	public static void main(String[] args) throws IOException, InterruptedException{
		
		/*Read set of queries from external .txt file*/
		Set<String> queries = new HashSet<String>();
		File file = new File(IndexManager.class.getClassLoader().getResource("queries.txt").getFile());
		queries = readQueries(file.getAbsolutePath());
		
		/*Server configuration
		 * Create elasticsearch index mappings*/
		IndexManager indexManager = new IndexManager();
		indexManager.createMappings();

		/*Web parsing
		 * Parse list of url links and index the web documents*/	
		WebParser webParser = new WebParser();
		int counter = 0;
		for(String query: queries){
			counter++;
			System.out.println("Posing query "+counter+"/"+queries.size()+": "+query);
			webParser.parseWebDocuments(query);
			writeQuery(query);
			Thread.sleep(3000);
		}
		System.out.println("Corpus succesfully populated with posed queries");
	}

	private static Set<String> readQueries(String fileName) throws IOException {

		Set<String> querySet = new HashSet<String>();
			
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		while ((line = reader.readLine()) != null) {
			   querySet.add(line);
		}
			
		return querySet;
	}
	
	private static void writeQuery(String query){
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter("confirmed.txt", true));
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println(query);
		
		out.close();
		
	}

}
