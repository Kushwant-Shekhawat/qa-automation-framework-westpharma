package com.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qa.config.ConfigManager;

import java.util.List;
import java.util.stream.Collectors;

public class SearchPage {
    private final Page page;

    // Locators as CSS Selectors
    private final String searchInput = "#searchInput";
    private final String themeSelect = "#themeSelect";
    private final String searchBtn = "#searchBtn";
    private final String universeCards = ".universe-card";
    private final String cardTitles = ".universe-card .card-title";
    private final String cardBadges = ".universe-card .card-badge";
    private final String loadingState = "#loadingState";
    private final String errorState = "#errorState";
    private final String emptyState = "#emptyState";
    private final String logoutBtn = "#logoutBtn";

    public SearchPage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate(ConfigManager.getBaseUrl() + "/index.html");
    }

    public void enterSearchKeyword(String keyword) {
        page.fill(searchInput, keyword);
    }

    public void selectTheme(String theme) {
        page.selectOption(themeSelect, theme);
    }

    public void clickSearch() {
        page.click(searchBtn);
    }

    public void performSearch(String keyword, String theme) {
        if (keyword != null) {
            enterSearchKeyword(keyword);
        }
        if (theme != null) {
            selectTheme(theme);
        }
        clickSearch();
    }

    public int getUniverseCardsCount() {
        page.waitForSelector(".universe-card:visible, #emptyState:visible, #errorState:visible", new Page.WaitForSelectorOptions().setTimeout(5000));
        return page.locator(universeCards).count();
    }

    public List<String> getCardTitles() {
        return page.locator(cardTitles).allTextContents();
    }

    public List<String> getCardThemes() {
        return page.locator(cardBadges).allTextContents();
    }

    public boolean isLoadingStateDisplayed() {
        return page.isVisible(loadingState);
    }

    public boolean isErrorStateDisplayed() {
        return page.isVisible(errorState);
    }

    public boolean isEmptyStateDisplayed() {
        try {
            page.waitForSelector(emptyState, new Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(5000));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogout() {
        page.click(logoutBtn);
    }
}
