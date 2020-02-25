Feature: Proves with a matching words checking service.
  This service gets a text content as input and returns a percentage of
  words matching a list of sample words retrieved from a file content with the
  sample words separated by linebreak character.
  bad-words.txt file has been downloaded from https://www.cs.cmu.edu/~biglou/resources/

  Scenario: Read multiple word list files and return a list of unique words
    Given A word list multiple files to list converter service for files as "bad-words.txt", "google-profanity-words.txt", "swear-words-chucknorris.txt"
    When I convert the multiple files into a word list
    Then I got a list of "1828" unique words

  Scenario: Detect the minimum amount of offensive words
    Given A word list multiple files to list converter service for files as "bad-words.txt", "google-profanity-words.txt", "swear-words-chucknorris.txt"
    When I convert the multiple files into a word list
    And A matching words service
    When I check the matching words as in file as "wordcheck/bitoffensive.txt" with the service
    Then The service returned a value greater than "1"

  Scenario: Returns almost 100% of bad words matching
    Given A sample words content as in file "bad-words.txt"
    And A matching words service
    When I check the matching words as in file as "bad-words.txt" with the service
    Then The service returned a value greater than "98"

  Scenario: Returns almost 0% of bad words matching
    Given A sample words content as in file "bad-words.txt"
    And A matching words service
    When I check the matching words as in file as "wordcheck/mockitomedium.txt" with the service
    Then The service returned a value lower than "1"

  Scenario: Returns a little offensive result
    Given A sample words content as in file "bad-words.txt"
    And A matching words service
    When I check the matching words as in file as "wordcheck/littleoffensive.txt" with the service
    Then The service returned a value greater than "3"
    Then The service returned a value lower than "6"

  Scenario: Returns a quite offensive result
    Given A sample words content as in file "bad-words.txt"
    And A matching words service
    When I check the matching words as in file as "wordcheck/offensive.txt" with the service
    Then The service returned a value greater than "3"
    Then The service returned a value lower than "6"

  Scenario Outline: Returns standard results for a batch
    Given A sample words content as in file "bad-words.txt"
    And A matching words service
    When I check the matching words as in file as "<filePath>" with the service
    Then The service returned a value lower than "3"
    Examples:
      | filePath         |
      | wordcheck/t1.txt |
      | wordcheck/t2.txt |
      | wordcheck/t3.txt |
      | wordcheck/z1.txt |
      | wordcheck/s1.txt |

  Scenario: Appropriate Words validator does not validate
    Given A sample words content as in file "bad-words.txt"
    And A matching words service
    And A words validator service for a maximum of "3" percent of offensive words
    When I validate words as in file as "wordcheck/offensive.txt" with the service
    Then The service returned a value as "false"

  Scenario: Appropriate Words validator validates
    Given A sample words content as in file "bad-words.txt"
    And A matching words service
    And A words validator service for a maximum of "3" percent of offensive words
    When I validate words as in file as "wordcheck/t1.txt" with the service
    Then The service returned a value as "true"