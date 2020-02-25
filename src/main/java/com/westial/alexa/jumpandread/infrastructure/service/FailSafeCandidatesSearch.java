package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.User;
import com.westial.alexa.jumpandread.infrastructure.exception.EngineNoSearchResultException;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FailSafeCandidatesSearch implements CandidatesSearch
{
    private final LinkedList<CandidatesSearch> engines;

    public FailSafeCandidatesSearch(LinkedList<CandidatesSearch> engines)
    {
        this.engines = engines;
    }

    @Override
    public List<Candidate> find(User user, String searchId, String terms)
            throws SearchException, NoSearchResultException
    {
        Iterator<CandidatesSearch> enginesIterator = engines.iterator();

        while (enginesIterator.hasNext())
        {
            CandidatesSearch engine = enginesIterator.next();

            try
            {
                return engine.find(user, searchId, terms);
            }
            catch (Exception any)
            {
                if (!enginesIterator.hasNext())
                {
                    throw any;
                }
            }
        }

        throw new EngineNoSearchResultException(
                "No results over fail safe search engine"
        );
    }
}
