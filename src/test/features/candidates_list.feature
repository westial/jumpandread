Feature: Proving candidates listing

  Scenario: Successfully getting the last candidates list
    Given A mock candidate repository for listing
    And The mock candidate repository contains candidates for user as "user123456789", session as "session123456789" for search id as "mysearchid" at "10"
    And The mock candidate repository contains candidates for user as "userOther", session as "sessionOther" for search id as "othersearchid" at "10"
    And A user state repository for listing
    And A current state for listing with user Id as "user123456789", session Id as "session123456789", search Id as "mysearchid", candidateIndex as "1"
    And A command for getting list by search ID
    And An Alexa Presenter service for listing
    And A getting search if list use case
    When I invoke getting list use case
    Then Getting list result as in file "expected_getting_list.txt"

  Scenario: Trying to get the last candidates list for a state with no search Id gets the empty list warning
    Given A mock candidate repository for listing
    And The mock candidate repository contains candidates for user as "user123456789", session as "session123456789" for search id as "mysearchid" at "10"
    And The mock candidate repository contains candidates for user as "userOther", session as "sessionOther" for search id as "othersearchid" at "10"
    And A user state repository for listing
    And A current state for listing with user Id as "user123456789", session Id as "session123456789", no search Id
    And A command for getting list by search ID
    And An Alexa Presenter service for listing
    And A getting search if list use case
    When I invoke getting list use case
    Then Getting list result as in file "expected_getting_empty_list.txt"

  Scenario: Trying to get the last candidates list for a state with search Id but no candidates gets the empty list warning
    Given A mock candidate repository for listing
    And A user state repository for listing
    And A current state for listing with user Id as "user123456789", session Id as "session123456789", search Id as "mysearchid", candidateIndex as "1"
    And A command for getting list by search ID
    And An Alexa Presenter service for listing
    And A getting search if list use case
    When I invoke getting list use case
    Then Getting list result as in file "expected_getting_empty_list.txt"

  Scenario: Adding candidate children items to a search id candidates collection when they are not in yet
    Given A mock text content provider for candidate listing
    And A margin edges calculator service for candidate listing
    And A mock candidate repository for listing
    And The mock candidate repository contains candidates for user as "user123456789", session as "session123456789" for search id as "mysearchid" at "10"
    And A candidate factory for candidate listing
    And A recorded in repository sample candidate of user as "user123456789", session as "session123456789", from search id as "mysearchid", url as "https://myurl.com" with children as follows
      | title                  | url                         | description         |
      | First candidate title  | http://first.candidate.com  | first bla bla desc  |
      | Second candidate title | http://second.candidate.com | second bla bla desc |
    And An adding children to search candidates command
    When I execute the adding children command for the given sample candidate
    Then The mock candidate repository count is as "13"
    And The listing command result is as "12. Second candidate title.{{ . }}13. First candidate title.{{ . }}"

  Scenario: Adding candidate children items to a search id candidates collection when some of them are already in
    Given A mock text content provider for candidate listing
    And A margin edges calculator service for candidate listing
    And A mock candidate repository for listing
    And The mock candidate repository contains candidates for user as "user123456789", session as "session123456789" for search id as "mysearchid" at "10"
    And A candidate factory for candidate listing
    And A recorded in repository sample candidate of user as "user123456789", session as "session123456789", from search id as "mysearchid", url as "http://first.candidate.com" with no children
    And A recorded in repository sample candidate of user as "user123456789", session as "session123456789", from search id as "mysearchid", url as "https://myurl.com" with children as follows
      | title                  | url                         | description         |
      | First candidate title  | http://first.candidate.com  | first bla bla desc  |
      | Second candidate title | http://second.candidate.com | second bla bla desc |
      | Third candidate title  | http://third.candidate.com  | third bla bla desc  |
    And An adding children to search candidates command
    When I execute the adding children command for the given sample candidate
    Then The mock candidate repository count is as "14"
    And The listing command result is as "13. Second candidate title.{{ . }}14. Third candidate title.{{ . }}"