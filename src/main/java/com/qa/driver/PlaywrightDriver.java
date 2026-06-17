package com.qa.driver;

import com.microsoft.playwright.*;
import com.qa.config.ConfigManager;

public class PlaywrightDriver {
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    public static Page getPage() {
        if (pageThreadLocal.get() == null) {
            initDriver();
        }
        return pageThreadLocal.get();
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    public static void initDriver() {
        Playwright playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);

        String browserType = ConfigManager.getBrowser().toLowerCase();
        boolean headless = ConfigManager.isHeadless();

        Browser browser;
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setChannel("chrome");

        switch (browserType) {
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            case "chromium":
            default:
                browser = playwright.chromium().launch(launchOptions);
                break;
        }

        browserThreadLocal.set(browser);

        // Setup browser context
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 800));
        contextThreadLocal.set(context);

        // Setup page
        Page page = context.newPage();
        pageThreadLocal.set(page);
    }

    public static void quitDriver() {
        if (pageThreadLocal.get() != null) {
            pageThreadLocal.get().close();
            pageThreadLocal.remove();
        }
        if (contextThreadLocal.get() != null) {
            contextThreadLocal.get().close();
            contextThreadLocal.remove();
        }
        if (browserThreadLocal.get() != null) {
            browserThreadLocal.get().close();
            browserThreadLocal.remove();
        }
        if (playwrightThreadLocal.get() != null) {
            playwrightThreadLocal.get().close();
            playwrightThreadLocal.remove();
        }
    }
}
