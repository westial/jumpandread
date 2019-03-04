Feature: Proves with parsers by pattern

  Scenario Outline: Get parser according to configuration patterns and address url
    Given An environment configuration service
    And A parsers by pattern configuration as in file "<filePath>"
    And A mock web search parser as default
    And A parser factory with empty value for medium prefix filter regex
    When I create a by pattern parser according to configuration
    And I configure the by pattern parser for "<contentUrl>"
    Then The configured parser into by pattern parser is an instance of "<expectedParser>"
    Examples:
      | filePath                       | contentUrl                                           | expectedParser                |
      | parsers_by_pattern_sample.json | https://www.gutenberg.jumpandread.com/4/f/index.html | WebNarrativeTextContentParser |
      | parsers_by_pattern_sample.json | https://gutenberg.jumpandread.com/4/f/index.html     | WebNarrativeTextContentParser |
      | parsers_by_pattern_sample.json | http://gutenberg.jumpandread.com/4/f/index.html      | WebNarrativeTextContentParser |
      | parsers_by_pattern_sample.json | https://www.westial.com/4/f/index.html               | MockContentParser             |
      | parsers_by_pattern_sample.json | https://gutenbergnoes.com/4/f/index.html             | MockContentParser             |
      | parsers_by_pattern_sample.json | https://noesgutenberg.com/4/f/index.html             | MockContentParser             |
      | parsers_by_pattern_sample.json | https://www.mediumnoes.com/@ertererte/f/index.html   | MockContentParser             |
      | parsers_by_pattern_sample.json | https://www.noesmedium.com/@ertererte/f/index.html   | MockContentParser             |
      | parsers_by_pattern_sample.json | https://www.medium.com/noesmediumparsejable          | MediumTextContentParser       |
      | parsers_by_pattern_sample.json | https://www.medium.com/@ertererte/f/index.html       | WebSearchTextContentParser    |
      | parsers_by_pattern_sample.json | https://medium.com/@ertererte/f/index.html           | WebSearchTextContentParser    |
      | parsers_by_pattern_sample.json | http://medium.com/@ertererte/f/index.html            | WebSearchTextContentParser    |