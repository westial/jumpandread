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

            presenter.addText(
                    "Disculpa, no puedo continuar leyendo porque sigo " +
                            "sin encontrar texto para leer en el resultado " +
                            "seleccionado. Puedes pedirme de saltar otra vez " +
                            "con la orden: Alexa salta. O también puedes " +
                            "volver leer cualquier otro resultado con la " +
                            "orden: Alexa lee, y el número de resultado que " +
                            "quieras leer. O, también puedes volver a buscar " +
                            "otra cosa con la orden: Alexa buscar, y las " +
                            "palabras que quieras buscar."
            );
        } catch (MandatorySearchException mandatoryExc)
        {
            mandatoryExc.printStackTrace();

            presenter.addText(
                    "Disculpa, algo raro ha pasado... " +
                            "o no encuentro tu última búsqueda, o " +
                            "no veo que queden más resultados en ella," +
                            " o has pedido leer un número de resultado que " +
                            "no es correcto. " +
                            "Si quieres buscar otra cosa, dame la " +
                            "orden: Alexa buscar, y las palabras que " +
                            "quieras buscar."
            );
        }

        return input.getResponseBuilder()
                .withSpeech(presenter.output())
                .withReprompt(presenter.output())
                .build();
    }

    protected abstract Optional<Response> safeHandle(HandlerInput input);
}
