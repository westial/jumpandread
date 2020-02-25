package com.westial.alexa.jumpandread.domain.content;

import java.util.Map;

interface Tag<K, V> extends Map<K, V>
{
    String getText();
}
