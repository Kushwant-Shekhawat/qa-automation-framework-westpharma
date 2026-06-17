package com.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Warning: config.properties not found, using default configurations.");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            System.err.println("Error loading config.properties file: " + ex.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getBrowser() {
        return getProperty("browser", "chromium");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "true"));
    }

    public static String getBaseUrl() {
        return getProperty("baseUrl", "http://localhost:3001");
    }

    public static String getApiBaseUrl() {
        return getProperty("apiBaseUrl", "http://localhost:3001/api");
    }

    public static String getDefaultEmail() {
        return getProperty("defaultEmail", "pharmacist@dummypharma.com");
    }

    public static String getDefaultPassword() {
        return getProperty("defaultPassword", "SecurePass123");
    }

    public static String getAnthropicKey() {
        return getProperty("anthropicKey", "mock-anthropic-key");
    }
}
