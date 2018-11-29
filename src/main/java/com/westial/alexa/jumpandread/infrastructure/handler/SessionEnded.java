package com.westial.alexa.jumpandread.infrastructure.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.SessionEndedRequest;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class SessionEnded implements RequestHandler
{
    public static final String INTENT_NAME = "SessionEnded";
    private final State state;

    public SessionEnded(
            State state
    )
    {
        this.state = state;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(requestType(SessionEndedRequest.class));
    }

    public Optional<Response> handle(HandlerInput input)
    {
        state.updateIntent(INTENT_NAME);
        String speech;
        SessionEndedRequest request = (SessionEndedRequest) input
                .getRequestEnvelope().getRequest();

        System.out.format(
                "DEBUG: Handling requested intent custom name as %s\n",
                INTENT_NAME
        );

        switch (request.getReason())
        {
            case ERROR:
                speech = "Ha ocurrido un error y la sesión se va a cerrar.";
                System.out.format(
                        "ERROR: Session error due to %s\n",
                        request.getError()
                );
                break;

            case USER_INITIATED:
                speech = "Hasta pronto.";
                break;

            default:
                return (new Launch(state)).handle(input);
        }

        return input.getResponseBuilder()
                .withSpeech(speech)
                .withShouldEndSession(true)
                .build();
    }
}
