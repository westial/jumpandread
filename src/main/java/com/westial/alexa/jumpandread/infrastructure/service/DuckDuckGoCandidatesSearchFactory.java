package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.Configuration;

public class DuckDuckGoCandidatesSearchFactory implements CandidatesSearchFactory
{
    @Override
    public CandidatesSearch create(Configuration config, CandidateFactory candidateFactory)
    {
        return new DuckDuckGoCandidatesSearch(
                Integer.parseInt(config.retrieve("STARTING_CANDIDATE_INDEX")),
                new UnirestWebClient(),
                new JsoupDuckDuckGoResultParser(),
                config.retrieve("DUCK_URL"),
                new RandomDuckDuckGoHeadersProvider(
                        config.retrieve("AGENTS_RESOURCE_PATH"),
                        config.retrieve("LANGUAGES_RESOURCE_PATH"),
                        config.retrieve("REFERRERS_RESOURCE_PATH")
                ),
                new RandomDuckDuckGoLocaleProvider(config.retrieve("DUCK_LOCALE_RESOURCE_PATH")),
                config.retrieve("ISO4_LANGUAGE"),
                candidateFactory
        );
    }
}
