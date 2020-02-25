package com.westial.alexa.jumpandread.domain.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class TextTag implements Tag<String, String>
{
    private final String text;
    private final Map<String, String> attributes;

    public TextTag(String text)
    {
        this.text = text;
        attributes = new HashMap<>();
    }

    @Override
    public String getText()
    {
        return text;
    }

    // -------------------------------------------------------------------------

    @Override
    public int size()
    {
        return attributes.size();
    }

    @Override
    public boolean isEmpty()
    {
        return attributes.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return attributes.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return attributes.containsValue(value);
    }

    @Override
    public String get(Object key)
    {
        return attributes.get(key);
    }

    @Override
    public String put(String key, String value)
    {
        return attributes.put(key, value);
    }

    @Override
    public String remove(Object key)
    {
        return attributes.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map)
    {
        attributes.putAll(map);
    }

    @Override
    public void clear()
    {
        attributes.clear();
    }

    @Override
    public Set<String> keySet()
    {
        return attributes.keySet();
    }

    @Override
    public Collection<String> values()
    {
        return attributes.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet()
    {
        return attributes.entrySet();
    }
}
