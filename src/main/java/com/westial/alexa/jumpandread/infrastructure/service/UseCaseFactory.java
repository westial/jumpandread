package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.application.command.CountCandidatesBySearchCommand;
import com.westial.alexa.jumpandread.application.command.GetCandidateTitleCommand;
import com.westial.alexa.jumpandread.application.command.ReadCommand;
import com.westial.alexa.jumpandread.application.command.SearchCandidatesCommand;
import com.westial.alexa.jumpandread.application.command.move.BackwardCommandFactory;
import com.westial.alexa.jumpandread.application.command.move.ForwardCommandFactory;
import com.westial.alexa.jumpandread.domain.*;

public class UseCaseFactory
{
    private final CandidateRepository candidateRepository;
    private CandidateFactory candidateFactory;
    private final CandidatesSearch candidatesSearch;
    private final State state;
    private final Presenter presenter;
    private final int defaultUnsignedCandidatesFactor;
    private final int defaultUnsignedParagraphsFactor;

    public UseCaseFactory(
            CandidateRepository candidateRepository,
            CandidateFactory candidateFactory,
            CandidatesSearch candidatesSearch,
            State state,
            Presenter presenter,
            int defaultUnsignedCandidatesFactor,
            int defaultUnsignedParagraphsFactor
    )
    {
        this.candidateRepository = candidateRepository;
        this.candidateFactory = candidateFactory;
        this.candidatesSearch = candidatesSearch;
        this.state = state;
        this.presenter = presenter;
        this.defaultUnsignedCandidatesFactor = defaultUnsignedCandidatesFactor;
        this.defaultUnsignedParagraphsFactor = defaultUnsignedParagraphsFactor;
    }

    public BackwardUseCase createBackward()
    {
        BackwardCommandFactory backwardCommandFactory =
                new BackwardCommandFactory(candidateFactory);

        return new BackwardUseCase(
                state,
                backwardCommandFactory,
                presenter,
                2,
                defaultUnsignedParagraphsFactor
                );

    }

    public ForwardUseCase createForward()
    {
        ForwardCommandFactory forwardCommandFactory =
                new ForwardCommandFactory(candidateFactory);

        return new ForwardUseCase(
                state,
                forwardCommandFactory,
                presenter,
                defaultUnsignedCandidatesFactor,
                defaultUnsignedParagraphsFactor
        );
    }

    public CurrentUseCase createCurrent()
    {
        return new CurrentUseCase(
                state,
                new GetCandidateTitleCommand(candidateFactory),
                new CountCandidatesBySearchCommand(candidateRepository),
                new ReadCommand(candidateFactory),
                presenter,
                defaultUnsignedCandidatesFactor,
                defaultUnsignedParagraphsFactor
        );
    }

    public SearchUseCase createSearch()
    {
        return new SearchUseCase(
                state,
                new SearchCandidatesCommand(candidatesSearch),
                presenter
        );
    }

    public PauseUseCase createPause()
    {
        return new PauseUseCase(state, candidateFactory, presenter);
    }

    public LaunchUseCase createLaunch()
    {
        return new LaunchUseCase(state, presenter);
    }

    public StopUseCase createStop()
    {
        return new StopUseCase(state, presenter);
    }

    public SessionEndedUseCase createEnded()
    {
        return new SessionEndedUseCase(state, presenter);
    }
}
