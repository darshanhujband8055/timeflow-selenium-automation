package com.timeflow.utils;

import com.timeflow.constants.FrameworkConstants;
import com.timeflow.driver.DriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ScreenshotUtils {

    private ScreenshotUtils() {
        // Prevent instantiation
    }

    public static String captureScreenshot(String testName) {
        if (DriverManager.getDriver() == null) {
            return null;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        String destinationPath = FrameworkConstants.SCREENSHOTS_PATH + File.separator + fileName;

        File sourceFile = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
        File destinationFile = new File(destinationPath);

        try {
            FileUtils.copyFile(sourceFile, destinationFile);
            return destinationPath;
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }

    public static String getBase64Screenshot() {
        if (DriverManager.getDriver() == null) {
            return "";
        }
        return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
    }
}
