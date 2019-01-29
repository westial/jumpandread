package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.exception.GettingContentException;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;

import java.io.IOException;

@SuppressWarnings("ALL")
public class LocalContentGetter implements ContentGetter
{
    @Override
    public String getContent(ContentAddress address) throws GettingContentException
    {
        try {
            return FileSystemService.readFileByUrl(address.getUrl());
        } catch (IOException exception) {
            throw new GettingContentException(exception.getMessage());
        }
    }
}
