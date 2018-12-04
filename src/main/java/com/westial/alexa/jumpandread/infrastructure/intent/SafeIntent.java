package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.MandatorySearchException;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.application.CannotContinueMandatoryReadException;

import java.util.Optional;

abstract class SafeIntent implements RequestHandler
{
    protected final Presenter presenter;

    public SafeIntent(Presenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public Optional<Response> handle(HandlerInput input)
    {
        try
        {
            return safeHandle(input);
        } catch (CannotContinueMandatoryReadException excReading)
        {
            excReading.printStackTrace();
            presenter.addText("notice.no.more.paragraphs");

        } catch (MandatorySearchException mandatoryExc)
        {
            mandatoryExc.printStackTrace();

            presenter.addText("warning.something.unexpected");
        }

        return input.getResponseBuilder()
                .withSpeech(presenter.output())
                .withReprompt(presenter.output())
                .build();
    }

    protected abstract Optional<Response> safeHandle(HandlerInput input);
}
