package com.westial.alexa.jumpandread;

import com.amazonaws.services.lambda.runtime.Context;
import com.westial.alexa.jumpandread.infrastructure.service.DuckDuckGoCandidatesSearchFactory;
import com.westial.alexa.jumpandread.infrastructure.service.WebSearchContentParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebSearchDuckDuckGoJumpAndReadRouter extends JumpAndReadRouter
{
    @Override
    public final void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        searchFactory = new DuckDuckGoCandidatesSearchFactory();
        contentParser = new WebSearchContentParser();
        super.jumpAndRead(inputStream, outputStream, context);
    }
}
