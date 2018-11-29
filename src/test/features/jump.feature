Feature: Jump everywhere and for every scenario

  Scenario: Jump to next existent Candidate while reading a Candidate with no more paragraphs after
    Given An html candidate parser
    And An Alexa output formatter
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "2"
    And A candidate document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 6              |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A configuration value for joining paragraphs as "2"
    And A jump command
    When I execute jump command for current state
    Then Command returned a text as in file "expected_paragraph_index_sample_7_jump_following.txt"

  Scenario Outline: Jump while reading a Candidate which has more paragraphs to read after
    Given An html candidate parser
    And An Alexa output formatter
    And A user state repository for parsing
    And A current state with user Id as "<userId>", session Id as "<sessionId>", search Id as "<searchId>", candidateIndex as "<candidateIndex>"
    And A candidate document getter with forced content as in file "sample_7_paragraphs.html"
    And A selected candidate as follows
      | index   | userId   | sessionId   | searchId   | title   | url   | description   | paragraphIndex   |
      | <index> | <userId> | <sessionId> | <searchId> | <title> | <url> | <description> | <paragraphIndex> |
    And A candidate repository for parsing
    And A candidate factory for parsing
    And A configuration value for joining paragraphs as "2"
    And A jump command
    When I execute jump command for current state
    Then Command returned a text as in file "<expectedFile>"
    Examples:
      | index | userId                               | sessionId                            | searchId                            | candidateIndex | title           | url             | description | paragraphIndex | expectedFile                                 |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | 3              | My sample title | https://url.com | bla bla bla | 3              | expected_paragraph_index_3_to_5_sample_7.txt |


  Scenario Outline: Jump while reading a Candidate returns null because it has not been started reading
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
    And A configuration value for joining paragraphs as "2"
    And A jump command
    When I execute jump command for current state
    Then Command thrown an exception with the message as "<expectedErrorMessage>"
    Examples:
      | index | userId                               | sessionId                            | searchId                            | title           | url             | description | paragraphIndex | expectedErrorMessage       |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | My sample title | https://url.com | bla bla bla | 0              | candidateIndex is null yet |

  Scenario Outline: Jump while reading a Candidate returns null because there is no candidate with the candidateIndex as given one
    Given An html candidate parser
    And An Alexa output formatter
    And A user state repository for parsing
    And A current state with user Id as "<userId>", session Id as "<sessionId>", search Id as "<searchId>", candidateIndex as "<candidateIndex>"
    And A candidate document getter with forced content as in file "sample_7_paragraphs.html"
    And A selected candidate as follows
      | index   | userId   | sessionId   | searchId   | title   | url   | description   | paragraphIndex   |
      | <index> | <userId> | <sessionId> | <searchId> | <title> | <url> | <description> | <paragraphIndex> |
    And A candidate repository for parsing
    And A candidate factory for parsing
    And A configuration value for joining paragraphs as "2"
    And A jump command
    When I execute jump command for current state
    Then Command thrown an exception with the message as "<expectedErrorMessage>"
    Examples:
      | index | userId                               | sessionId                            | searchId                            | candidateIndex | title           | url             | description | paragraphIndex | expectedErrorMessage        |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | 1111           | My sample title | https://url.com | bla bla bla | 0              | No Candidate for index 1111 |