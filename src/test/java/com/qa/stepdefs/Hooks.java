package com.qa.stepdefs;

import com.microsoft.playwright.Page;
import com.qa.driver.PlaywrightDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Paths;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        // Only initialize browser driver for non-API tests (UI and Chat tests)
        if (!scenario.getSourceTagNames().contains("@API")) {
            System.out.println("[HOOKS] Starting browser for UI test: " + scenario.getName());
            PlaywrightDriver.initDriver();
        } else {
            System.out.println("[HOOKS] Skipping browser initialization for API test: " + scenario.getName());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        if (!scenario.getSourceTagNames().contains("@API")) {
            Page page = PlaywrightDriver.getPage();
            if (page != null) {
                // Capture screenshot for the report
                String status = scenario.isFailed() ? "failed" : "passed";
                String screenshotName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_" + status + ".png";
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                        .setPath(Paths.get("target/screenshots/" + screenshotName)));
                scenario.attach(screenshot, "image/png", "Screenshot - Status: " + status);
                System.out.println("[HOOKS] Screenshot captured for scenario: " + scenario.getName() + " (" + status + ")");
                
                System.out.println("[HOOKS] Closing browser for UI test: " + scenario.getName());
            }
            PlaywrightDriver.quitDriver();
        }
    }
}
