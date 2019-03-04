package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.domain.content.AddressModifier;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;

public class DefaultAddressModifier implements AddressModifier
{
    @Override
    public ContentAddress modify(ContentAddress address)
    {
        return address;
    }
}
