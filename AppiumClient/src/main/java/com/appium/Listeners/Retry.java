package com.appium.Listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
	int retrycount = 0;
	int maxretyrcount = 1;

	public boolean retry(ITestResult result) {
		if (retrycount < maxretyrcount) {
			System.out.println("Retrying test " + result.getName() + " with status "
					+ getResultStatusName(result.getStatus()) + " for the " + (retrycount + 1) + " time(s).");
			result.setStatus(ITestResult.FAILURE); // Mark test as failed
			// extendReportsFailOperations(result);
			retrycount++;
			return true;
		}
		return false;
	}

	public String getResultStatusName(int status) {
		String resultName = null;
		if (status == 1)
			resultName = "SUCCESS";
		if (status == 2)
			resultName = "FAILURE";
		if (status == 3)
			resultName = "SKIP";
		return resultName;

	}

}
