package com.westial.alexa.jumpandread.domain.content;

public interface AddressModifier
{
    ContentAddress modify(ContentAddress address) throws AddressException;
}
