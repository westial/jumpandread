package com.westial.alexa.jumpandread.infrastructure.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.westial.alexa.jumpandread.infrastructure.exception.WebClientSearchException;
import com.westial.alexa.jumpandread.infrastructure.structure.HttpMethod;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class UnirestWebClient implements WebClient
{
    @Override
    public String request(
            HttpMethod method,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, String> payload
    ) throws WebClientSearchException
    {
        switch (method)
        {
            case POST:
                return post(url, headers, params, payload);
            case GET:
                return get(url, headers, params);
            default:
                throw new RuntimeException("Still not implemented!!");
        }
    }

    private String post(
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, String> payload
    ) throws WebClientSearchException
    {
        try
        {
            List<NameValuePair> dataFields = new ArrayList<>();
            for (Map.Entry<String, String> entry : payload.entrySet())
            {
                dataFields.add(
                        new BasicNameValuePair(
                                entry.getKey(),
                                entry.getValue()
                        )
                );
            }
            String body = URLEncodedUtils.format(
                    dataFields,
                    StandardCharsets.UTF_8
            );

            HttpResponse<String> response = Unirest.post(url)
                    .headers(headers)
                    .queryString(params)
                    .body(body)
                    .asString();
            return StringService.utf8Encode(response.getBody());
        } catch (UnirestException | IOException e)
        {
            throw new WebClientSearchException(e.getMessage());
        }
    }

    private String get(
            String url,
            Map<String, String> headers,
            Map<String, Object> params
    ) throws WebClientSearchException
    {
        try
        {
            HttpResponse<String> response = Unirest.get(url)
                    .headers(headers)
                    .queryString(params)
                    .asString();
            return StringService.utf8Encode(response.getBody());
        } catch (UnirestException | IOException e)
        {
            throw new WebClientSearchException(e.getMessage());
        }
    }

    // FIXME does not work. Disabled header for gzip compression
    private String uncompressBody(byte[] contentBytes) throws IOException
    {
        GZIPInputStream gzip = new GZIPInputStream (
                new ByteArrayInputStream(contentBytes)
        );
        StringBuilder buffer = new StringBuilder();

        byte[] contentByte = new byte[1024];

        while (true)
        {
            int  iLength = gzip.read (contentByte, 0, 1024);

            if (iLength < 0)
                break;

            buffer.append (new String(contentByte, 0, iLength));
        }
        return buffer.toString();
    }
}
