Feature: Proves for web narrative parser service
  A narrative html is a web page with apparently no menu and with long text
  parts with mostly no html tag separations but br tags. The exceptions of this
  narrative conditions have to be considered too.

  Scenario: Separate an html content with long parts with no html paragraph tag
    Given A large html narrative content file "long_narrative_sample.html"
    And A local content getter
    And A web narrative parser service implemented from TextContentParser
    When I parse the narrative file content
    Then The list of results length is as "4589"
    And The item indexed as "0" has content as "Fifty Bab Ballads, by William S. Gilbert", has label as "a"
    And The item indexed as "4" has content as " PREFACE.", has label as "p"
    And The item indexed as "198" has content as " And HOOPER holds his ground,", has label as "p"
    And The item indexed as "4587" has content as "{15}", has label as "a"

  Scenario: Separate a usual html content not specially long
    Given A large html narrative content file "sample_9_paragraphs_web_narrative.html"
    And A local content getter
    And A web narrative parser service implemented from TextContentParser
    When I parse the narrative file content
    Then The list of results length is as "9"
    And The item indexed as "0" has content as "Whats up bro!", has label as "h2"
    And The item indexed as "4" has content as "Hey how are you?", has label as "h2"
    And The item indexed as "7" has content as "jaume", has label as "span"
    And The item indexed as "8" has content as "Lo 2 rewfksdfj dkgkdjnjfgk jhdfkjghfk. djghkfjhgjh jhgjhgjhg jdjhgk djfhgk.", has label as "p"