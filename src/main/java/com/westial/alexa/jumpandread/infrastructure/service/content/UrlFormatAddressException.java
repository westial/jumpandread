package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.domain.content.AddressException;

public class UrlFormatAddressException extends AddressException
{
    public UrlFormatAddressException()
    {
    }

    public UrlFormatAddressException(String message)
    {
        super(message);
    }
}
