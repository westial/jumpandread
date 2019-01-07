package com.westial.alexa.jumpandread.domain;

import com.westial.alexa.jumpandread.application.exception.GettingCandidateContentException;

public interface CandidateGetter
{
    String getContent(Candidate candidate) throws GettingCandidateContentException;
}
