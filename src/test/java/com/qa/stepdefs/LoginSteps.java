package com.qa.stepdefs;
 
import com.microsoft.playwright.Page;
import com.qa.driver.PlaywrightDriver;
import com.qa.pages.LoginPage;
import com.qa.pages.SearchPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
 
public class LoginSteps {
    private LoginPage loginPage;
    private SearchPage searchPage;
 
    @Given("the pharmacist is on the login page")
    public void the_pharmacist_is_on_the_login_page() {
        Page page = PlaywrightDriver.getPage();
        loginPage = new LoginPage(page);
        loginPage.navigate();
    }
 
    @When("the pharmacist enters email {string} and password {string}")
    public void the_pharmacist_enters_email_and_password(String email, String password) {
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
    }
 
    @When("clicks the login button")
    public void clicks_the_login_button() {
        loginPage.clickLogin();
    }
 
    @Then("the pharmacist should be redirected to the dashboard page")
    public void the_pharmacist_should_be_redirected_to_the_dashboard_page() {
        Page page = PlaywrightDriver.getPage();
        searchPage = new SearchPage(page);
        // Wait for redirect to index.html (url matches /index.html)
        page.waitForURL("**/index.html");
        Assert.assertTrue(page.url().contains("index.html"), "Pharmacist was not redirected to the dashboard!");
    }
 
    @Then("the welcome message should display {string}")
    public void the_welcome_message_should_display(String expectedMsg) {
        Page page = PlaywrightDriver.getPage();
        // Since welcome message might take a moment to load from storage
        page.waitForSelector("#userName");
        String fullWelcomeText = page.textContent(".welcome-msg").trim();
        Assert.assertTrue(fullWelcomeText.contains("Dr. Jane Doe"), "Welcome message was: '" + fullWelcomeText + "'");
    }
 
    @Then("a login error message should be displayed indicating {string}")
    public void a_login_error_message_should_be_displayed_indicating(String expectedError) {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed!");
        Assert.assertEquals(loginPage.getErrorMessage().trim(), expectedError);
    }
 
    @When("the pharmacist enters password {string}")
    public void the_pharmacist_enters_password(String password) {
        loginPage.enterPassword(password);
    }
 
    @Then("the password field should mask the input text")
    public void the_password_field_should_mask_the_input_text() {
        Assert.assertEquals(loginPage.getPasswordInputType(), "password", "Password field was not masked!");
    }
 
    @When("the pharmacist clicks the password toggle show button")
    public void the_pharmacist_clicks_the_password_toggle_show_button() {
        loginPage.togglePasswordVisibility();
    }
 
    @Then("the password field should reveal the input text")
    public void the_password_field_should_reveal_the_input_text() {
        Assert.assertEquals(loginPage.getPasswordInputType(), "text", "Password field was not revealed!");
    }
 
    @Then("the toggle button label should change to {string}")
    public void the_toggle_button_label_should_change_to(String expectedLabel) {
        Assert.assertEquals(loginPage.getTogglePasswordText().trim(), expectedLabel);
    }
}
