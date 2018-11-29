package com.westial.alexa.jumpandread.infrastructure.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.User;

import java.io.IOException;
import java.net.URISyntaxException;
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
    private final static int GOOGLE_RESULTS_BY_QUERY = 10;
    private final String googleLr;

    public GoogleCandidatesSearch(
            int startingIndex,
            String googleKey,
            String googleCx,
            String iso4Language,
            CandidateFactory candidateFactory)
    {
        this.startingIndex = startingIndex;
        this.googleKey = googleKey;
        this.googleCx = googleCx;
        this.googleLr = formatLanguage(iso4Language);

        this.candidateFactory = candidateFactory;
        NetHttpTransport.Builder netBuilder = new NetHttpTransport.Builder();
        this.customsearch = new Customsearch(netBuilder.build(), new JacksonFactory(), httpRequest -> {
        });
    }

    private static String formatLanguage(String iso4Language)
    {
        return "lang_" + iso4Language.substring(0, 2);
    }

    @Override
    public List<Candidate> find(User user, String searchId, String terms)
    {
        List<Candidate> candidates = new ArrayList<>();
        Result result;
        Candidate candidate;
        try
        {
            LinkedList<Result> results = new LinkedList<>(
                    search(terms, GOOGLE_RESULTS_BY_QUERY)
            );
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
        } catch (IOException e)
        {
            // FIXME exceptions management
            e.printStackTrace();
        } catch (URISyntaxException e)
        {
            // FIXME exceptions management
            e.printStackTrace();
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
    private List<Result> search(String query, int numOfResults) throws IOException, URISyntaxException
    {
        List<Result> results = new ArrayList<>();
        List<Result> lastResults;

        Customsearch.Cse.List list = customsearch.cse().list(query);

        list.setKey(googleKey);
        list.setCx(googleCx);
        list.setLr(googleLr);

        //Exact terms
        for(long i = 1 ; i < numOfResults ; i += GOOGLE_RESULTS_BY_QUERY){
            list.setStart(i);
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
