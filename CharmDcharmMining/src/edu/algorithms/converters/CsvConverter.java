package edu.algorithms.converters;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.*;

public class CsvConverter {
		
		private static final String EMPTY_STRING = "";
		private static final String SPLIT_CVS_STRING = ",";
		private static final String SPLIT_BY_SPACE_STRING = " ";
		private static final int CSVC_OK = 0;
		private static final int CSVC_FILE_NOT_FOUND = 1;
		private static final int CSVC_IO_ERR = 2;
		
		public enum MapValues { COMPONENT , EVENT };
		
		public CsvConverter() {}
		
		public int writeToFile(StringBuilder input, String fileName) throws IOException
		{
			
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(fileName));
			int returnValue = CSVC_OK;
			
			try {
				buffWriter.write(input.toString());  	                                                        
	        } catch (IOException e) {
	        	returnValue = CSVC_IO_ERR;
	        } finally {
	        	buffWriter.close();
	        }
			
			return returnValue;
		}
		
		/*  This function purpose is to fill a HashMap structure -
		 *  the key is the ip of each user and linked list contains  
		 *  the mapped numbers of every item the user was interested in. 
		 *  
		 *  Two rules are kept when filling the list:
		 *  	1. The linked list is sorted in ascending order. 
		 *  	2. Each item mapped number appear only once in the list.
		 * 
		 *  The motivation behind this is that the list is written later
		 *  directly into the file and the numbers in the transaction 
		 *  dataset need to be sorted and appear only once.  
		 */
		
		public int addItemEntryToMap(Map<String, List<Integer>> transactions, String ip, int item)
		{
			
			boolean mapEntryExists = false;
			boolean listEntryExists = false;
			
			for(Map.Entry<String, List<Integer>> entry : transactions.entrySet()) {
				
	 			  String key = entry.getKey();
	 			  
	 			  if(key.equalsIgnoreCase(ip)){
	 				  
	 				 for (Integer value : entry.getValue()) {
		 				 if(value == item)
		 					listEntryExists = true;
		 			 }
	 				 
	 				 if(listEntryExists == false) {
	 					transactions.get(key).add(item);
	 					Collections.sort(entry.getValue());
	 				 }
	 				 
	 				mapEntryExists = true;
	 			  }
	 		}
			
			if(mapEntryExists == false) {
				transactions.put(ip, new ArrayList<Integer>());
				transactions.get(ip).add(item);
			}
			
			return CSVC_OK;
		}
		
		/* This function gets the already filled HashMap structure
		 * and writes the info in a file that will be input file for
		 * charm/dCharm algorithms later.
		 * 
		 * * Parameters:
		 * 		* Map<String, List<Integer>> transactions -  the HashMap;
		 * 		* String fileName - the file name to write the converted
		 * 							data;  
		 * 
		 * Every new line in the transaction dataset represents all the 
		 * items for a specific user (already prepared in the linked list); 
		 */
		
		public int conctructTransactionDatasetFile(Map<String, List<Integer>> transactions, String fileName) throws IOException
		{
			
			StringBuilder output = new StringBuilder();
			int returnValue = CSVC_OK;
			
			for(Map.Entry<String, List<Integer>> entry : transactions.entrySet()) {
				
	 			  String key = entry.getKey();
	 			  	 			  
	 			 for (Integer value : entry.getValue()) {
	 				 output.append(value + " ");
	 			 }
	 			  
	 			 output.append(System.getProperty("line.separator"));
	 		}
		        
			returnValue = writeToFile(output, fileName);
		    
		    return returnValue;
		}
		
		/* This function maps an item (event/component) to specific number.
		 * 
		 * Parameters:
		 * 		* MapValues mapValue - enum value (EVENT or COMPONENT);
		 * 		* String[] items - this is the array with the splitted  
		 * 			values from each line read from the database file;
		 * Return Value:
		 * 		The function return the number for the given item;
		 */
		
		public int getMapValueNumber(MapValues mapValue, String[] items)
		{
			
			int mapValueNumber = 0;
			
			switch(mapValue) {
				case EVENT:
					mapValueNumber = convertEventToNumber(items[4]);
					break;
				case COMPONENT:
					mapValueNumber = convertComponentToNumber(items[3]);
					break;
				default:
					break;				
			}
			
			return  mapValueNumber;
		}
		
		/* The reverse functionality of the previous function.
		 * Returns the type of the item from the mapped number.
		 * 
		 * Parameters:
		 * 		* MapValues mapValue - enum value (EVENT or COMPONENT);
		 * 		* int itemNumber - the mapped number;
		 * 
		 * Return Value:
		 * 				The function return the name of the item 
		 * 				        represented by the given number;
		 */
		
		public String getMappedValue(MapValues mapValue, int itemNumber)
		{
			
			String mappedValue = EMPTY_STRING;
			
			switch(mapValue) {
				case EVENT:
					mappedValue = convertNumberToEvent(itemNumber);
					break;
				case COMPONENT:
					mappedValue = convertNumberToComponent(itemNumber);
					break;
				default:
					break;				
			}
			
			return  mappedValue;
		}
		
		public int convertInputByMapValue(Map<String, List<Integer>> transactions, String[] items, MapValues mapValue)
		{
			
			int mappedValue = 0;
			
			mappedValue = getMapValueNumber( mapValue, items);
             
            if(items.length == 8) {
            	addItemEntryToMap(transactions, items[7], mappedValue);
            } else {
            	addItemEntryToMap(transactions, "empty", mappedValue);
            }
            
           return CSVC_OK;
		}
		
		public int formatUserOutput(StringBuilder output, String mappedValue, int item)
		{
			
			if(mappedValue.equalsIgnoreCase("Not Found")) {
				
				 output.append("Support of " + item + " transactions for the itemset of:");
				 output.append(System.getProperty("line.separator"));
				 
			 } else {
				 
				 output.append(System.getProperty("line.separator"));
				 output.append("* " + mappedValue);
				 
			 }
			
			return CSVC_OK;
		}
		
		/* This is the main function that transforms database to transaction dataset.
		 * 
		 * Params:
		 * 		* String inputFile - the database file path;
		 * 		* MapValues mapValue - enum value (EVENT or COMPONENT);
		 * 		* String outputFile - transaction dataset file path;
		 * 
		 * Return Value:
		 * 				* CSVC_OK - If the conversion was successful;
		 * 				* CSVC_IO_ERR - If IO error occurs;
		 * 			* CSVC_FILE_NOT_FOUND - if the files is not found;
		 */
		
		public int convertToTransactionDataset(String inputFile, MapValues mapValue, String outputFile)
		{
			
			 Map<String, List<Integer>> transactions = new HashMap<String, List<Integer>>();
			 BufferedReader buffReader = null;
			 String fileLine = EMPTY_STRING;
			 int returnValue = CSVC_OK;
			 
		     try {
		
		    	 buffReader = new BufferedReader(new FileReader(inputFile));
		         
		         while ((fileLine = buffReader.readLine()) != null) {
		        	 
		             // use comma as separator
		             String[] items = fileLine.split(SPLIT_CVS_STRING);
		             convertInputByMapValue(transactions, items, mapValue); 
		         }
		         
		         returnValue = conctructTransactionDatasetFile(transactions, outputFile);
	             if(returnValue != CSVC_OK)
	            	 System.out.println("convertToTransactionDataset: Couldn't write converted database to file!");
	             
		     } catch (FileNotFoundException e) {
		    	 System.out.println("convertToTransactionDataset: Database file not found!");
				 returnValue = CSVC_FILE_NOT_FOUND;
		     } catch (IOException ioe) {
		    	 System.out.println("convertToTransactionDataset: Something went wrong!");
		    	 returnValue = CSVC_IO_ERR;
		     } finally {
		         if (buffReader != null) {
		             try {
		            	 buffReader.close();
		             } catch (IOException e) {
		            	 System.out.println("convertToTransactionDataset: Something went wrong!");
				    	 returnValue = CSVC_IO_ERR;
		             }
		         }
		     }
		     
		    return returnValue;
		}
		
		/* This function converts the result from charm/dCharm to 
		 * output appropriate for the user;
		 * 
		 * Params:
		 * 		* String resultFileName - the result of mining algorithm
		 * 								 file path;
		 * 		* MapValues mapValue - enum value (EVENT or COMPONENT);
		 * 		* String resultFileName - the translated result from
		 * 								the mining algorithm file path;
		 * 
		 * Return Value:
		 * 				* CSVC_OK - If the conversion was successful;
		 * 				* CSVC_IO_ERR - If IO error occurs;
		 * 			* CSVC_FILE_NOT_FOUND - if the files is not found;
		 */
		
		public int convertResultsForUsers(String resultFileName, MapValues mapValue, String formattedResult)
		{
			
			StringBuilder output = new StringBuilder();
			BufferedReader buffReader = null;
			String mappedValue = EMPTY_STRING;
			String fileLine = EMPTY_STRING;
			int index = 0;
			int returnValue = CSVC_OK;
			
			 try {
					
				 buffReader = new BufferedReader(new FileReader(resultFileName));
		         
		         while ((fileLine = buffReader.readLine()) != null) {	 
		        	 
		        	 String[] items = fileLine.split(SPLIT_BY_SPACE_STRING);
		        	 
		        	 for(index = items.length-1; index >= 0; index--) {
		        		 
		        		 if(items[index].equalsIgnoreCase(EMPTY_STRING)) {
		        			 continue;
		        		 }
		        		 
		        		 if(!items[index].contentEquals("#SUP:")) {
		        			 mappedValue = getMappedValue(mapValue, Integer.parseInt(items[index]));
		        			 formatUserOutput(output, mappedValue, Integer.parseInt(items[index]));
		        		 }
		    
		        	 }
		        	 
		        	 output.append(System.getProperty("line.separator") + System.getProperty("line.separator"));	 
		         }
		         
		         returnValue = writeToFile(output, formattedResult);
		         System.out.println(output);
		         
		         if(returnValue != CSVC_OK) {
		        	 System.out.println("convertResultsForUsers: Couldn't write the formatted result to file!");
		         } else {
		        	 System.out.println("convertResultsForUsers: Formatted result saved in " + formattedResult);
		         }
		         
			 } catch (FileNotFoundException e) {
				 System.out.println("convertResultsForUsers: Result file not found!");
				 returnValue = CSVC_FILE_NOT_FOUND;
		     } catch (IOException ioe) {
		    	 System.out.println("convertResultsForUsers: Something went wrong!");
		    	 returnValue = CSVC_IO_ERR;
		     } finally {
		         if (buffReader != null) {
		             try {
		                 buffReader.close();
		             } catch (IOException e) {
		            	 System.out.println("convertResultsForUsers: Something went wrong!");
				    	 returnValue = CSVC_IO_ERR;
		             }
		         }
		     }
			 
			 return returnValue;
		}
		
		public int convertComponentToNumber(String item)
		{
			
			int result = 0;
			
			switch(item) { 
			
	            case "Logs": 
	            	result = 1;
	                break; 
	            case "File": 
	            	result = 2;
	                break; 
	            case "Activity report":  
	            	result = 3;
	                break;
	            case "Live logs": 
	            	result = 4;
	                break; 
	            case "System": 
	            	result = 5;
	                break; 
	            case "Forum":  
	            	result = 6;
	                break;
	            case "User report": 
	            	result = 7;
	                break; 
	            case "Recycle bin": 
	            	result = 8;
	                break; 
	            case "Overview report":  
	            	result = 9;
	                break; 
	            case "Event name":  
	            	result = 10;
	                break;
	            case "Grader report": 
	            	result = 11;
	                break; 
	            case "Page": 
	            	result = 12;
	                break; 
	            default: 
	                break;
	        }
					
			return result;
		}
		
		public String convertNumberToComponent(int item)
		{
			
			String result = EMPTY_STRING;
			
			switch(item) { 
			
	            case 1: 
	            	result = "Logs";
	                break; 
	            case 2: 
	            	result = "File";
	                break; 
	            case 3:  
	            	result = "Activity report";
	                break;
	            case 4: 
	            	result = "Live logs";
	                break; 
	            case 5: 
	            	result = "System";
	                break; 
	            case 6:  
	            	result = "Forum";
	                break;
	            case 7: 
	            	result = "User report";
	                break; 
	            case 8: 
	            	result = "Recycle bin";
	                break; 
	            case 9:  
	            	result = "Overview report";
	                break; 
	            case 10:  
	            	result = "Event name";
	                break;
	            case 11: 
	            	result = "Grader report";
	                break; 
	            case 12: 
	            	result = "Page";
	                break; 
	            default: 
	            	result = "Not found";
	                break;
	        }
					
			return result;
		}
		
		
		public int convertEventToNumber(String item)
		{
			
			int result = 0;
			
			switch(item) { 
			
	            case "Log report viewed": 
	            	result = 1;
	                break; 
	            case "Course module viewed": 
	            	result = 2;
	                break; 
	            case "Activity report viewed": 
	            	result = 3;
	                break; 
	            case "Live log report viewed": 
	            	result = 4;
	                break;
	            case "Course viewed": 
	            	result = 5;
	                break; 
	            case "User unenrolled from course": 
	            	result = 6;
	                break; 
	            case "Role unassigned": 
	            	result = 7;
	                break; 
	            case "User report viewed": 
	            	result = 8;
	                break; 
	            case "Grade user report viewed": 
	            	result = 9;
	                break; 
	            case "Description": 
	            	result = 10;
	                break; 
	            case "User list viewed": 
	            	result = 11;
	                break; 
	            case "Course module updated": 
	            	result = 12;
	                break;
	            case "Role assigned": 
	            	result = 13;
	                break; 
	            case "Discussion viewed": 
	            	result = 14;
	                break;
	            case "User enrolled in course": 
	            	result = 15;
	                break;
	            case "Course module created": 
	            	result = 16;
	                break;
	            case "Recent activity viewed": 
	            	result = 17;
	                break;
	            case "Item deleted": 
	            	result = 18;
	                break;
	            case "User profile viewed": 
	            	result = 19;
	                break;
	            case "Course module deleted": 
	            	result = 20;
	                break;
	            case "Item created": 
	            	result = 21;
	                break;
	            case "Enrolment instance created": 
	            	result = 22;
	                break;
	            case "Grouping deleted": 
	            	result = 23;
	                break;
	            case "Course user report viewed": 
	            	result = 24;
	                break;
	            case "Grade overview report viewed": 
	            	result = 25;
	                break;
	            case "Calendar event created": 
	            	result = 26;
	            	break;
	            case "Grader report viewed": 
	            	result = 27;
	            	break;
	            case "Course searched": 
	            	result = 28;
	            	break;
	            case "Calendar event updated": 
	            	result = 29;
	            	break;
	            case "Discussion created": 
	            	result = 30;
	            	break;
	            case "Some content has been posted.": 
	            	result = 31;
	            	break;
	            case "Course module instance list viewed": 
	            	result = 32;
	            	break;
	            case "Course section updated": 
	            	result = 33;
	            	break;
	            default: 
	                break;
	        }
			
			return result;
		}
		
		
		public String convertNumberToEvent(int item)
		{
			
			String result = EMPTY_STRING;
			
			switch(item) {
			
	            case 1: 
	            	result = "Log report viewed";
	                break; 
	            case 2: 
	            	result = "Course module viewed";
	                break; 
	            case 3: 
	            	result = "Activity report viewed";
	                break; 
	            case 4: 
	            	result = "Live log report viewed";
	                break;
	            case 5: 
	            	result = "Course viewed";
	                break; 
	            case 6: 
	            	result = "User unenrolled from course";
	                break; 
	            case 7: 
	            	result = "Role unassigned";
	                break; 
	            case 8: 
	            	result = "User report viewed";
	                break; 
	            case 9: 
	            	result = "Grade user report viewed";
	                break; 
	            case 10: 
	            	result = "Description";
	                break; 
	            case 11: 
	            	result = "User list viewed";
	                break; 
	            case 12: 
	            	result = "Course module updated";
	                break;
	            case 13: 
	            	result = "Role assigned";
	                break; 
	            case 14: 
	            	result = "Discussion viewed";
	                break;
	            case 15: 
	            	result = "User enrolled in course";
	                break;
	            case 16: 
	            	result = "Course module created";
	                break;
	            case 17: 
	            	result = "Recent activity viewed";
	                break;
	            case 18: 
	            	result = "Item deleted";
	                break;
	            case 19: 
	            	result = "User profile viewed";
	                break;
	            case 20: 
	            	result = "Course module deleted";
	                break;
	            case 21: 
	            	result = "Item created";
	                break;
	            case 22: 
	            	result = "Enrolment instance created";
	                break;
	            case 23: 
	            	result = "Grouping deleted";
	                break;
	            case 24: 
	            	result = "Course user report viewed";
	                break;
	            case 25: 
	            	result = "Grade overview report viewed";
	                break;
	            case 26: 
	            	result = "Calendar event created";
	            	break;
	            case 27: 
	            	result = "Grader report viewed";
	            	break;
	            case 28: 
	            	result = "Course searched";
	            	break;
	            case 29: 
	            	result = "Calendar event updated";
	            	break;
	            case 30: 
	            	result = "Discussion created";
	            	break;
	            case 31: 
	            	result = "Some content has been posted.";
	            	break;
	            case 32: 
	            	result = "Course module instance list viewed";
	            	break;
	            case 33: 
	            	result = "Course section updated";
	            	break;
	            default: 
	            	result = "Not Found";
	                break;
	        }
			
			return result;
		}
		
		/* This method is takes the correct input from the user. 
		 *  
		 * Params:
		 * 		* String[] filesPaths - where all path entered from the user
		 * 					are collected and used in the invoking function;
		 * 
		 * Return Value: minsup entered from the user;
		 * 			
		 */
		
		public double askForInput(String[] filesPaths) throws IOException
		{
			
			 BufferedReader systemReader = new BufferedReader(new InputStreamReader(System.in)); 
			 boolean resultTxt = true;
			 boolean resultCsv = true;
			 
			 do {
				 System.out.println("Enter the file path where the input database is kept (Should be .txt or .csv file):");
				 filesPaths[0] = systemReader.readLine();
				 resultTxt = filesPaths[0].endsWith(".txt");
				 resultCsv = filesPaths[0].endsWith(".csv");
			 } while(!resultTxt && !resultCsv);
			 
			 do {
				 System.out.println("Enter the file path where the converted data will be kept (Should be .txt file):");
				 filesPaths[1] = systemReader.readLine();
			 } while(!filesPaths[1].endsWith(".txt"));
			 
			 do {
				 System.out.println("Enter the file path where the data mining result will be kept (Should be .txt file):");
				 filesPaths[2] = systemReader.readLine();
			 }while(!filesPaths[2].endsWith(".txt"));
			 
			 do {
				 System.out.println("Enter the file path where the formatted result will be kept (Should be .txt file):");
				 filesPaths[3] = systemReader.readLine();
			 }while(!filesPaths[3].endsWith(".txt"));
			 
			System.out.println("Enter the minsup value:");
			double minsup = Double.parseDouble(systemReader.readLine());
			
			return minsup;
		}
}
