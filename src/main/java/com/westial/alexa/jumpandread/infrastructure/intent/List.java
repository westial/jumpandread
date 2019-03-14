//package com.westial.alexa.jumpandread.infrastructure.intent;
//
//import com.amazon.ask.dispatcher.request.handler.HandlerInput;
//import com.amazon.ask.dispatcher.request.handler.RequestHandler;
//import com.amazon.ask.model.LaunchRequest;
//import com.amazon.ask.model.Response;
//import com.westial.alexa.jumpandread.application.GettingListUseCase;
//import com.westial.alexa.jumpandread.application.LaunchUseCase;
//import com.westial.alexa.jumpandread.application.View;
//
//import java.util.Optional;
//
//import static com.amazon.ask.request.Predicates.intentName;
//import static com.amazon.ask.request.Predicates.requestType;
//
//public class List implements RequestHandler
//{
//    public static final String INTENT_NAME = "List";
//    private final GettingListUseCase listUseCase;
//
//    public List(
//            GettingListUseCase listUseCase
//    )
//    {
//        this.listUseCase = listUseCase;
//    }
//
//    public boolean canHandle(HandlerInput input)
//    {
//        return input.matches(
//                intentName("AMAZON.HelpIntent")
//                        .or(requestType(LaunchRequest.class))
//        );
//    }
//
//    public Optional<Response> handle(HandlerInput input)
//    {
//        View view = listUseCase.invoke(INTENT_NAME);
//
//        return input.getResponseBuilder()
//                .withSpeech(view.getSpeech())
//                .withReprompt(view.getSpeech())
//                .build();
//    }
//}
