package com.qa.pages;

import com.microsoft.playwright.Page;
import com.qa.config.ConfigManager;

public class LoginPage {
    private final Page page;

    // Locators as CSS Selectors
    private final String emailInput = "#email";
    private final String passwordInput = "#password";
    private final String togglePasswordBtn = "#togglePassword";
    private final String loginBtn = "#loginBtn";
    private final String messageBox = "#messageBox";

    public LoginPage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate(ConfigManager.getBaseUrl() + "/login.html");
    }

    public void enterEmail(String email) {
        page.fill(emailInput, email);
    }

    public void enterPassword(String password) {
        page.fill(passwordInput, password);
    }

    public void clickLogin() {
        page.click(loginBtn);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    public void togglePasswordVisibility() {
        page.click(togglePasswordBtn);
    }

    public String getPasswordInputType() {
        return page.getAttribute(passwordInput, "type");
    }

    public String getTogglePasswordText() {
        return page.textContent(togglePasswordBtn);
    }

    public String getErrorMessage() {
        return page.textContent(messageBox);
    }

    public boolean isErrorMessageDisplayed() {
        try {
            page.waitForSelector(messageBox, new Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(3000));
            String className = page.getAttribute(messageBox, "class");
            return className != null && className.contains("error");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            page.waitForSelector(messageBox, new Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(3000));
            String className = page.getAttribute(messageBox, "class");
            return className != null && className.contains("success");
        } catch (Exception e) {
            return false;
        }
    }
}
