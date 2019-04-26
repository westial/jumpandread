package com.westial.alexa.jumpandread.infrastructure.service;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.User;
import com.westial.alexa.jumpandread.infrastructure.exception.EngineNoSearchResultException;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import com.westial.alexa.jumpandread.infrastructure.exception.WebClientSearchException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GoogleCandidatesSearch implements CandidatesSearch
{
    private final Customsearch customsearch;
    private final int startingIndex;
    private final String googleKey;
    private final String googleCx;
    private final CandidateFactory candidateFactory;
    private final int resultsBySearch;
    private final String googleLr;
    private final String dork;

    // Immutable, see https://developers.google.com/custom-search/json-api/v1/reference/cse/list#num
    private final static int GOOGLE_RESULTS_BY_QUERY = 10;

    public GoogleCandidatesSearch(
            int startingIndex,
            String googleKey,
            String googleCx,
            String googleAppName,
            String iso4Language,
            CandidateFactory candidateFactory,
            int resultsBySearch,
            String dork)
    {
        this.startingIndex = startingIndex;
        this.googleKey = googleKey;
        this.googleCx = googleCx;
        this.googleLr = formatLanguage(iso4Language);

        this.candidateFactory = candidateFactory;
        this.resultsBySearch = resultsBySearch;

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        this.customsearch = new Customsearch.Builder(
                httpTransport,
                jsonFactory,
                null
        ).setApplicationName(googleAppName).build();

        this.dork = (null == dork || dork.isEmpty()) ? "" : String.format(" AND (%s)", dork);
    }

    private static String formatLanguage(String iso4Language)
    {
        return "lang_" + iso4Language.substring(0, 2);
    }

    @Override
    public List<Candidate> find(User user, String searchId, String terms) throws SearchException, NoSearchResultException
    {
        List<Candidate> candidates = new ArrayList<>();
        Result result;
        Candidate candidate;
        try
        {
            LinkedList<Result> results = new LinkedList<>(
                    search(
                            String.format("%s%s", terms, dork),
                            resultsBySearch
                    )
            );
            if (results.isEmpty())
            {
                throw new EngineNoSearchResultException("No result in Google searching results page");
            }
            for (int resultsIndex = startingIndex; !results.isEmpty() ; resultsIndex ++)
            {
                result = results.pop();
                candidate = candidateFactory.create(
                        resultsIndex,
                        user,
                        searchId,
                        result.getTitle(),
                        result.getFormattedUrl(),
                        result.getSnippet()
                );
                candidates.add(candidate);
            }
        } catch (IOException sdkException)
        {
            throw new WebClientSearchException(
                    sdkException.getMessage()
            );
        }
        return candidates;
    }

    /**
     * Search Google CSE for query
     * @param query to search for
     * @param numOfResults of results to return
     * @return List of results
     * @throws IOException
     */
    private List<Result> search(String query, int numOfResults) throws IOException
    {
        List<Result> results = new ArrayList<>();
        List<Result> lastResults;

        Customsearch.Cse.List list = customsearch.cse().list(query);

        list.setKey(googleKey);
        list.setCx(googleCx);
        list.setLr(googleLr);

        // Exact terms
        for(long index = 1 ; index < numOfResults ; index += GOOGLE_RESULTS_BY_QUERY){
            list.setStart(index);
            lastResults = list.execute().getItems();
            if (null == lastResults)
            {
                break;
            }
            results.addAll(lastResults);
            if (lastResults.size() < GOOGLE_RESULTS_BY_QUERY)
            {
                break;
            }
        }

        return results;
    }
}
