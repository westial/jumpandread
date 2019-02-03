Feature: Margin edges calculator service tests

  Scenario Outline: Get first and last item index within the part range
    Given A total width as "<total>"
    And A part range width as "<pageWidth>"
    And The part range starts at index "<pageStart>"
    And A moving margin width as "<margin>"
    And An edges calculator service
    When I move the current position to "<position>"
    Then The part range has to start at "<expected>" because "<comment>"
    Examples:
      | total | pageWidth | pageStart | margin | position | expected | comment                                          |
      | 217   | 50        | 40        | 20     | 90       | 80       | moved after margin                               |
      | 1000  | 50        | 0         | 20     | 55       | 45       | moved after margin                               |
      | 1000  | 50        | 70        | 20     | 65       | 25       | moved before margin                              |
      | 1000  | 300       | 0         | 150    | 289      | 214      | moved after margin                               |
      | 1000  | 300       | 700       | 150    | 720      | 495      | moved before margin                              |
      | 1000  | 300       | 100       | 150    | 5        | 0        | moved between 0 and half of margin               |
      | 1000  | 300       | 100       | 150    | 990      | 699      | moved between total and half of margin           |
      | 1000  | 300       | 180       | 150    | 256      | [null]   | do not move it inside negative margin            |
      | 1000  | 300       | 180       | 150    | 404      | [null]   | do not move it inside positive margin            |
      | 1000  | 300       | 500       | 150    | -110     | 0        | move to 0 for impossible negative numbers        |
      | 1000  | 300       | 500       | 150    | 2356     | 699      | move to maximum for impossible exceeded numbers  |
      | 1000  | 100       | 0         | 25     | 911      | 899      | moved from beginning to end of total             |
      | 1000  | 100       | 950       | 25     | 25       | 0        | moved from end to beginning of total             |
      | 1000  | 100       | 950       | 25     | 24       | 0        | moved from end to just before beginning of total |
      | 1000  | 100       | 0         | 25     | 912      | 899      | moved from beginning to just after end of total  |
      | 1000  | 100       | 0         | 25     | 910      | 898      | moved from beginning to almost end of total      |
      | 1000  | 100       | 999       | 25     | 89       | 1        | moved from end to almost beginning of total      |