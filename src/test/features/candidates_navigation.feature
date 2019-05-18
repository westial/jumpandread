Feature: All movements between candidates and paragraphs with their exceptions proves

  Scenario: Read command, read the first paragraphs for the given candidate index even when paragraphs index is not the candidate starting point
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 6              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for current candidate reading
    When I invoke current candidate use case for intent name as "ReadCandidate", candidate index as "1", paragraphs group as "10"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_0_sample_7.txt"
    And The current state candidate index is as "1"


  Scenario: Pause and Continue command, pause when reading and point paragraph index behind current reading
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 6              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A configuration value for default jumping factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for pause candidate reading
    When I invoke pause candidate use case for intent name as "Pause", paragraphs group as "2"
    Then The result after invocation is not null
    And The current state candidate index is as "1"
    And The current state candidate paragraph position is as "4"
    And The speech contained in result is as "warning.after.pause"


  Scenario: Read command, try reading the first paragraphs for an empty candidate goes to read first paragraphs of the next one
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_0_paragraphs.html |
      | sample_7_paragraphs.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 6              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for current candidate reading
    When I invoke current candidate use case for intent name as "ReadCandidate", candidate index as "1", paragraphs group as "10"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_0_sample_7_with_no_paragraphs_prefix.txt"
    And The current state candidate index is as "2"


  Scenario: Next command, read next paragraphs when current candidate has them
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 2              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for reading next
    When I invoke next candidate use case for intent name as "Continue", paragraphs group as "3"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_5_sample_7.txt"
    And The current state candidate index is as "1"


  Scenario: Repeat command, repeat reading last read paragraphs
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 2              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for repeat reading
    When I invoke repeat reading use case for intent name as "Repeat", paragraphs group as "2"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_2_to_4_sample_7.txt"
    And The current state candidate index is as "1"


  Scenario: Trying to read a candidate with a number out of range asks user to find again
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 2              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for current candidate reading
    When I invoke current candidate use case for intent name as "ReadCandidate", candidate index as "3", paragraphs group as "10"
    Then The result after invocation is not null
    And The speech contained in result is as "warning.no.candidate.for.this.index.(index(3),start(1),end(2))"
    And The current state candidate index is as "3"


  Scenario: Trying to read a candidate index in a new session having this user deprecated search candidates from other session asks user to search for something
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A new state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "99999999-1234-4ea6-bfca-347ca7612345"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 2              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for current candidate reading
    When I invoke current candidate use case for intent name as "ReadCandidate", candidate index as "3", paragraphs group as "10"
    Then The result after invocation is not null
    And The speech contained in result is as "warning.no.search.in.this.session.yet"


  Scenario: Trying to read next paragraphs when candidate has no more paragraphs notices user and goes to next candidate and reads first paragraphs group
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_0_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 6              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
      | 3     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Third candidate title  | http://third.candidate.com  | third bla bla desc  | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A newly created use case for reading next
    When I invoke next candidate use case for intent name as "Continue", paragraphs group as "10"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_sample_7_following.txt"
    And The current state candidate index is as "3"


  Scenario: Forward command, jump forwards as value of jumping factor and read paragraphs when current candidate has them
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 3              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A configuration value for default jumping factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A forward created use case for reading as times as default jumping factor after next
    When I invoke forward candidate use case for intent name as "Forward", paragraphs group as "2"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_5_sample_7.txt"
    And The current state candidate index is as "1"


  Scenario: Trying to forward command when last candidate has not more paragraphs notices about no more candidates
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "2"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 6              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 5              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A configuration value for default jumping factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A forward created use case for reading as times as default jumping factor after next
    When I invoke forward candidate use case for intent name as "Forward", paragraphs group as "2"
    Then The result after invocation is not null
    And The speech contained in result is as "warning.forward.into.candidate.out.of.bounds(index(2))command.reading.list"
    And The current state candidate index is as "2"


  Scenario: Backward command, jump backwards as value of jumping factor and read paragraphs when current candidate has them
    Given A web search candidate parser
    And An address document getter with forced and queued contents as in files as follows
      | sample_7_paragraphs.html           |
      | sample_7_paragraphs_following.html |
    And A mock text content provider for retrieval
    And A user state repository for parsing
    And A current state with user Id as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session Id as "12345678-1234-4ea6-bfca-347ca7612345", search Id as "87654321-7654-4ea6-bfca-747ca111111", candidateIndex as "1"
    And A searching result candidate list as follows
      | index | userId                               | sessionId                            | searchId                            | title                  | url                         | description         | paragraphIndex |
      | 1     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | First candidate title  | http://first.candidate.com  | first bla bla desc  | 5              |
      | 2     | ab551872-0a24-4ea6-bfca-347ca76ee8ce | 12345678-1234-4ea6-bfca-347ca7612345 | 87654321-7654-4ea6-bfca-747ca111111 | Second candidate title | http://second.candidate.com | second bla bla desc | 0              |
    And A multiple candidate repository for jumping
    And A candidate factory for parsing
    And A Mock Presenter service
    And A configuration value for default candidates factor as "1"
    And A configuration value for default jumping factor as "1"
    And An adding children to search candidates command for reading only
    And A Use Case Factory for reading only
    And A backward created use case for reading as times as default jumping factor after last
    When I invoke backward candidate use case for intent name as "Backward", paragraphs group as "2"
    Then The result after invocation is not null
    And The speech contained in result is as in file "expected_paragraph_index_3_to_5_sample_7.txt"
    And The current state candidate index is as "1"