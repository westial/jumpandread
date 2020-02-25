package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.SessionEndedRequest;
import com.westial.alexa.jumpandread.application.LaunchUseCase;
import com.westial.alexa.jumpandread.application.SessionEndedUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class SessionEnded implements RequestHandler
{
    public static final String INTENT_NAME = "SessionEnded";
    private final SessionEndedUseCase endedUseCase;
    private final LaunchUseCase launchUseCase;

    public SessionEnded(
            SessionEndedUseCase endedUseCase,
            LaunchUseCase launchUseCase
    )
    {
        this.endedUseCase = endedUseCase;
        this.launchUseCase = launchUseCase;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(requestType(SessionEndedRequest.class));
    }

    public Optional<Response> handle(HandlerInput input)
    {
        SessionEndedRequest request = (SessionEndedRequest) input
                .getRequestEnvelope().getRequest();

        System.out.format(
                "DEBUG: Handling requested intent custom name as %s\n",
                INTENT_NAME
        );

        View view;

        switch (request.getReason())
        {
            case ERROR:
                System.out.format(
                        "ERROR: Session error due to %s\n",
                        request.getError()
                );
                view = endedUseCase.invoke(
                        INTENT_NAME, SessionEndedUseCase.Reason.ERROR
                );
                break;

            case USER_INITIATED:
                view = endedUseCase.invoke(
                        INTENT_NAME, SessionEndedUseCase.Reason.USER_DID
                );
                break;

            default:
                return (new Launch(launchUseCase)).handle(input);
        }

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withShouldEndSession(true)
                .build();
    }
}
