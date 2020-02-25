Feature: Proves with a text cleaner service

  Scenario Outline: Extract the given text with the given regular expression
    Given A text cleaner service setting up with the extraction regex as "\b[^-]+"
    When I extract the wanted content from "<input>"
    Then I got the content as "<output>"
    Examples:
      | input                                            | output                         |
      | My horrible title - Merdium - Mierdum in spanish | My horrible title              |
      | My horrible Merdium in spanish                   | My horrible Merdium in spanish |
      | - Merdium - Mierdum in spanish                   | Merdium                        |