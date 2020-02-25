package com.westial.alexa.jumpandread.domain;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;

public class CandidateContentAddress implements ContentAddress
{
    private final Candidate candidate;

    public CandidateContentAddress(Candidate candidate)
    {
        this.candidate = candidate;
    }

    @Override
    public String getUrl()
    {
        return candidate.getUrl();
    }
}
