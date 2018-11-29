package com.westial.alexa.jumpandread.domain;

public interface CandidateRepository
{
    void update(Candidate candidate);

    Candidate get(String searchId, Integer index);

    int countBySearch(String searchId);
}
