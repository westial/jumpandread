package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import utils.RandomContent;

import java.util.ArrayList;
import java.util.List;

public class MockCandidatesSearch implements CandidatesSearch
{
    private final int forcedResults;
    private final CandidateFactory candidateFactory;

    public MockCandidatesSearch(
            int forcedResults,
            CandidateFactory candidateFactory
    )
    {
        this.forcedResults = forcedResults;
        this.candidateFactory = candidateFactory;
    }

    public List<Candidate> find(User user, String searchId, String terms) throws SearchException, NoSearchResultsException
    {
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
            description = RandomContent.createParagraph(3, 5).getContent();
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

