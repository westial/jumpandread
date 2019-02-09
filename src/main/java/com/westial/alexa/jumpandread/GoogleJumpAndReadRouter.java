package com.westial.alexa.jumpandread;

import com.amazonaws.services.lambda.runtime.Context;
import com.westial.alexa.jumpandread.infrastructure.service.GoogleCandidatesSearchFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GoogleJumpAndReadRouter extends JumpAndReadRouter
{
    @Override
    public final void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        searchFactory = new GoogleCandidatesSearchFactory();
        super.jumpAndRead(inputStream, outputStream, context);
    }
}
