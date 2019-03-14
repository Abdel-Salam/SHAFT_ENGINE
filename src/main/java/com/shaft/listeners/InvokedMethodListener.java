package com.shaft.listeners;

import org.testng.Assert;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.shaft.browser.BrowserFactory;
import com.shaft.element.ElementActions;
import com.shaft.io.ReportManager;
import com.shaft.video.RecordManager;

public class InvokedMethodListener implements IInvokedMethodListener {
    private int invokedTestsCounter = 0;
    private int testSize = 0;

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
	if (!method.isConfigurationMethod()) {
	    try {
		// testSize where the structure is testSuite > test > testClasses > testMethods
		testSize = testResult.getTestContext().getAllTestMethods().length;
	    } catch (NullPointerException e) {
		// this is thrown if there is no test context for some reason...
		ReportManager.log(e);
	    }
	    ITestNGMethod testMethod = method.getTestMethod();
	    if (testMethod.isTest()) {
		if (testMethod.getDescription() != null) {
		    ReportManager.logTestInformation(testMethod.getTestClass().getName(), testMethod.getMethodName(),
			    testMethod.getDescription());
		} else {
		    ReportManager.logTestInformation(testMethod.getTestClass().getName(), testMethod.getMethodName(),
			    "");
		}

		if (invokedTestsCounter == 0) {
		    RecordManager.startRecording();
		}
	    }
	}
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
	if (!method.isConfigurationMethod()) {
	    ITestNGMethod testMethod = method.getTestMethod();
	    if (testMethod.isTest()) {
		if (invokedTestsCounter == testSize - 1) {
		    // is last test in the class
		    RecordManager.stopRecording();
		    RecordManager.attachRecording();
		    BrowserFactory.attachBrowserLogs();
		    ReportManager.logEngineVersion(false);
		    ReportManager.attachFullLog();
		    invokedTestsCounter = 0;
		} else {
		    invokedTestsCounter++;
		}

		if (ReportManager.getTestCasesCounter() == ReportManager.getTotalNumberOfTests()) {
		    // is the last test in the suite
		    ReportManager.generateAllureReportArchive();
		}
		updateTestStatusInCaseOfVerificationFailure(testResult);
	    }
	}

	// resetting scope and config
	ElementActions.switchToDefaultContent();
	ReportManager.setDiscreteLogging(Boolean.valueOf(System.getProperty("alwaysLogDiscreetly")));

	// attaching log and gif
	BrowserFactory.attachAnimatedGif();
	ReportManager.attachTestLog();
    }

    private void updateTestStatusInCaseOfVerificationFailure(ITestResult testResult) {
	if (testResult != null && testResult.getStatus() == ITestResult.FAILURE && testResult.getThrowable() != null) {
	    Assert.fail(testResult.getThrowable().getMessage());
	}
    }
}
