package com.westial.alexa.jumpandread.domain;

import java.util.Map;

public interface HeadersProvider
{
    Map<String, String> provide(String locale);
}
