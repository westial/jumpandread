package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.westial.alexa.jumpandread.application.SearchUseCase;

/**
 * This intent has to be configured alone because always can handle itself.
 * It's an intent to force user search something. Used, for example, when a new
 * user tries request an intent that requires some steps before.
 */
public class FallbackSearch extends Search implements RequestHandler
{
    public static final String INTENT_NAME = "FallbackSearch";


    public FallbackSearch(SearchUseCase searchUseCase)
    {
        super(searchUseCase);
    }

    @Override
    public boolean canHandle(HandlerInput input)
    {
        return true;
    }
}
