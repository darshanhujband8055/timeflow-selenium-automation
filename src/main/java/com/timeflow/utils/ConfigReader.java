package com.timeflow.utils;

import com.timeflow.constants.FrameworkConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(FrameworkConstants.CONFIG_FILE_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties from path: " 
                    + FrameworkConstants.CONFIG_FILE_PATH, e);
        }
    }

    private ConfigReader() {
        // Prevent instantiation
    }

    public static String getProperty(String key) {
        // First check system properties (CLI overrides e.g. -Dbrowser=firefox)
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.trim().isEmpty()) {
            return systemProp.trim();
        }
        return properties.getProperty(key);
    }

    public static String getBrowser() {
        String browser = getProperty("browser");
        return browser != null ? browser.trim().toLowerCase() : "chrome";
    }

    public static String getBaseUrl() {
        String url = getProperty("url");
        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException("Application URL is not configured in config.properties!");
        }
        return url.trim();
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless"));
    }

    public static int getTimeout() {
        String timeout = getProperty("timeout");
        return timeout != null ? Integer.parseInt(timeout.trim()) : 10;
    }

    public static boolean shouldMaximize() {
        String maximize = getProperty("maximize");
        return maximize == null || Boolean.parseBoolean(maximize.trim());
    }
}
