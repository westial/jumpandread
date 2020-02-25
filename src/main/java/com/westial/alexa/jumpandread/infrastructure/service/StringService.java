package com.westial.alexa.jumpandread.infrastructure.service;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class StringService
{
    public static String encode(String content, String charset) throws IOException
    {
        StringEntity entity = new StringEntity(content);
        return EntityUtils.toString(entity, charset);
    }

    public static String utf8Encode(String content) throws IOException
    {
        return encode(content, "UTF-8");
    }
}
