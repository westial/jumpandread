package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.domain.content.AddressModifier;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.infrastructure.structure.SimpleContentAddress;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public class MediumAddressModifier implements AddressModifier
{
    @Override
    public ContentAddress modify(ContentAddress address) throws UrlFormatAddressException
    {
        URI uri;
        try
        {
            uri = new URI(address.getUrl());
            URIBuilder builder = new URIBuilder(uri);
            uri = builder.addParameter("format", "json").build();
        } catch (URISyntaxException e)
        {
            throw new UrlFormatAddressException(
                    String.format(
                            "Unexpected URL format as %s. %s",
                            address.getUrl(),
                            e.getMessage()
                    )
            );
        }
        return new SimpleContentAddress(uri.toString());
    }
}
