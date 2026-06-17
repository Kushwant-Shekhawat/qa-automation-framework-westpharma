@LoginFeature
Feature: Pharmacist Login UI Workflow
  As a pharmacist
  I want to securely log in to the Dummy Pharma Company database
  So that I can access the medical formulas dashboard

  Background:
    Given the pharmacist is on the login page

  @HighRisk @Smoke
  Scenario: Successful pharmacist login with valid credentials
    When the pharmacist enters email "pharmacist@dummypharma.com" and password "SecurePass123"
    And clicks the login button
    Then the pharmacist should be redirected to the dashboard page
    And the welcome message should display "Welcome, Dr. Jane Doe"

  @HighRisk
  Scenario Outline: Failed pharmacist login with invalid credentials
    When the pharmacist enters email "<email>" and password "<password>"
    And clicks the login button
    Then a login error message should be displayed indicating "<error>"

    Examples:
      | email                      | password      | error                      |
      | pharmacist@dummypharma.com | WrongPass     | Invalid email or password. |
      | unknown@domain.com         | SecurePass123 | Invalid email or password. |

  @MediumRisk
  Scenario Outline: Validating email input format
    When the pharmacist enters email "<email>" and password "SecurePass123"
    And clicks the login button
    Then a login error message should be displayed indicating "<error>"

    Examples:
      | email              | error                 |
      | invalidemail       | Invalid email format. |
      | pharmacist@        | Invalid email format. |

  @LowRisk
  Scenario: Password field toggle visibility
    When the pharmacist enters password "SecurePass123"
    Then the password field should mask the input text
    When the pharmacist clicks the password toggle show button
    Then the password field should reveal the input text
    And the toggle button label should change to "Hide"
