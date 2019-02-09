Feature: Proves for text content providers factory


  Scenario: Throw exception when both parsing configuration parameters are configured
    Given An environment configuration service
    And A parser type configuration value as "WebSearch"
    And A parsers by pattern configuration as in file "parsers_by_pattern_sample.json"
    And An empty mock content getter
    And A mock web search parser as default
    And A mock text content providers factory by configuration with the test only method to get the parser
    When I create the text content provider
    Then The factory threw an exception with name as "com.westial.alexa.jumpandread.infrastructure.exception.InitializationError"


  Scenario: Create provider with pattern parser type according to configuration
    Given An environment configuration service
    And A parsers by pattern configuration as in file "parsers_by_pattern_sample.json"
    And An empty mock content getter
    And A mock web search parser as default
    And A mock text content providers factory by configuration with the test only method to get the parser
    When I create the text content provider
    Then The providers factory parser class name is as "com.westial.alexa.jumpandread.infrastructure.service.ParserByPatternTextContentProvider"


  Scenario: Create provider with fixed parser type according to configuration
    Given An environment configuration service
    And A parser type configuration value as "WebNarrative"
    And An empty mock content getter
    And A mock web search parser as default
    And A mock text content providers factory by configuration with the test only method to get the parser
    When I create the text content provider
    Then The providers factory parser class name is as "com.westial.alexa.jumpandread.infrastructure.service.RemoteTextContentProvider"
