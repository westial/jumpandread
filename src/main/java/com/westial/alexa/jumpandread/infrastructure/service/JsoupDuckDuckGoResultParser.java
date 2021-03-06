package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.infrastructure.exception.EngineNoSearchResultException;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import com.westial.alexa.jumpandread.infrastructure.exception.WebClientSearchException;
import com.westial.alexa.jumpandread.infrastructure.structure.DuckDuckGoResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class JsoupDuckDuckGoResultParser implements DuckDuckGoResultParser
{

    private final TextCleaner titleCleaner;

    public JsoupDuckDuckGoResultParser(TextCleaner titleCleaner)
    {
        this.titleCleaner = titleCleaner;
    }

    /**
     * Parse string content and return duckduckgo result structure items or null
     * if no result is found.
     */
    @Override
    public List<DuckDuckGoResult> parse(String content) throws SearchException, NoSearchResultException
    {
        Document document = Jsoup.parse(content);
        List<DuckDuckGoResult> results = new ArrayList<>();
        Elements nodes = document.select("div.result__body");
        if (null == nodes || nodes.isEmpty())
        {
            throw new WebClientSearchException(
                    String.format(
                            "Expecting %s in DuckDuckGo searching results is not found",
                            "div.result__body"
                    )
            );
        }
        for (Element node: nodes)
        {
            try
            {
                DuckDuckGoResult result = createResult(node);
                results.add(result);
            }
            catch (NullPointerException ignored)
            {
            }
        }
        if (results.isEmpty())
        {
            throw new EngineNoSearchResultException("No result in DuckDuckGo searching results page");
        }
        return results;
    }

    private DuckDuckGoResult createResult(Element resultNode) throws NullPointerException
    {
        String description = "";
        Element titleLink = resultNode
                .select("a.result__a")
                .first();
        String title = titleCleaner.extract(titleLink.text());
        String url = titleLink.attr("href");
        Elements descriptionNodes = resultNode.select("a.result__snippet");
        if (!descriptionNodes.isEmpty())
        {
            description = descriptionNodes
                    .first()
                    .text();
        }
        return new DuckDuckGoResult(title, url, description);
    }
}
