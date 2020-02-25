Feature: Repeating dialog support in Presenter for adding and publishing content after reprompts

  Scenario: Return the content previously settled for reprompt
    Given An Alexa Presenter service for reprompt
    And I set speech content as "bla bla" into Presenter
    And I set repeat content as "fins fans" into Presenter
    When I ask for repeating content to Presenter
    Then Presenter returned "<speak>fins fans</speak>"

  Scenario: Return the content for speech instead of reprompt when repeating content is empty
    Given An Alexa Presenter service for reprompt
    And I set speech content as "bla bla" into Presenter
    When I ask for repeating content to Presenter
    Then Presenter returned "<speak>bla bla</speak>"