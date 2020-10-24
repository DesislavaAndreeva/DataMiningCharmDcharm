package edu.tests.junit;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JUnitRunnerTests {
	
	@Test
	public void main()
	{
		Result result = JUnitCore.runClasses(JUnitTestCases.class);
		
		for (Failure failure : result.getFailures()) {
		      System.out.println(failure.toString());
		}
	}

}
