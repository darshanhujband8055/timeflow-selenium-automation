package com.timeflow.constants;

import java.io.File;
import java.time.Duration;

public final class FrameworkConstants {

    private FrameworkConstants() {
        // Prevent instantiation
    }

    public static final String CONFIG_FILE_PATH = System.getProperty("user.dir") 
            + File.separator + "src" 
            + File.separator + "main" 
            + File.separator + "resources" 
            + File.separator + "config.properties";

    public static final String SCREENSHOTS_PATH = System.getProperty("user.dir") 
            + File.separator + "target" 
            + File.separator + "screenshots";

    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
}
