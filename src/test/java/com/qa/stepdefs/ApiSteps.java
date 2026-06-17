package com.qa.stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.qa.api.APIClient;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.io.IOException;

public class ApiSteps {
    private APIClient apiClient;
    private APIResponse lastResponse;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Before("@API")
    public void initApiClient() {
        apiClient = new APIClient();
    }

    @After("@API")
    public void disposeApiClient() {
        if (apiClient != null) {
            apiClient.close();
        }
    }

    @Given("the API client is not authenticated")
    public void the_api_client_is_not_authenticated() {
        apiClient.clearAuthToken();
    }

    @Given("the API client is authenticated with token {string}")
    public void the_api_client_is_authenticated_with_token(String token) {
        apiClient.setAuthToken(token);
    }

    @When("the API request is sent to POST {string} with email {string} and password {string}")
    public void the_api_request_is_sent_to_post_with_email_and_password(String endpoint, String email, String password) {
        // Since we only have /api/login for login post, check endpoint
        if (endpoint.equals("/api/login")) {
            lastResponse = apiClient.postLogin(
                email.isEmpty() ? null : email, 
                password.isEmpty() ? null : password
            );
        }
    }

    @Then("the API response status should be {int}")
    public void the_api_response_status_should_be(int expectedStatus) {
        Assert.assertEquals(lastResponse.status(), expectedStatus, "API response status mismatch!");
    }

    @Then("the response body should contain success true")
    public void the_response_body_should_contain_success_true() throws IOException {
        JsonNode json = objectMapper.readTree(lastResponse.text());
        Assert.assertTrue(json.path("success").asBoolean(), "API response success flag is not true!");
    }

    @Then("the response should include a valid auth token {string}")
    public void the_response_should_include_a_valid_auth_token(String expectedToken) throws IOException {
        JsonNode json = objectMapper.readTree(lastResponse.text());
        String token = json.path("token").asText();
        Assert.assertEquals(token, expectedToken, "Returned auth token was mismatch!");
    }

    @Then("the response body success should be false")
    public void the_response_body_success_should_be_false() throws IOException {
        JsonNode json = objectMapper.readTree(lastResponse.text());
        Assert.assertFalse(json.path("success").asBoolean(), "API response success flag is not false!");
    }

    @Then("the response error message should be {string}")
    public void the_response_error_message_should_be(String expectedError) throws IOException {
        JsonNode json = objectMapper.readTree(lastResponse.text());
        Assert.assertEquals(json.path("error").asText(), expectedError);
    }

    @When("the API request is sent to GET {string} with query {string}")
    public void the_api_request_is_sent_to_get_with_query(String endpoint, String query) {
        if (endpoint.equals("/api/search")) {
            lastResponse = apiClient.getSearch(query, null);
        }
    }

    @Then("the response error message should contain {string}")
    public void the_response_error_message_should_contain(String expectedSubtext) throws IOException {
        JsonNode json = objectMapper.readTree(lastResponse.text());
        String errorMsg = json.path("error").asText();
        Assert.assertTrue(errorMsg.contains(expectedSubtext), 
                "Error message '" + errorMsg + "' did not contain '" + expectedSubtext + "'!");
    }

    @When("the API request is sent to GET {string} with query {string} and category {string}")
    public void the_api_request_is_sent_to_get_with_query_and_category(String endpoint, String query, String category) {
        if (endpoint.equals("/api/search")) {
            lastResponse = apiClient.getSearch(query, category);
        }
    }

    @Then("the response results count should be {int}")
    public void the_response_results_count_should_be(int expectedCount) throws IOException {
        JsonNode json = objectMapper.readTree(lastResponse.text());
        Assert.assertEquals(json.path("resultsCount").asInt(), expectedCount);
    }

    @Then("the results should contain {string}")
    public void the_results_should_contain(String expectedFormulaName) throws IOException {
        JsonNode json = objectMapper.readTree(lastResponse.text());
        JsonNode results = json.path("results");
        boolean found = false;
        for (JsonNode node : results) {
            if (node.path("name").asText().equals(expectedFormulaName)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found, "Results list did not contain formula '" + expectedFormulaName + "'!");
    }
}
