Feature: Search some terms with a searching service

  Scenario: Properly parse all results from a given DuckDuckGo results page
    Given A local web client service with a forced content as in file as "duckduckgo/recetas melones at DuckDuckGo.html"
    And A random headers provider service for agents file as "useragent.pc.list", languages file as "languages.list", referrers file as "referrers.list"
    And A random duckduckgo locale provider service for available locales file as "duckduckgo.kl.lang.es.list"
    And A DuckDuckGo page parser service
    And A mock candidate parser
    And A candidate document getter
    And An Alexa output formatter for searching
    And A candidate repository
    And A candidate factory
    And A DuckDuckGo candidates search service for url as "http://duck.dot", iso 4-letters locale as "es-ES"
    When I ask to find candidates to search service for user with ID as "userid1234-1234-1234", session ID as "sessionid1234-1234-1234", search ID as "searchid1234-1234-1234", terms as "word1 word2 word3"
    Then The service returned a list with "29" candidates
    And Candidate with position "3" in list has property "title" as "Recetas de Melones - recetasmelones.blogspot.com"
    And Candidate with position "17" in list has property "description" as "Para hacer la sopa de melón, lo primero que tienes que hacer es retirar toda la piel de los melones, que en esta variedad es una piel bastante fina. Una vez limpios, el siguiente paso es trocear la pulpa y pasarla por la batidora."
    And Candidate with position "19" in list has property "url" as "https://www.petitchef.es/recetas/recetas-con-melon"
    And Candidate with position "23" in list has property "title" as "Cómo distinguir un melón maduro - YouTube"
    And Candidate with position "28" in list has property "url" as "https://elcomidista.elpais.com/elcomidista/2013/08/20/receta/1376974800_137697.html"

  Scenario: No candidates for a given DuckDuckGo empty results page
    Given A local web client service with a forced content as in file as "duckduckgo/recetas no results at DuckDuckGo.html"
    And A random headers provider service for agents file as "useragent.pc.list", languages file as "languages.list", referrers file as "referrers.list"
    And A random duckduckgo locale provider service for available locales file as "duckduckgo.kl.lang.es.list"
    And A DuckDuckGo page parser service
    And A mock candidate parser
    And A candidate document getter
    And An Alexa output formatter for searching
    And A candidate repository
    And A candidate factory
    And A DuckDuckGo candidates search service for url as "http://duck.dot", iso 4-letters locale as "es-ES"
    When I ask to find candidates to search service for user with ID as "userid1234-1234-1234", session ID as "sessionid1234-1234-1234", search ID as "searchid1234-1234-1234", terms as "word1 word2 word3"
    Then The service returned no candidates

  Scenario: Search engine is invoked with some terms and returns a candidates collection with some items
    Given A mock candidate parser
    And An Alexa output formatter for searching
    And A candidate document getter
    And A user state repository
    And A user state factory
    And A state with the user as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session as "12345678-1234-4ea6-bfca-347ca7612345" and intent as "SearchCandidates"
    And A candidate repository
    And A candidate factory
    And A candidates search service with "3" forced results
    And A searching step command
    When I execute the step command with the terms "mis palabras de búsqueda"
    Then Searching command returned a text with points "1" to "3"
    And The current intent in state repository is "SearchCandidates"
    And Candidate repository contains exactly "3" candidates

  Scenario: Search engine is invoked with some terms and returns a candidates collection with only one item
    Given A mock candidate parser
    And An Alexa output formatter for searching
    And A candidate document getter
    And A user state repository
    And A user state factory
    And A state with the user as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session as "12345678-1234-4ea6-bfca-347ca7612345" and intent as "SearchCandidates"
    And A candidate repository
    And A candidate factory
    And A candidates search service with "1" forced results
    And A searching step command
    When I execute the step command with the terms "mis palabras de búsqueda"
    Then Searching command returned a text with points "1" to "1"
    And The current intent in state repository is "SearchCandidates"
    And Candidate repository contains exactly "1" candidates

  Scenario: Search engine is invoked with some terms and returns nothing
    Given A mock candidate parser
    And An Alexa output formatter for searching
    And A candidate document getter
    And A user state repository
    And A user state factory
    And A state with the user as "ab551872-0a24-4ea6-bfca-347ca76ee8ce", session as "12345678-1234-4ea6-bfca-347ca7612345" and intent as "SearchCandidates"
    And A candidate repository
    And A candidate factory
    And A candidates search service with "0" forced results
    And A searching step command
    When I execute the step command with the terms "mis palabras de búsqueda"
    Then Command returns nothing
    And The current intent in state repository is "SearchCandidates"