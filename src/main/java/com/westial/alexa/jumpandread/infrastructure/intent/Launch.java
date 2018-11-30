package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.requestType;

public class Launch implements RequestHandler
{
    private State state;
    private final Presenter presenter;
    public static final String INTENT_NAME = "Launch";

    public Launch(
            State state,
            Presenter presenter
    )
    {
        this.state = state;
        this.presenter = presenter;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(
                intentName("AMAZON.HelpIntent")
                        .or(requestType(LaunchRequest.class))
        );
    }

    public Optional<Response> handle(HandlerInput input)
    {
        state.updateIntent(INTENT_NAME);
        presenter.addText(
                "Busca y lee{{ . }}, un buscador que " +
                        "lista tus resultados de búsqueda y entra a leerlos " +
                        "para tí. {{ . }}{{ . }}. Salta esta " +
                        "introducción con la orden " +
                        "{{ . }} Alexa, buscar" +
                        "{{ . }}{{ . }}. Voy a explicarte el " +
                        "funcionamiento básico brevemente." +
                        "{{ . }}{{ . }} Memoriza bien estos tres " +
                        "verbos: buscar, {{ . }} " +
                        "leer,  {{ . }}saltar. " +
                        "{{ . }}{{ . }} Primero buscas con la orden " +
                        "{{ . }}Alexa, buscar " +
                        "{{ . }}{{ . }}. Después escoge uno de los " +
                        "resultados de la lista numerada y dime  " +
                        "{{ . }}Alexa, lee " +
                        "{{ , }} y el número " +
                        "de resultado que quieras leer. {{ . }}{{ . }} " +
                        "Si el contenido que estoy leyendo no " +
                        "te gusta dime:  {{ , }}" +
                        "Alexa, saltar.  {{ . }}{{ . }} Si me " +
                        "paro después de leer un contenido, puedes " +
                        "decirme  {{ , }}Alexa, " +
                        "leer {{ . }} para que " +
                        "continúe en el siguiente contenido. " +
                        "{{ . }}{{ . }}También dispones de " +
                        "otras órdenes como para volver a escuchar el" +
                        " último contenido, con {{ , }}" +
                        "Alexa, repetir{{ . }}{{ . }} O incluso si " +
                        "quieres ir más atrás, dime " +
                        "{{ , }}Alexa, atrás. " +
                        "{{ . }}{{ . }}Y bien, ya puedes empezar " +
                        "a buscar. Empiezas tú, dime " +
                        "{{ , }}Alexa, buscar"
        );
        return input.getResponseBuilder()
                .withSpeech(presenter.output())
                .withReprompt(presenter.output())
                .build();
    }
}
