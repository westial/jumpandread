package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.Configuration;

public class GoogleCandidatesSearchFactory implements CandidatesSearchFactory
{
    @Override
    public CandidatesSearch create(Configuration config, CandidateFactory candidateFactory)
    {
        TextCleaner titleCleaner = new RegexTextCleaner(
                config.retrieve("TITLE_CLEANER_EXTRACT_PATTERN")
        );
        return new GoogleCandidatesSearch(
                Integer.parseInt(config.retrieve("STARTING_CANDIDATE_INDEX")),
                config.retrieve("GOOGLE_KEY"),
                config.retrieve("GOOGLE_CX"),
                config.retrieve("GOOGLE_APP_NAME"),
                config.retrieve("ISO4_LANGUAGE"),
                SafeSearchConfiguration.read(
                        config.retrieve(
                                "ENABLED_SAFE_SEARCH",
                                "off"
                        )
                ),
                candidateFactory,
                Integer.parseInt(config.retrieve("GOOGLE_RESULTS_BY_SEARCH")),
                config.retrieve("GOOGLE_DORK", null),
                titleCleaner
        );
    }
}
