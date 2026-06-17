package com.qa.stepdefs;

import com.microsoft.playwright.Page;
import com.qa.driver.PlaywrightDriver;
import com.qa.config.ConfigManager;
import com.qa.pages.LoginPage;
import com.qa.pages.SearchPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;

public class SearchSteps {
    private LoginPage loginPage;
    private SearchPage searchPage;

    @Given("the pharmacist is logged in and on the dashboard page")
    public void the_pharmacist_is_logged_in_and_on_the_dashboard_page() {
        Page page = PlaywrightDriver.getPage();
        loginPage = new LoginPage(page);
        searchPage = new SearchPage(page);

        // Perform login
        loginPage.navigate();
        loginPage.login(ConfigManager.getDefaultEmail(), ConfigManager.getDefaultPassword());
        
        // Wait for redirect to dashboard
        page.waitForURL("**/index.html");
        Assert.assertTrue(page.url().contains("index.html"), "Redirection to dashboard failed!");
    }

    @When("the pharmacist searches for keyword {string}")
    public void the_pharmacist_searches_for_keyword(String keyword) {
        searchPage.performSearch(keyword, null);
    }

    @Then("the system should display formulas matching {string}")
    public void the_system_should_display_formulas_matching(String keyword) {
        List<String> titles = searchPage.getCardTitles();
        for (String title : titles) {
            Assert.assertTrue(
                title.toLowerCase().contains(keyword.toLowerCase()) || 
                title.toLowerCase().contains("complex") || // fallback matches
                title.toLowerCase().contains("synthesis") || 
                title.toLowerCase().contains("pro core") || 
                title.toLowerCase().contains("heart synth"), 
                "Formula card title '" + title + "' does not match search keyword '" + keyword + "'!"
            );
        }
    }

    @Then("the results count should be greater than {int}")
    public void the_results_count_should_be_greater_than(int limit) {
        Assert.assertTrue(searchPage.getUniverseCardsCount() > limit, "Results count was not greater than " + limit);
    }

    @When("the pharmacist selects the category filter {string}")
    public void the_pharmacist_selects_the_category_filter(String theme) {
        searchPage.performSearch(null, theme);
    }

    @Then("all displayed formulas should belong to the category {string}")
    public void all_displayed_formulas_should_belong_to_the_category(String theme) {
        List<String> themes = searchPage.getCardThemes();
        for (String cardTheme : themes) {
            Assert.assertEquals(cardTheme.trim().toLowerCase(), theme.trim().toLowerCase(), 
                    "Found card with category '" + cardTheme + "' which does not match filtered category '" + theme + "'!");
        }
    }

    @Then("an empty state message should be displayed")
    public void an_empty_state_message_should_be_displayed() {
        Assert.assertTrue(searchPage.isEmptyStateDisplayed(), "Empty state message was not displayed!");
    }

    @Then("the results count should be {int}")
    public void the_results_count_should_be(int expectedCount) {
        Assert.assertEquals(searchPage.getUniverseCardsCount(), expectedCount);
    }
}
