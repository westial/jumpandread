package com.westial.alexa.jumpandread;

import com.amazonaws.services.lambda.runtime.Context;
import com.westial.alexa.jumpandread.infrastructure.service.CandidatesSearchFactory;
import com.westial.alexa.jumpandread.infrastructure.service.DuckDuckGoCandidatesSearchFactory;
import com.westial.alexa.jumpandread.infrastructure.service.FailSafeCandidatesSearchFactory;
import com.westial.alexa.jumpandread.infrastructure.service.GoogleCandidatesSearchFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FreeFirstFailSafeJumpAndReadRouter extends JumpAndReadRouter
{
    @Override
    public final void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        CandidatesSearchFactory googleFactory = new GoogleCandidatesSearchFactory();
        CandidatesSearchFactory duckFactory = new DuckDuckGoCandidatesSearchFactory();
        searchFactory = new FailSafeCandidatesSearchFactory(duckFactory, googleFactory);
        super.jumpAndRead(inputStream, outputStream, context);
    }
}
