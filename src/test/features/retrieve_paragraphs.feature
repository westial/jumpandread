Feature: Retrieving paragraphs from current user's candidate after selecting a candidate from search results list

  Scenario Outline: Get exception retrieving paragraphs from empty candidate
    Given An html candidate parser
    And A user state repository for parsing
    And A current state with user Id as "<userId>", session Id as "<sessionId>", search Id as "<searchId>"
    And An Alexa output formatter
    And A user with user id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session id as "12345678-1234-4ea6-bfca-347ca7612345", search id as "87654321-7654-4ea6-bfca-747ca111111"
    And A candidate document getter with forced content as in file "<sampleFile>"
    And A selected candidate as follows
      | index   | userId   | sessionId   | searchId   | title   | url   | description   | paragraphIndex   |
      | <index> | <userId> | <sessionId> | <searchId> | <title> | <url> | <description> | <paragraphIndex> |
    And A candidate repository for parsing
    And A candidate factory for parsing
    And A configuration value for joining paragraphs as "10"
    And A retrieving paragraphs command
    When I execute retrieving command for user and session and number of candidate as "5" for intent "RetrieveCandidates"
    Then Command thrown an exception with the message as "<expectedError>"
    Examples:
      | sampleFile               | index | userId                               | sessionId                            | searchId                            | title           | url             | description | paragraphIndex | expectedError             |
      | sample_0_paragraphs.html | 5     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | My sample title | https://url.com | bla bla bla | 0              | No Candidate for index 6  |

  Scenario Outline: Retrieve textual representation of paragraphs always from beginning with introduction
    Given An html candidate parser
    And An Alexa output formatter
    And A user state repository for parsing
    And A current state with user Id as "<userId>", session Id as "<sessionId>", search Id as "<searchId>"
    And A candidate document getter with forced content as in file "sample_7_paragraphs.html"
    And A selected candidate as follows
      | index   | userId   | sessionId   | searchId   | title   | url   | description   | paragraphIndex   |
      | <index> | <userId> | <sessionId> | <searchId> | <title> | <url> | <description> | <paragraphIndex> |
    And A candidate repository for parsing
    And A candidate factory for parsing
    And A configuration value for joining paragraphs as "10"
    And A retrieving paragraphs command
    When I execute retrieving command for user and session and number of candidate as "3" for intent "RetrieveCandidates"
    Then Command returned a text as in file "<expectedFile>"
    Examples:
      | index | userId                               | sessionId                            | searchId                            | title           | url             | description | paragraphIndex | expectedFile                            |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | My sample title | https://url.com | bla bla bla | 0              | expected_paragraph_index_0_sample_7.txt |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | My sample title | https://url.com | bla bla bla | 2              | expected_paragraph_index_0_sample_7.txt |