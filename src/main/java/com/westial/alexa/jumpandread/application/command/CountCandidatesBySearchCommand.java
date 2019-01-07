package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.domain.CandidateRepository;

public class CountCandidatesBySearchCommand
{
    private CandidateRepository repository;

    public CountCandidatesBySearchCommand(CandidateRepository repository)
    {
        this.repository = repository;
    }

    public Integer execute(String searchId)
    {
        return repository.countBySearch(searchId);
    }
}
