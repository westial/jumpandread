package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.application.exception.GettingContentException;
import com.westial.alexa.jumpandread.domain.content.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByPatternContentGetterDecorator implements ContentGetter, ConfigurableAddressModifier
{
    private final ContentGetter getter;
    private Map<Pattern, AddressModifier> modifiers = new HashMap<>();
    private AddressModifier modifier;

    public ByPatternContentGetterDecorator(
            ContentGetter getter,
            AddressModifier defaultModifier
    )
    {
        this.getter = getter;
        this.modifier = defaultModifier;
    }

    @Override
    public String getContent(ContentAddress address) throws GettingContentException
    {
        setModifier(address.getUrl());
        try
        {
            address = modifier.modify(address);
        } catch (AddressException e)
        {
            throw new GettingContentException(e.getMessage());
        }
        return getter.getContent(address);
    }

    @Override
    public void addModifier(String regex, AddressModifier addressModifier)
    {
        Pattern pattern = Pattern.compile(regex);
        modifiers.put(pattern, addressModifier);
    }

    private void setModifier(String target)
    {
        for (Map.Entry<Pattern, AddressModifier> entry: modifiers.entrySet())
        {
            Matcher matcher = entry.getKey().matcher(target);
            if (matcher.matches())
            {
                modifier = entry.getValue();
                break;
            }
        }
    }
}
