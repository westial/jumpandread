package com.westial.alexa.jumpandread.infrastructure.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.westial.alexa.jumpandread.application.exception.GettingContentException;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


public class UnirestContentGetter implements ContentGetter
{
    private final String userAgent;

    public UnirestContentGetter(String userAgent)
    {
        this.userAgent = userAgent;
    }

    private String fixUrl(String url)
    {
        if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = "http://" + url;
        }
        return url;
    }

    @Override
    public String getContent(ContentAddress address) throws GettingContentException
    {
        String url = fixUrl(address.getUrl());
        SSLContext sslcontext;

        try
        {
            sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();
            SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionFactory)
                    .build();
            Unirest.setHttpClient(httpclient);
            HttpResponse<String> response = Unirest.get(url)
                    .header("User-Agent", userAgent)
                    .asString();
            return response.getBody();
        } catch (UnirestException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException exception)
        {
            throw new GettingContentException(exception.getMessage());
        }
    }
}
