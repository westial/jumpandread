package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.infrastructure.exception.WebClientSearchException;
import com.westial.alexa.jumpandread.infrastructure.service.FileSystemService;
import com.westial.alexa.jumpandread.infrastructure.service.WebClient;
import com.westial.alexa.jumpandread.infrastructure.structure.HttpMethod;

import java.io.IOException;
import java.util.Map;

public class LocalWebClient implements WebClient
{
    private final String filePath;

    public LocalWebClient(String filePath)
    {
        this.filePath = filePath;
    }

    @Override
    public String request(
            HttpMethod method,
            String url,
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, String> payload
    ) throws WebClientSearchException
    {
        try
        {
            return FileSystemService.readFileByUrl(filePath);
        } catch (IOException e)
        {
            throw new RuntimeException(
                    String.format(
                            "Trying to get the local file %s. %s",
                            filePath,
                            e.getMessage()
                    )
            );
        }
    }
}
