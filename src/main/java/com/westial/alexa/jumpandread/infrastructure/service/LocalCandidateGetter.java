package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.exception.GettingCandidateContentException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateGetter;

import java.io.IOException;

@SuppressWarnings("ALL")
public class LocalCandidateGetter implements CandidateGetter
{
    @Override
    public String getContent(Candidate candidate) throws GettingCandidateContentException
    {
        try {
            return FileSystemService.readFileByUrl(candidate.getUrl());
        } catch (IOException exception) {
            throw new GettingCandidateContentException(exception.getMessage());
        }
    }
}
