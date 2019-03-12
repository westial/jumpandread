package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.application.exception.AlreadyStepped;
import com.westial.alexa.jumpandread.domain.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChildrenToSearchCommand
{
    private final CandidateFactory candidateFactory;
    private final CandidateRepository candidateRepository;
    private Set<String> currentUrls;

    public ChildrenToSearchCommand(
            CandidateFactory candidateFactory,
            CandidateRepository candidateRepository
    )
    {
        this.candidateFactory = candidateFactory;
        this.candidateRepository = candidateRepository;
    }

    private boolean isDuplicated(String searchId, String url)
    {
        if (currentUrls.isEmpty())
        {
            currentUrls = candidateRepository.getUniqueUrls(searchId);
        }
        return currentUrls.contains(url);
    }

    public String execute(Candidate parentCandidate) throws AlreadyStepped
    {
        currentUrls = new HashSet<>();
        StringBuilder candidatesList = new StringBuilder();

        int nextIndex = 1 + candidateRepository.lastIndexBySearch(parentCandidate.getSearchId());

        Map<String, Candidate> unlistedCandidates = parentCandidate.getChildren();

        for(Map.Entry<String, Candidate> childEntry: unlistedCandidates.entrySet())
        {
            String url = childEntry.getKey();
            if (isDuplicated(parentCandidate.getSearchId(), url))
            {
                continue;
            }
            Candidate unlistedCandidate = childEntry.getValue();

            Candidate childCandidate = candidateFactory.create(
                    nextIndex,
                    new User(
                            unlistedCandidate.getUserId(),
                            unlistedCandidate.getSessionId()
                    ),
                    parentCandidate.getSearchId(),
                    unlistedCandidate.getTitle(),
                    unlistedCandidate.getUrl(),
                    unlistedCandidate.getDescription()
            );

            childCandidate.persist();
            candidatesList.append(childCandidate.buildListing(Presenter.STRONG_TOKEN));
            currentUrls.add(childCandidate.getUrl());
            nextIndex ++;
        }
        String result = candidatesList.toString();
        if (result.isEmpty())
        {
            throw new AlreadyStepped("This children has already been converted to Candidate");
        }
        return result;
    }
}
