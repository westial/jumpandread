package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.infrastructure.structure.HttpMethod;

import java.util.Map;

public interface WebClient
{
    String request(
            HttpMethod method,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, String> payload
    );
}
