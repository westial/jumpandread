Feature: Margin edges calculator service tests

  Scenario Outline: Get first and last item index within the part range
    Given A total width as "<total>"
    And A part range width as "<partWidth>"
    And The part range starts at index "<partStart>"
    And A moving margin width as "<margin>"
    And An edges calculator service
    When I move the current position to "<position>"
    Then The part range has to start at "<expected>" because "<comment>"
    Examples:
      | total | partWidth | partStart | margin | position | expected | comment                                         |
      | 1000  | 300       | 0         | 150    | 290      | 64       | moved after margin                              |
      | 1000  | 300       | 700       | 150    | 720      | 644      | moved before margin                             |
      | 1000  | 300       | 100       | 150    | 5        | 0        | moved between 0 and half of margin              |
      | 1000  | 300       | 100       | 150    | 990      | 699      | moved between total and half of margin          |
      | 1000  | 300       | 180       | 150    | 256      | [null]   | do not move it inside negative margin           |
      | 1000  | 300       | 180       | 150    | 404      | [null]   | do not move it inside positive margin           |
      | 1000  | 300       | 500       | 150    | -110     | 0        | move to 0 for impossible negative numbers       |
      | 1000  | 300       | 500       | 150    | 2356     | 699      | move to maximum for impossible exceeded numbers |