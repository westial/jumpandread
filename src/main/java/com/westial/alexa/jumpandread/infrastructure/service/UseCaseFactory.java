package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.application.command.*;
import com.westial.alexa.jumpandread.application.command.move.BackwardCommandFactory;
import com.westial.alexa.jumpandread.application.command.move.ForwardCommandFactory;
import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;
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
    private final ChildrenToSearchCommand childrenCommand;
    private final String dialogSearchWhat;
    private final String dialogWantContinue;
    private final WordsValidator wordsValidator;

    public UseCaseFactory(
            CandidateRepository candidateRepository,
            CandidateFactory candidateFactory,
            CandidatesSearch candidatesSearch,
            State state,
            Presenter presenter,
            int defaultUnsignedCandidatesFactor,
            int defaultUnsignedParagraphsFactor,
            ChildrenToSearchCommand childrenCommand,
            String dialogSearchWhat,
            String dialogWantContinue,
            WordsValidator wordsValidator
    )
    {
        this.candidateRepository = candidateRepository;
        this.candidateFactory = candidateFactory;
        this.candidatesSearch = candidatesSearch;
        this.state = state;
        this.presenter = presenter;
        this.defaultUnsignedCandidatesFactor = defaultUnsignedCandidatesFactor;
        this.defaultUnsignedParagraphsFactor = defaultUnsignedParagraphsFactor;
        this.childrenCommand = childrenCommand;
        this.dialogSearchWhat = dialogSearchWhat;
        this.dialogWantContinue = dialogWantContinue;
        this.wordsValidator = wordsValidator;
    }

    public BackwardUseCase createBackward()
    {
        BackwardCommandFactory backwardCommandFactory =
                new BackwardCommandFactory(
                        candidateFactory,
                        childrenCommand,
                        wordsValidator
                );

        return new BackwardUseCase(
                state,
                backwardCommandFactory,
                presenter,
                2,
                defaultUnsignedParagraphsFactor,
                dialogWantContinue
        );

    }

    public ForwardUseCase createForward()
    {
        ForwardCommandFactory forwardCommandFactory =
                new ForwardCommandFactory(
                        candidateFactory,
                        childrenCommand,
                        wordsValidator
                );

        return new ForwardUseCase(
                state,
                forwardCommandFactory,
                new CountCandidatesBySearchCommand(candidateRepository),
                presenter,
                defaultUnsignedCandidatesFactor,
                defaultUnsignedParagraphsFactor,
                dialogWantContinue
        );
    }

    public CurrentUseCase createCurrent()
    {
        return new CurrentUseCase(
                state,
                new GetCandidateTitleCommand(candidateFactory),
                new CountCandidatesBySearchCommand(candidateRepository),
                new ReadCommand(candidateFactory, childrenCommand, wordsValidator),
                presenter,
                defaultUnsignedCandidatesFactor,
                defaultUnsignedParagraphsFactor,
                dialogWantContinue
        );
    }

    public GettingListUseCase createList()
    {
        return new GettingListUseCase(
                state,
                new GettingListCommand(candidateRepository),
                presenter
        );
    }

    public SearchUseCase createSearch()
    {
        return new SearchUseCase(
                state,
                new SearchCandidatesCommand(candidatesSearch, wordsValidator),
                presenter,
                dialogSearchWhat
        );
    }

    public FallbackUseCase createFallback()
    {
        return new FallbackUseCase(
                state,
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

    public HelpUseCase createHelp()
    {
        return new HelpUseCase(state, presenter);
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
