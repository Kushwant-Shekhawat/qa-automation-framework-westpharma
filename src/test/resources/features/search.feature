@SearchFeature
Feature: Dashboard Formula Search and Filters
  As a logged-in pharmacist
  I want to search and filter pharmaceutical compound formulas
  So that I can quickly locate specific clinical records

  Background:
    Given the pharmacist is logged in and on the dashboard page

  @HighRisk @Smoke
  Scenario Outline: Search formulas by text keywords
    When the pharmacist searches for keyword "<keyword>"
    Then the system should display formulas matching "<keyword>"
    And the results count should be greater than 0

    Examples:
      | keyword     |
      | Aspirin     |
      | Amoxicillin |
      | Lactobacil  |
      | Lipitor     |

  @MediumRisk
  Scenario Outline: Filter formulas by drug category
    When the pharmacist selects the category filter "<category>"
    Then all displayed formulas should belong to the category "<category>"

    Examples:
      | category       |
      | Analgesics     |
      | Antibiotics    |
      | Probiotics     |
      | Cardiovascular |

  @LowRisk
  Scenario: Search with no matching results displays empty state
    When the pharmacist searches for keyword "UnknownFormulaMysteryCompound"
    Then an empty state message should be displayed
    And the results count should be 0
