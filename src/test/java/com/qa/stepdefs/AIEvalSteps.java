package com.qa.stepdefs;

import com.microsoft.playwright.Page;
import com.qa.driver.PlaywrightDriver;
import com.qa.pages.ChatPage;
import com.qa.utils.AIEvaluator;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class AIEvalSteps {
    private ChatPage chatPage;

    @Then("the AI response should be evaluated with criteria:")
    public void the_ai_response_should_be_evaluated_with_criteria(DataTable dataTable) {
        Page page = PlaywrightDriver.getPage();
        chatPage = new ChatPage(page);

        // Get the last user message and the last AI response from page or keep tracking
        // For simplicity, we can fetch from ChatPage UI
        int bubblesCount = chatPage.getChatBubblesCount();
        Assert.assertTrue(bubblesCount >= 2, "Not enough chat bubbles to perform evaluation!");

        // The last bubble is AI response. The second to last bubble is user message.
        String aiResponse = chatPage.getLastBubbleText();
        
        // Let's get user message (second to last)
        String userPrompt = "";
        if (bubblesCount >= 2) {
            String fullText = page.locator(".chat-bubble").nth(bubblesCount - 2).textContent().trim();
            String sender = page.locator(".chat-bubble").nth(bubblesCount - 2).locator(".sender-tag").textContent().trim();
            userPrompt = fullText.replace(sender, "").trim();
        }

        // Set context maps
        String researcherId = page.inputValue("#researcherIdInput").trim();
        String assistantName = page.inputValue("#assistantNameInput").trim();
        Map<String, Object> context = new HashMap<>();
        context.put("researcherId", researcherId.isEmpty() ? null : researcherId);
        context.put("assistantName", assistantName.isEmpty() ? null : assistantName);
        // Keep childName/companionName for backward compatibility within evaluator checks
        context.put("childName", researcherId.isEmpty() ? null : researcherId);
        context.put("companionName", assistantName.isEmpty() ? null : assistantName);

        System.out.println("[AI EVAL STEPS] Evaluating User Prompt: '" + userPrompt + "'");
        System.out.println("[AI EVAL STEPS] AI Response: '" + aiResponse + "'");

        // Execute AI Evaluation
        AIEvaluator.EvaluationResult result = AIEvaluator.evaluateResponse(userPrompt, aiResponse, context);
        System.out.println("[AI EVAL STEPS] Evaluation Output: " + result);

        // Map and validate thresholds from Gherkin Datatable
        Map<String, String> thresholds = dataTable.asMap(String.class, String.class);
        
        if (thresholds.containsKey("Correctness & Pacing")) {
            int threshold = Integer.parseInt(thresholds.get("Correctness & Pacing"));
            Assert.assertTrue(result.correctnessScore >= threshold, 
                "Correctness Score " + result.correctnessScore + " is below threshold " + threshold + "! Feedback: " + result.feedback);
        }
        
        if (thresholds.containsKey("Relevance")) {
            int threshold = Integer.parseInt(thresholds.get("Relevance"));
            Assert.assertTrue(result.relevanceScore >= threshold, 
                "Relevance Score " + result.relevanceScore + " is below threshold " + threshold + "! Feedback: " + result.feedback);
        }
        
        if (thresholds.containsKey("Consistency")) {
            int threshold = Integer.parseInt(thresholds.get("Consistency"));
            Assert.assertTrue(result.consistencyScore >= threshold, 
                "Consistency Score " + result.consistencyScore + " is below threshold " + threshold + "! Feedback: " + result.feedback);
        }
        
        if (thresholds.containsKey("Safety")) {
            int threshold = Integer.parseInt(thresholds.get("Safety"));
            Assert.assertTrue(result.safetyScore >= threshold, 
                "Safety Score " + result.safetyScore + " is below threshold " + threshold + "! Feedback: " + result.feedback);
            Assert.assertTrue(result.passedSafety, "Safety evaluation failed! Feedback: " + result.feedback);
        }
    }

    @Then("the evaluation result should pass all assertions")
    public void the_evaluation_result_should_pass_all_assertions() {
        // Handled in the Gherkin step above, included for scenario flow completeness
        System.out.println("[AI EVAL STEPS] All assertions passed successfully!");
    }
}
