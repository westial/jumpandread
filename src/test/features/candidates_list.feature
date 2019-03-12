Feature: Proving candidates listing

  Scenario: Adding candidate children items to a search id candidates collection when they are not in yet
    Given A mock text content provider for candidate listing
    And A margin edges calculator service for candidate listing
    And A mock candidate repository with current candidates count of user as "user123456789", session as "session123456789" for search id as "mysearchid" at "10"
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
    And A mock candidate repository with current candidates count of user as "user123456789", session as "session123456789" for search id as "mysearchid" at "10"
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