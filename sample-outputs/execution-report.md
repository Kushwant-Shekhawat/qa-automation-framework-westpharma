# Dummy Pharma Company - Automation Execution Report & Scripts Guide

This document captures the test runner reports, terminal outputs, and lists the Playwright automation scripts for verification under the Dummy Pharma Company domain.

---

## 💻 Playwright Scripts & Code References

The framework is built using Playwright Java, modularized into Page Object Models (POM), Step Definitions, and API Clients:

1. **Browser Driver Manager**:
   * [PlaywrightDriver.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/main/java/com/qa/driver/PlaywrightDriver.java): Configures and manages the thread-local Playwright browser instances (`chromium`, `firefox`, `webkit`).
2. **Page Objects (POM)**:
   * [LoginPage.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/main/java/com/qa/pages/LoginPage.java): Encapsulates selectors and interactions for email, password inputs, toggle show button, and login submissions.
   * [SearchPage.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/main/java/com/qa/pages/SearchPage.java): Handles search keyword fields, drug category filters, and formula listing card elements.
   * [ChatPage.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/main/java/com/qa/pages/ChatPage.java): Manages the AI research assistant settings and chat bubbles elements.
3. **API Client**:
   * [APIClient.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/main/java/com/qa/api/APIClient.java): Playwright `APIRequestContext` client managing headers, authentication cookies, login, search, and chat endpoint requests.
4. **Step Definitions (Glue Code)**:
   * [LoginSteps.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/test/java/com/qa/stepdefs/LoginSteps.java)
   * [SearchSteps.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/test/java/com/qa/stepdefs/SearchSteps.java)
   * [ChatSteps.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/test/java/com/qa/stepdefs/ChatSteps.java)
   * [ApiSteps.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/test/java/com/qa/stepdefs/ApiSteps.java)
   * [AIEvalSteps.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/test/java/com/qa/stepdefs/AIEvalSteps.java)
   * [Hooks.java](file:///Users/kushwantsinghshekhawat/qa-automation-framework/src/test/java/com/qa/stepdefs/Hooks.java)

---

## 📊 Test Execution Summary

The automation suite was run locally against the mock Node.js/Express application.

### Maven Console Run Output
```text
PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 mvn clean test

[INFO] Scanning for projects...
[INFO] ----------------< com.qa:qa-automation-framework-westpharma >-----------------
[INFO] Building qa-automation-framework-westpharma 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
...
[INFO] --- maven-surefire-plugin:3.2.5:test (default-test) @ qa-automation-framework-westpharma ---
...
Running TestSuite

@LoginFeature @HighRisk @Smoke
Scenario: Successful pharmacist login with valid credentials # src/test/resources/features/login.feature:11
[HOOKS] Starting browser for UI test: Successful pharmacist login with valid credentials
  Given the pharmacist is on the login page                  # com.qa.stepdefs.LoginSteps.the_pharmacist_is_on_the_login_page()
  When the pharmacist enters email "pharmacist@dummypharma.com" and password "SecurePass123" # com.qa.stepdefs.LoginSteps.the_pharmacist_enters_email_and_password(java.lang.String,java.lang.String)
  And clicks the login button                            # com.qa.stepdefs.LoginSteps.clicks_the_login_button()
  Then the pharmacist should be redirected to the dashboard page # com.qa.stepdefs.LoginSteps.the_pharmacist_should_be_redirected_to_the_dashboard_page()
  And the welcome message should display "Welcome, Dr. Jane Doe" # com.qa.stepdefs.LoginSteps.the_welcome_message_should_display(java.lang.String)

    Embedding Screenshot - Status: passed [image/png 197646 bytes]

[HOOKS] Screenshot captured for scenario: Successful pharmacist login with valid credentials (passed)
[HOOKS] Closing browser for UI test: Successful pharmacist login with valid credentials

...

@SearchFeature @LowRisk
Scenario: Search with no matching results displays empty state             # src/test/resources/features/search.feature:36
[HOOKS] Starting browser for UI test: Search with no matching results displays empty state
  Given the pharmacist is logged in and on the dashboard page              # com.qa.stepdefs.SearchSteps.the_pharmacist_is_logged_in_and_on_the_dashboard_page()
  When the pharmacist searches for keyword "UnknownFormulaMysteryCompound" # com.qa.stepdefs.SearchSteps.the_pharmacist_searches_for_keyword(java.lang.String)
  Then an empty state message should be displayed                          # com.qa.stepdefs.SearchSteps.an_empty_state_message_should_be_displayed()
  And the results count should be 0                                        # com.qa.stepdefs.SearchSteps.the_results_count_should_be(int)

    Embedding Screenshot - Status: passed [image/png 180955 bytes]

[HOOKS] Screenshot captured for scenario: Search with no matching results displays empty state (passed)
[HOOKS] Closing browser for UI test: Search with no matching results displays empty state

[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 97.72 s -- in TestSuite
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:38 min
[INFO] Finished at: 2026-06-17T14:26:06+01:00
[INFO] ------------------------------------------------------------------------
```

### 📋 Key Metrics

| Metric | Value | Details |
| :--- | :--- | :--- |
| **Total Scenarios Executed** | **29** | Covers Pharmacist Login (UI/API), Formula Search & Filter (UI/API), Chemical Chat (UI/API), and AI Evaluation. |
| **Passed** | **29** | 100% Pass rate. |
| **Failed** | **0** | No failures observed. |
| **Skipped** | **0** | No skipped scenarios. |
| **Execution Duration** | **97.72 seconds** | Serial browser run (can be parallelized in testng.xml). |

### 📂 Execution Reports Formats

Upon completion, Maven outputs the reports in three formats:
1. **Cucumber HTML Report**: Located at [target/cucumber-reports/cucumber.html](file:///Users/kushwantsinghshekhawat/qa-automation-framework/target/cucumber-reports/cucumber.html) which includes step-by-step statistics, tags, timings, and screenshots embedded directly into all steps (passed and failed).
2. **Cucumber JSON Report**: Located at `target/cucumber-reports/cucumber.json` for integration with CI dashboards (e.g., Jenkins Cucumber Reports plugin).
3. **Extent HTML Spark Report**: Located at [target/extent-reports/extent-report.html](file:///Users/kushwantsinghshekhawat/qa-automation-framework/target/extent-reports/extent-report.html) which provides a dashboard-driven visual interface featuring inline base64-encoded screenshots for all UI/Chat test cases.
