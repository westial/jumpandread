package com.westial.alexa.jumpandread.domain;

public interface CandidateFactory
{
    Candidate create(
            Integer index,
            User user,
            String searchId,
            String title,
            String url,
            String description
    );

    Candidate create(
            Integer index,
            User user,
            String searchId
    ) throws NoCandidateException;
}
