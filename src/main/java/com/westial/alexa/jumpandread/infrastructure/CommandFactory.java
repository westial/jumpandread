package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.infrastructure.intent.Pause;

public class CommandFactory
{
    private CandidateFactory candidateFactory;
    private final CandidatesSearch candidatesSearch;

    public CommandFactory(
            CandidateFactory candidateFactory,
            CandidatesSearch candidatesSearch
    )
    {
        this.candidateFactory = candidateFactory;
        this.candidatesSearch = candidatesSearch;
    }

    public RewindCommand createRewindCommand(int paragraphsGroup)
    {
        return new RewindCommand(candidateFactory, paragraphsGroup,2);

    }

    public NextReadingCommandContract createContinueCommand(
            String lastIntent,
            int paragraphsGroup
    )
    {
        if (null != lastIntent && lastIntent.equals(Pause.INTENT_NAME))
        {
            return new RewindCommand(candidateFactory, paragraphsGroup,1);
        }
        else
        {
            return new NextCommand(candidateFactory, paragraphsGroup);
        }
    }

    public JumpCommand createJumpCommand(int paragraphsGroup)
    {
        return new JumpCommand(candidateFactory, paragraphsGroup);
    }

    public ReadCommand createReadCommand(int paragraphsGroup)
    {
        return new ReadCommand(candidateFactory, paragraphsGroup);
    }

    public SearchCandidatesCommand createSearchCommand()
    {
        return new SearchCandidatesCommand(
                candidatesSearch
        );
    }
}
