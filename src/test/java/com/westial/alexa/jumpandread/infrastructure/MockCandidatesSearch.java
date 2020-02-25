package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import utils.RandomContent;

import java.util.ArrayList;
import java.util.List;

public class MockCandidatesSearch implements CandidatesSearch
{
    private final SearchException forcedSearchException;
    private final NoSearchResultException forcedNoResultException;
    private int forcedResults;
    private final CandidateFactory candidateFactory;

    public MockCandidatesSearch(
            int forcedResults,
            CandidateFactory candidateFactory
    )
    {
        this.forcedResults = forcedResults;
        this.candidateFactory = candidateFactory;
        forcedSearchException = null;
        forcedNoResultException = null;
    }

    public MockCandidatesSearch(
            SearchException forcedSearchException
    )
    {
        this.forcedSearchException = forcedSearchException;
        forcedNoResultException = null;
        candidateFactory = null;
    }

    public MockCandidatesSearch(
            NoSearchResultException forcedNoResultException
    )
    {
        this.forcedNoResultException = forcedNoResultException;
        forcedSearchException = null;
        candidateFactory = null;
    }

    public List<Candidate> find(User user, String searchId, String terms) throws SearchException, NoSearchResultException
    {
        if (null != forcedSearchException)
        {
            throw forcedSearchException;
        }
        
        if (null != forcedNoResultException)
        {
            throw forcedNoResultException;
        }
        
        if (0 == forcedResults)
        {
            return null;
        }

        List<Candidate> candidates = new ArrayList<>();
        String title;
        String description;
        String url;

        for (int i = 0; i < forcedResults; i++)
        {
            title = RandomContent.randomWord();
            description = RandomContent.createParagraph(3, 5).getContent().getText();
            url = String.format(
                    "https://www.%s.%s/%s",
                    RandomContent.randomWord(),
                    RandomContent.randomWord(),
                    RandomContent.randomWord()
            );
            candidates.add(candidateFactory.create(
                    i + Candidate.INDEX_START,
                    user,
                    searchId,
                    title,
                    url,
                    description
            ));
        }

        return candidates;
    }
}

