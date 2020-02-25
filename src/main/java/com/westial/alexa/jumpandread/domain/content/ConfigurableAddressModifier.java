package com.westial.alexa.jumpandread.domain.content;

public interface ConfigurableAddressModifier
{
    void addModifier(String regex, AddressModifier addressModifier);
}
