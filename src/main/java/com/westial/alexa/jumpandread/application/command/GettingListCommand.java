package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.application.exception.FromRepositoryNoSearchResultException;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import com.westial.alexa.jumpandread.domain.Presenter;

import java.util.List;

public class GettingListCommand
{
    private final CandidateRepository candidateRepository;

    public GettingListCommand(CandidateRepository candidateRepository)
    {
        this.candidateRepository = candidateRepository;
    }

    public String execute(String searchId) throws NoSearchResultException
    {
        StringBuilder candidatesList = new StringBuilder();
        List<Candidate> candidates = candidateRepository.all(searchId);

        if (null == candidates || candidates.isEmpty())
        {
            throw new FromRepositoryNoSearchResultException(
                    String.format(
                            "No candidates for search Id as %s",
                            searchId
                    )
            );
        }

        for(Candidate candidate: candidates)
        {
            candidatesList.append(candidate.buildListing(Presenter.STRONG_TOKEN));
        }

        return candidatesList.toString();
    }
}
