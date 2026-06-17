package com.qa.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import com.qa.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

public class APIClient {
    private final Playwright playwright;
    private final APIRequestContext requestContext;
    private String authToken;

    public APIClient() {
        this.playwright = Playwright.create();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        this.requestContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(ConfigManager.getProperty("baseUrl"))
                .setExtraHTTPHeaders(headers));
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public void clearAuthToken() {
        this.authToken = null;
    }

    private RequestOptions createOptionsWithAuth(Object body) {
        RequestOptions options = RequestOptions.create();
        if (body != null) {
            options.setData(body);
        }
        if (authToken != null) {
            options.setHeader("Authorization", "Bearer " + authToken);
        }
        return options;
    }

    public APIResponse postLogin(String email, String password) {
        Map<String, String> body = new HashMap<>();
        if (email != null) body.put("email", email);
        if (password != null) body.put("password", password);

        return requestContext.post("/api/login", RequestOptions.create().setData(body));
    }

    public APIResponse getSearch(String query, String theme) {
        RequestOptions options = createOptionsWithAuth(null);
        if (query != null) {
            options.setQueryParam("q", query);
        }
        if (theme != null) {
            options.setQueryParam("theme", theme);
        }
        return requestContext.get("/api/search", options);
    }

    public APIResponse postChat(String message, Map<String, Object> context) {
        Map<String, Object> body = new HashMap<>();
        if (message != null) body.put("message", message);
        if (context != null) body.put("context", context);

        return requestContext.post("/api/chat", createOptionsWithAuth(body));
    }

    public void close() {
        if (requestContext != null) {
            requestContext.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
