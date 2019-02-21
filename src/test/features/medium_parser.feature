Feature: Proves for medium parser service

  Scenario: Successfully parsing a normal free article
    Given A medium article content as in file "medium/mediumFreeArticle.json"
    And A local content getter for medium parser test
    And A medium parser service implemented from TextContentParser with the regex pattern as "\]\)\}while\(1\);</x>(\{.+\})"
    When I parse the medium file content
    Then The list of medium parsing results length is as "16"
    And The medium parsed item indexed as "0" has content as "Are you looking to buy Outer Lid Pressure Cooker Online?", has label as "h2"
    And The medium parsed item indexed as "6" has content as "The outer lid pressure cookers come with the induction base. This induction base offers even heat distribution for cooking your food. The precision weight valve of these pressure cookers works as the initial level of the feature of security. It saves your important time and simplifies the entire process of cooking.", has label as "p"
    And The medium parsed item indexed as "14" has content as "They are also famous across the world for their inner lid pressure cookers. Products made by this company are going through a stringent quality check. They are safe for regular use and available at an attractive price. This company continuously wins the heart of its customers.", has label as "p"
    And The medium parsed item indexed as "15" has content as "Since its inception, Mr. Cook is a leading cookware and cooker manufacturing company in India. Its outer lid pressure cookers are available in the market with attractive designs.", has label as "p"


  Scenario: Successfully parsing an author article list
    Given A medium article content as in file "medium/mediumFreeArticle.json"
    And A local content getter for medium parser test
    And A medium parser service implemented from TextContentParser with the regex pattern as "\]\)\}while\(1\);</x>(\{.+\})"
    When I parse the medium file content
    Then The list of medium parsing results length is as "16"
    And The medium parsed item indexed as "0" has content as "Are you looking to buy Outer Lid Pressure Cooker Online?", has label as "h2"
    And The medium parsed item indexed as "6" has content as "The outer lid pressure cookers come with the induction base. This induction base offers even heat distribution for cooking your food. The precision weight valve of these pressure cookers works as the initial level of the feature of security. It saves your important time and simplifies the entire process of cooking.", has label as "p"
    And The medium parsed item indexed as "14" has content as "They are also famous across the world for their inner lid pressure cookers. Products made by this company are going through a stringent quality check. They are safe for regular use and available at an attractive price. This company continuously wins the heart of its customers.", has label as "p"
    And The medium parsed item indexed as "15" has content as "Since its inception, Mr. Cook is a leading cookware and cooker manufacturing company in India. Its outer lid pressure cookers are available in the market with attractive designs.", has label as "p"


  Scenario: Throw a no paragraphs exception on parsing a member only article
    Given A medium article content as in file "medium/mediumMemberOnlyArticle.json"
    And A local content getter for medium parser test
    And A medium parser service implemented from TextContentParser with the regex pattern as "\]\)\}while\(1\);</x>(\{.+\})"
    When I parse the medium file content
    Then The parser threw an exception as "com.westial.alexa.jumpandread.application.exception.IteratingNoParagraphsException"
