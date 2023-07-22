package evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * @description Class that initiates the evaluation process for a set of queries read from an external .txt file
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class EvalController {

	public static void main(String[] args) throws IOException {

		double meanPrecisionDefault=0;
		double meanPrecisionProposed=0;
		double meanRecallDefault=0;
		double meanRecallProposed=0;
		double meanImprovementPrecision=0;
		double meanImprovementRecall=0;
		
		int q=0;
		
		Evaluator evaluator;
		Set<String> queries = new HashSet<String>();
		queries = readQueries("queries.txt");	

		for(String query: queries){
			
			q++;
			System.out.println("Evaluation metrics for query: "+query);
			evaluator = new Evaluator();
			evaluator.evaluate(query);
			
			meanPrecisionDefault += evaluator.getMeanPrecisionDefault();
			meanRecallDefault += evaluator.getMeanRecallDefault();
			meanPrecisionProposed += evaluator.getMeanPrecisionProposed();
			meanRecallProposed += evaluator.getMeanRecallProposed();
			meanImprovementPrecision += evaluator.getMeanImprovementPrecision();
			meanImprovementRecall += evaluator.getMeanImprovementRecall();
			
			System.out.println("meanPrecisionDefault: "+(double) meanPrecisionDefault/q);
			System.out.println("meanRecallDefault: "+(double) meanRecallDefault/q);
			System.out.println("meanPrecisionProposed: "+(double) meanPrecisionProposed/q);
			System.out.println("meanRecallProposed: "+(double) meanRecallProposed/q);
			System.out.println("meanImprovementPrecision: "+(double) meanImprovementPrecision/q);
			System.out.println("meanImprovementRecall: "+(double) meanImprovementRecall/q);
			
			delay(2000);
		}
		
		meanPrecisionDefault = (double) meanPrecisionDefault/queries.size();
		meanPrecisionProposed = (double) meanPrecisionProposed/queries.size();
		meanImprovementPrecision = (double) meanImprovementPrecision/queries.size();
		
		meanRecallDefault = (double) meanRecallDefault/queries.size();	
		meanRecallProposed = (double) meanRecallProposed/queries.size();
		meanImprovementRecall = (double) meanImprovementRecall/queries.size();
		
		String result = meanPrecisionDefault + "\t\t" + meanPrecisionProposed  +"\t\t"+meanImprovementPrecision+
				"\t\t"+meanRecallDefault+"\t\t"+meanRecallProposed+"\t\t"+meanImprovementRecall;
		
		writeMetrics(result);

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
	
	private static void delay(int msec){
		
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void writeMetrics(String result) throws IOException{
		
		PrintWriter out = new PrintWriter(new FileWriter("metrics.txt", true));
		out.println(result);
		
		out.close();
	}
	
}
