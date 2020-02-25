package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.HeadersProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomDuckDuckGoHeadersProvider implements HeadersProvider
{
    private final List<String> agents, languages, referrers;


    public RandomDuckDuckGoHeadersProvider(
            String agentsFileName,
            String languagesFileName,
            String referrersFileName
            )
    {
        agents = FileSystemService.readListFromFile(agentsFileName);
        languages = FileSystemService.readListFromFile(languagesFileName);
        referrers = FileSystemService.readListFromFile(referrersFileName);
    }

    @Override
    public Map<String, String> provide(String iso4Locale)
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authority", "duckduckgo.com");
        headers.put("Cache-Control", "max-age=0");
        headers.put("Origin", "https://duckduckgo.com");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("User-Agent", RandomService.randomChoice(agents));
        headers.put("Referer", RandomService.randomChoice(referrers));
        headers.put("Accept-Language", RandomService.randomChoice(languages));
//        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");

        return headers;
    }
}
