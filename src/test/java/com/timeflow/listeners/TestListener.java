package com.timeflow.listeners;

import com.timeflow.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("==================================================");
        System.out.println("Starting Test Suite: " + context.getName());
        System.out.println("==================================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("==================================================");
        System.out.println("Completed Test Suite: " + context.getName());
        System.out.println("Passed: " + context.getPassedTests().size() 
                + ", Failed: " + context.getFailedTests().size() 
                + ", Skipped: " + context.getSkippedTests().size());
        System.out.println("==================================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[TEST STARTING] -> " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TEST PASSED]   -> " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.err.println("[TEST FAILED]   -> " + testName + " | Reason: " + result.getThrowable().getMessage());
        
        // Automatic screenshot capture on failure
        String screenshotPath = ScreenshotUtils.captureScreenshot(testName);
        if (screenshotPath != null) {
            System.out.println("Screenshot captured at: " + screenshotPath);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TEST SKIPPED]  -> " + result.getMethod().getMethodName());
    }
}
