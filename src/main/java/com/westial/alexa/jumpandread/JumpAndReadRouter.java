package com.westial.alexa.jumpandread;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.ResponseEnvelope;
import com.amazon.ask.model.services.Serializer;
import com.amazon.ask.util.JacksonSerializer;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.CommandFactory;
import com.westial.alexa.jumpandread.infrastructure.intent.*;
import com.westial.alexa.jumpandread.infrastructure.service.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class JumpAndReadRouter implements RequestStreamHandler
{
    private static final Serializer serializer = new JacksonSerializer();

    private static Configuration config = new EnvironmentConfiguration();

    private static final StateRepository stateRepository =
            new DynamoDbStateRepository(config.retrieve("STATE_TABLE_NAME"));

    private static final StateFactory stateFactory = new DynamoDbStateFactory(stateRepository);

    private static final StateManager stateManager = new StateManager(stateFactory);

    protected CandidatesSearchFactory searchFactory;

    private Presenter presenter;

    public final void jumpAndRead(InputStream input, OutputStream output, Context context) throws IOException
    {
        ResponseEnvelope response;
        Skill skill;

        RequestEnvelope request = serializer.deserialize(input, RequestEnvelope.class);

        State state = stateManager.provide(
                request.getSession().getUser().getUserId(),
                request.getSession().getSessionId()
        );

        System.out.printf(
                "DEBUG: Request.toString = %s\n",
                request.getRequest().toString()
        );

        // Define dependencies

        CandidateParser candidateParser = new JsoupCandidateParser();
        CandidateGetter candidateGetter = new UnirestCandidateGetter(
                config.retrieve("HTTP_USER_AGENT")
        );

        CandidateRepository candidateRepository = new DynamoDbCandidateRepository(
                config.retrieve("CANDIDATE_TABLE_NAME")
        );
        CandidateFactory candidateFactory = new DynamoDbCandidateFactory(
                candidateGetter,
                candidateParser,
                candidateRepository
        );

        CandidatesSearch candidatesSearch = searchFactory.create(
                config,
                candidateFactory
        );

        presenter = new AlexaPresenter(
                new FromJsonFileTranslator(
                        config.retrieve("ISO4_LANGUAGE"),
                        config.retrieve("LOCALES_FILENAME")
                )
        );

        // Create Commands

        CommandFactory commandFactory = new CommandFactory(
                candidateFactory,
                candidatesSearch
        );

        RewindCommand rewindCommand = commandFactory.createRewindCommand(
                Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
        );

        NextReadingCommandContract continueCommand = commandFactory.createContinueCommand(
                state.getIntent(),
                Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
        );

        JumpCommand jumpCommand = commandFactory.createJumpCommand(
                Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
        );

        ReadCommand readCommand = commandFactory.createReadCommand(
                Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
        );

        SearchCandidatesCommand searchCommand = commandFactory.createSearchCommand();

        // Create Skill

        skill = Skills.standard()
                .addRequestHandlers(
                        new Backward(state, rewindCommand, presenter),
                        new Continue(state, continueCommand, presenter),
                        new Jump(state, jumpCommand, presenter),
                        new Launch(state, presenter),
                        new Pause(state, presenter),
                        new Read(state, readCommand, presenter),
                        new Repeat(state, rewindCommand, presenter),
                        new Search(state, searchCommand, presenter),
                        new SessionEnded(state, presenter),
                        new Stop(state, presenter)
                )
                .build();

        response = skill.invoke(request);

        serializer.serialize(response, output);
    }
}
