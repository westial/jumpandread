Feature: Proves with getters by pattern

  Scenario Outline: Get getter according to configuration patterns and address url
    Given An environment configuration service
    And A parsers by pattern configuration as in file "<filePath>"
    And An url returning mock content getter a default
    And A default address modifier
    And A content getter factory
    When I create a by pattern content getter according to configuration
    And I get content from "<contentUrl>" by content getter
    Then The returned content is as "<expectedUrl>"
    Examples:
      | filePath                                 | contentUrl                                           | expectedUrl                                                 |
      | parsers_by_pattern_medium_ok_sample.json | https://www.gutenberg.jumpandread.com/4/f/index.html | https://www.gutenberg.jumpandread.com/4/f/index.html        |
      | parsers_by_pattern_medium_ok_sample.json | https://www.medium.com/4/f/index.html                | https://www.medium.com/4/f/index.html?format=json           |
      | parsers_by_pattern_medium_ok_sample.json | https://gutenberg.jumpandread.com/4/f/index.html     | https://gutenberg.jumpandread.com/4/f/index.html            |
      | parsers_by_pattern_medium_ok_sample.json | http://any.web.com/bla/                              | http://any.web.com/bla/                                     |
      | parsers_by_pattern_medium_ok_sample.json | https://medium.com/4/f/index.html?var=value          | https://medium.com/4/f/index.html?var=value&format=json     |
      | parsers_by_pattern_medium_ok_sample.json | https://medium.com/@pedrito/f/?var=value             | https://medium.com/@pedrito/f/?var=value&format=json        |
      | parsers_by_pattern_medium_ok_sample.json | https://gutenbergnoes.com/4/f/index.html?format=json | https://gutenbergnoes.com/4/f/index.html?format=json        |
      | parsers_by_pattern_medium_ok_sample.json | https://www.medium.com/4/f/index.html?var=value&     | https://www.medium.com/4/f/index.html?var=value&format=json |