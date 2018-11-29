package com.westial.alexa.jumpandread;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.ResponseEnvelope;
import com.amazon.ask.model.services.Serializer;
import com.amazon.ask.util.JacksonSerializer;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.westial.alexa.jumpandread.domain.Configuration;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.domain.StateFactory;
import com.westial.alexa.jumpandread.domain.StateRepository;
import com.westial.alexa.jumpandread.infrastructure.handler.*;
import com.westial.alexa.jumpandread.infrastructure.service.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class JumpAndReadRouter implements RequestStreamHandler
{
    private static final Serializer serializer = new JacksonSerializer();

    private static final Configuration config = new EnvironmentConfiguration();

    private static final StateRepository stateRepository =
            new DynamoDbStateRepository(config.retrieve("STATE_TABLE_NAME"));

    private static final StateFactory stateFactory = new DynamoDbStateFactory(stateRepository);

    private static final StateManager stateManager = new StateManager(stateFactory);

    protected CandidatesSearchFactory searchFactory;

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

        skill = Skills.standard()
                .addRequestHandlers(
                        new Backward(state, config),
                        new Continue(state, config),
                        new Jump(state, config),
                        new Launch(state),
                        new Pause(state),
                        new Read(state, config),
                        new Repeat(state, config),
                        new Search(state, config, searchFactory),
                        new SessionEnded(state),
                        new Stop(state)
                )
                .build();

        response = skill.invoke(request);

        serializer.serialize(response, output);
    }
}
