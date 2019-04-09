package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import com.westial.alexa.jumpandread.infrastructure.structure.DuckDuckGoResult;
import com.westial.alexa.jumpandread.infrastructure.structure.HttpMethod;

import java.util.*;

public class DuckDuckGoCandidatesSearch implements CandidatesSearch
{
    private int startingIndex;
    private final WebClient pageClient;
    private final DuckDuckGoResultParser pageParser;
    private final String duckUrl;
    private final HeadersProvider headersProvider;
    private final DuckDuckGoLocaleProvider duckGoLocaleProvider;
    private final String iso4Language;
    private final CandidateFactory candidateFactory;
    private final String dork;

    public DuckDuckGoCandidatesSearch(
            int startingIndex,
            WebClient pageClient,
            DuckDuckGoResultParser pageParser,
            String duckUrl,
            HeadersProvider headersProvider,
            DuckDuckGoLocaleProvider duckGoLocaleProvider,
            String iso4Language,
            CandidateFactory candidateFactory,
            String dork
    )
    {
        this.startingIndex = startingIndex;
        this.pageClient = pageClient;
        this.pageParser = pageParser;
        this.duckUrl = duckUrl;
        this.headersProvider = headersProvider;
        this.duckGoLocaleProvider = duckGoLocaleProvider;
        this.iso4Language = iso4Language;
        this.candidateFactory = candidateFactory;
        this.dork = (null == dork) ? "" : String.format(" AND (%s)", dork);
    }

    @Override
    public List<Candidate> find(User user, String searchId, String terms) throws SearchException, NoSearchResultException
    {
        LinkedList<DuckDuckGoResult> results;
        List<Candidate> candidates = new ArrayList<>();
        Candidate candidate;

        Map<String, String> payload = new HashMap<>();
        payload.put("q", String.format("%s%s", terms, dork));
        payload.put("b", "");
        payload.put("kl", duckGoLocaleProvider.provide());

        String content = pageClient.request(
                HttpMethod.POST,
                duckUrl,
                headersProvider.provide(iso4Language),
                null,
                payload
        );

        results = new LinkedList<>(
                pageParser.parse(content)
        );

        for (int resultsIndex = startingIndex; !results.isEmpty() ; resultsIndex ++)
        {
            DuckDuckGoResult result = results.pop();
            if (null == result)
            {
                continue;
            }
            candidate = candidateFactory.create(
                    resultsIndex,
                    user,
                    searchId,
                    result.getTitle(),
                    result.getUrl(),
                    result.getDescription()
            );
            candidates.add(candidate);
        }
        return candidates;
    }
}
