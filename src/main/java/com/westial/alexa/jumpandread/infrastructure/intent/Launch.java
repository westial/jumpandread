package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.OutputFormatter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.requestType;

public class Launch implements RequestHandler
{
    private State state;
    private final OutputFormatter outputFormatter;
    public static final String INTENT_NAME = "Launch";

    public Launch(
            State state,
            OutputFormatter outputFormatter
    )
    {
        this.state = state;
        this.outputFormatter = outputFormatter;
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
        String intro = "Busca y lee<break strength=\"x-strong\"/>, un buscador que lista tus resultados de búsqueda y entra a leerlos para tí. <break time=\"2s\"/>. Salta esta introducción con la orden <break strength=\"x-strong\"/> Alexa, buscar<break time=\"3s\"/>. Voy a explicarte el funcionamiento básico brevemente.<break time=\"2s\"/> Memoriza bien estos tres verbos: buscar, <break strength=\"x-strong\"/> leer,  <break strength=\"x-strong\"/>saltar. <break time=\"3s\"/> Primero buscas con la orden <break strength=\"x-strong\"/>Alexa, buscar <break time=\"2s\"/>. Después escoge uno de los resultados de la lista numerada y dime  <break strength=\"x-strong\"/>Alexa, lee <break strength=\"strong\"/> y el número de resultado que quieras leer. <break time=\"2s\"/> Si el contenido que estoy leyendo no te gusta dime:  <break strength=\"strong\"/>Alexa, saltar.  <break time=\"2s\"/> Si me paro después de leer un contenido, puedes decirme  <break strength=\"strong\"/>Alexa, leer <break strength=\"x-strong\"/> para que continúe en el siguiente contenido. <break time=\"2s\"/>También dispones de otras órdenes como para volver a escuchar el último contenido, con <break strength=\"strong\"/>Alexa, repetir<break time=\"2s\"/> O incluso si quieres ir más atrás, dime <break strength=\"strong\"/>Alexa, atrás. <break time=\"3s\"/>Y bien, ya puedes empezar a buscar. Empiezas tú, dime <break strength=\"strong\"/>Alexa, buscar";
        intro = outputFormatter.envelop(
                intro
        );
        return input.getResponseBuilder()
                .withSpeech(intro)
                .withReprompt(intro)
                .build();
    }
}
