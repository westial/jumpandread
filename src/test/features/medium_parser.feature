Feature: Proves for medium parser service

  Scenario: Successfully parsing a normal free article
    Given A medium article content as in file "medium/mediumFreeArticle.json"
    And A local content getter for medium parser test
    And A medium parser service implemented from TextContentParser with the regex pattern as "\]\)\}while\(1\);</x>(\{.+\})", URI root as "http://www.medium.com/p"
    When I parse the medium file content
    Then The list of medium parsing results length is as "16"
    And The medium parsed item indexed as "0" has content as "Are you looking to buy Outer Lid Pressure Cooker Online?", has label as "h2"
    And The medium parsed item indexed as "6" has content as "The outer lid pressure cookers come with the induction base. This induction base offers even heat distribution for cooking your food. The precision weight valve of these pressure cookers works as the initial level of the feature of security. It saves your important time and simplifies the entire process of cooking.", has label as "p"
    And The medium parsed item indexed as "14" has content as "They are also famous across the world for their inner lid pressure cookers. Products made by this company are going through a stringent quality check. They are safe for regular use and available at an attractive price. This company continuously wins the heart of its customers.", has label as "p"
    And The medium parsed item indexed as "15" has content as "Since its inception, Mr. Cook is a leading cookware and cooker manufacturing company in India. Its outer lid pressure cookers are available in the market with attractive designs.", has label as "p"


  Scenario: Successfully parsing an author article list
    Given A medium article content as in file "medium/mediumAuthorArticleList.json"
    And A local content getter for medium parser test
    And A medium parser service implemented from TextContentParser with the regex pattern as "\]\)\}while\(1\);</x>(\{.+\})", URI root as "http://www.medium.com/p"
    When I parse the medium file content
    Then The list of medium parsing results length is as "3"
    And The medium parsed item indexed as "0" has content as "Are you looking to buy Outer Lid Pressure Cooker Online?", has label as "X_CANDIDATE"
    And The medium parsed item indexed as "0" has "src" as "http://www.medium.com/p/are-you-looking-to-buy-outer-lid-pressure-cooker-online-fd107be8a9b0"
    And The medium parsed item indexed as "1" has content as "Expert?s guide for buying the best inner lid pressure cooker online!", has label as "X_CANDIDATE"
    And The medium parsed item indexed as "1" has "src" as "http://www.medium.com/p/experts-guide-for-buying-the-best-inner-lid-pressure-cooker-online-be507f80613c"
    And The medium parsed item indexed as "2" has content as "Buying 1 Litre Pressure Cooker Online Becomes Easier as India?s Best Brand Provides Online Option!", has label as "X_CANDIDATE"
    And The medium parsed item indexed as "2" has "src" as "http://www.medium.com/p/buying-1-litre-pressure-cooker-online-becomes-easier-as-indias-best-brand-provides-online-option-1b0256ed8631"


  Scenario: Successfully parsing a tag article list
    Given A medium article content as in file "medium/mediumTagArticleList.json"
    And A local content getter for medium parser test
    And A medium parser service implemented from TextContentParser with the regex pattern as "\]\)\}while\(1\);</x>(\{.+\})", URI root as "http://www.medium.com/p"
    When I parse the medium file content
    Then The list of medium parsing results length is as "10"
    And The medium parsed item indexed as "0" has content as "A #100DaysOfCode Timeboxed Front-End Development Curriculum", has label as "X_CANDIDATE"
    And The medium parsed item indexed as "0" has "src" as "http://www.medium.com/p/a-100daysofcode-timeboxed-front-end-development-curriculum-cb4b6c2081c2"
    And The medium parsed item indexed as "5" has content as "????? ?????????? ???????? ? CSS!", has label as "X_CANDIDATE"
    And The medium parsed item indexed as "5" has "src" as "http://www.medium.com/p/logical-css-props-c5046c563640"
    And The medium parsed item indexed as "9" has content as "Build A Real World Beautiful Web APP with Angular 7 ? A to Z Ultimate Guide (2019) ? PART I", has label as "X_CANDIDATE"
    And The medium parsed item indexed as "9" has "src" as "http://www.medium.com/p/build-a-real-world-beautiful-web-app-with-angular-6-a-to-z-ultimate-guide-2018-part-i-e121dd1d55e"


  Scenario: Throw a no paragraphs exception on parsing a member only article
    Given A medium article content as in file "medium/mediumMemberOnlyArticle.json"
    And A local content getter for medium parser test
    And A medium parser service implemented from TextContentParser with the regex pattern as "\]\)\}while\(1\);</x>(\{.+\})", URI root as "http://www.medium.com/p"
    When I parse the medium file content
    Then The parser threw an exception as "com.westial.alexa.jumpandread.application.exception.IteratingNoParagraphsException"
