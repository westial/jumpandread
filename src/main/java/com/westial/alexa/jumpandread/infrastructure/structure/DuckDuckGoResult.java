package com.westial.alexa.jumpandread.infrastructure.structure;

public class DuckDuckGoResult
{
    private String title;
    private String url;
    private String description;


    public DuckDuckGoResult(String title, String url, String description)
    {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }

    public String getDescription()
    {
        return description;
    }
}
