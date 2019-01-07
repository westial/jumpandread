Feature: All movements between candidates and paragraphs with their exceptions proves


  Scenario: Read command, read the first paragraphs for the given candidate index even when paragraphs index is not the candidate starting point
    Given An html candidate parser
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
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A Use Case Factory for reading only
    And A newly created use case for current candidate reading
    When I invoke current candidate use case for intent name as "ReadCandidate", candidate index as "2", paragraphs group as "10"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_0_sample_7.txt"
    And The current state candidate index is as "2"


  Scenario: Read command, try reading the first paragraphs for an empty candidate goes to read first paragraphs of the next one
    Given An html candidate parser
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "2"
    And A candidate document getter with forced and queued contents as in files as follows
      | sample_0_paragraphs.html           |
      | sample_7_paragraphs.html           |
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 6              |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A Use Case Factory for reading only
    And A newly created use case for current candidate reading
    When I invoke current candidate use case for intent name as "ReadCandidate", candidate index as "2", paragraphs group as "10"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_0_sample_7_with_no_paragraphs_prefix.txt"
    And The current state candidate index is as "3"


  Scenario: Next command, read next paragraphs when current candidate has them
    Given An html candidate parser
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "2"
    And A candidate document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 2              |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A Use Case Factory for reading only
    And A newly created use case for reading next
    When I invoke next candidate use case for intent name as "Continue", paragraphs group as "3"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_5_sample_7.txt"
    And The current state candidate index is as "2"


  Scenario: Repeat command, repeat reading last read paragraphs
    Given An html candidate parser
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "2"
    And A candidate document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 2              |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A Use Case Factory for reading only
    And A newly created use case for repeat reading
    When I invoke repeat reading use case for intent name as "Repeat", paragraphs group as "2"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_2_to_4_sample_7.txt"
    And The current state candidate index is as "2"


  Scenario: Trying to read a candidate with a number out of range asks user to find again

  Scenario: Trying to read a candidate too soon when there is no candidate yet asks user to search for something

  Scenario: Trying to read next paragraphs when candidate has no more paragraphs notices user and goes to next candidate and reads first paragraphs group

  Scenario: Trying to read next paragraphs when candidate has no paragraphs