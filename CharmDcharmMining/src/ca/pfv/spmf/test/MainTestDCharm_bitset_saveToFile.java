package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.charm.AlgoDCharm_Bitset;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;

import edu.algorithms.converters.*;

/**
 * Example of how to use DECLAT algorithm from the source code.
 * @author Philippe Fournier-Viger - 2014
 */
public class MainTestDCharm_bitset_saveToFile {

	public static void main(String [] arg) throws IOException{
		
		/* the database file name and
		 *  converted input file name and
		 *  the result from frequent itemsets found
		 */
		String[] filesPaths = new String[4]; 
		
		// minimum support
		double minsup = 0.1; // means a minsup of 2 transaction (we used a relative support)
		
		//Create new CsvConverter object and call the converter method
		CsvConverter csvc = new CsvConverter();
		minsup = csvc.askForInput(filesPaths);
		csvc.convertToTransactionDataset(filesPaths[0], CsvConverter.MapValues.EVENT,  filesPaths[1]);
		
		// Loading the transaction database
		TransactionDatabase database = new TransactionDatabase();
		try {
			database.loadFile( filesPaths[1]);
		} catch (IOException e) {
			System.out.println("File not found");
			//e.printStackTrace();
		}
//		context.printContext();
		
		// Applying the DECLAT algorithm
		AlgoDCharm_Bitset algo = new AlgoDCharm_Bitset();
		algo.runAlgorithm(filesPaths[2], database, minsup, true, 10000);
		// if you change use "true" in the line above, ECLAT will use
		// a triangular matrix  for counting support of itemsets of size 2.
		// For some datasets it should make the algorithm faster.
		
		// NOTE:  10000 is the size of the internal hash table that will
		// be used by dCharm.
		algo.printStats();
		System.out.println("");
		//Call the output result converter
		csvc.convertResultsForUsers(filesPaths[2], CsvConverter.MapValues.EVENT, filesPaths[3]);
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestDCharm_bitset_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
