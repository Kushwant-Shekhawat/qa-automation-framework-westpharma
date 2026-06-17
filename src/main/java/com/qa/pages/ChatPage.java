package com.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.qa.config.ConfigManager;

public class ChatPage {
    private final Page page;

    // Locators as CSS Selectors
    private final String researcherIdInput = "#researcherIdInput";
    private final String assistantNameInput = "#assistantNameInput";
    private final String chatInput = "#chatInput";
    private final String sendBtn = "#sendBtn";
    private final String chatBubbles = ".chat-bubble";
    private final String typingIndicator = "#typingIndicator";

    public ChatPage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate(ConfigManager.getBaseUrl() + "/chat.html");
    }

    public void setResearcherId(String researcherId) {
        page.fill(researcherIdInput, researcherId);
    }

    public void setAssistantName(String assistantName) {
        page.fill(assistantNameInput, assistantName);
    }

    public void enterMessage(String message) {
        page.fill(chatInput, message);
    }

    public void clickSend() {
        page.click(sendBtn);
    }

    public void sendMessage(String message) {
        enterMessage(message);
        clickSend();
        waitForResponse();
    }

    public void waitForResponse() {
        // Wait for typing indicator to be hidden (meaning response has arrived)
        page.waitForSelector(typingIndicator, new Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN));
    }

    public int getChatBubblesCount() {
        return page.locator(chatBubbles).count();
    }

    public Locator getLastBubble() {
        int count = getChatBubblesCount();
        if (count == 0) return null;
        return page.locator(chatBubbles).nth(count - 1);
    }

    public String getLastBubbleText() {
        Locator lastBubble = getLastBubble();
        if (lastBubble == null) return "";
        return lastBubble.locator(".message-text").textContent().trim();
    }

    public String getLastBubbleSender() {
        Locator lastBubble = getLastBubble();
        if (lastBubble == null) return "";
        return lastBubble.locator(".sender-tag").textContent().trim();
    }

    public boolean isSafetyIndicatorDisplayedInLastBubble() {
        Locator lastBubble = getLastBubble();
        if (lastBubble == null) return false;
        return lastBubble.locator(".safety-indicator").isVisible();
    }
}
