package edu.tests.junit;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import edu.algorithms.converters.*;

public class JUnitTestCases {
	
	@Test
	public void CsvConverterJUnitTests()
	{
		String[] items = {"", "", "", "Live logs", "Course module updated"}; //simulation of line from the database
		String csvInput = "./logs_BCS37_20181103.csv";  // the database
		String inputConverted = "D://converted_input.txt";  //converted file
		String formattedOutput = "D://formatted_output.txt";  //formatted output file
		String output = "D://mining_output.txt"; // the result of mining
		int index = 0;
		
		CsvConverter csvc = new CsvConverter();
		
		for(index = 1; index <= 12; index++)
		{
			items[3] = csvc.getMappedValue(CsvConverter.MapValues.COMPONENT, index);
			assertEquals("Value number for COMPONENTS correct!", csvc.getMappedValue(CsvConverter.MapValues.COMPONENT, index), items[3]);
			assertEquals("Value number for COMPONENTS correct!", csvc.getMapValueNumber(CsvConverter.MapValues.COMPONENT, items), index);
		}
		
		for(index = 1; index <= 33; index++)
		{
			items[4] = csvc.getMappedValue(CsvConverter.MapValues.EVENT, index);
			assertEquals("Value number for EVENTS correct!", csvc.getMappedValue(CsvConverter.MapValues.EVENT, index), items[4]);
			assertEquals("Value number for EVENTS correct!", csvc.getMapValueNumber(CsvConverter.MapValues.EVENT, items), index);
		}
				
		assertEquals("Convertion successfull!", csvc.convertToTransactionDataset(csvInput, CsvConverter.MapValues.EVENT, inputConverted), 0);
		assertEquals("Result converted successfully!", csvc.convertResultsForUsers(output, CsvConverter.MapValues.EVENT, formattedOutput), 0);		
	}

}
