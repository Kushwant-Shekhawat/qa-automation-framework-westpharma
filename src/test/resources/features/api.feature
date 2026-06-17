@APIFeature
Feature: Dummy Pharma Company REST API Integration
  As an API consumer
  I want to verify the REST API endpoints
  So that I can ensure schema compliance, security, and robust error handling

  @HighRisk @API
  Scenario: Login API returns valid authentication token on success
    When the API request is sent to POST "/api/login" with email "pharmacist@dummypharma.com" and password "SecurePass123"
    Then the API response status should be 200
    And the response body should contain success true
    And the response should include a valid auth token "mock-jwt-token-12345"

  @HighRisk @API
  Scenario Outline: Login API negative credentials and validations
    When the API request is sent to POST "/api/login" with email "<email>" and password "<password>"
    Then the API response status should be <status>
    And the response body success should be false
    And the response error message should be "<error>"

    Examples:
      | email                      | password      | status | error                      |
      | pharmacist@dummypharma.com | wrong_pass    | 401    | Invalid email or password. |
      | invalid_format             | SecurePass123 | 400    | Invalid email format.      |
      |                            | SecurePass123 | 400    | Email and password are required. |

  @MediumRisk @API
  Scenario: Search API requires valid bearer token authorization
    Given the API client is not authenticated
    When the API request is sent to GET "/api/search" with query "Aspirin"
    Then the API response status should be 401
    And the response error message should contain "Unauthorized"

  @MediumRisk @API
  Scenario: Search API returns matching results when authenticated
    Given the API client is authenticated with token "mock-jwt-token-12345"
    When the API request is sent to GET "/api/search" with query "Amoxicillin" and category "Antibiotics"
    Then the API response status should be 200
    And the response results count should be 1
    And the results should contain "Amoxicillin Premium Synthesis"

  @HighRisk @API
  Scenario: Search API handles database query error gracefully
    Given the API client is authenticated with token "mock-jwt-token-12345"
    When the API request is sent to GET "/api/search" with query "trigger-error"
    Then the API response status should be 500
    And the response error message should be "Internal database query error occurred."

  @MediumRisk @API
  Scenario: Search API handles empty query by returning all formulas
    Given the API client is authenticated with token "mock-jwt-token-12345"
    When the API request is sent to GET "/api/search" with query ""
    Then the API response status should be 200
    And the response results count should be 4

  @MediumRisk @API
  Scenario: Search API handles long query strings successfully
    Given the API client is authenticated with token "mock-jwt-token-12345"
    When the API request is sent to GET "/api/search" with query "ThisIsAnExtremelyLongQueryStringThatWillNeverMatchAnyFormulaInTheDatabaseSoWeExpectZeroResultsBack"
    Then the API response status should be 200
    And the response results count should be 0

  @MediumRisk @API
  Scenario: Search API handles special characters query successfully
    Given the API client is authenticated with token "mock-jwt-token-12345"
    When the API request is sent to GET "/api/search" with query "@#$%"
    Then the API response status should be 200
    And the response results count should be 0
