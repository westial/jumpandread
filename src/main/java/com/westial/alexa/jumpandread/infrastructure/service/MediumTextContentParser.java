package com.westial.alexa.jumpandread.infrastructure.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.application.exception.FilteringNoParagraphsException;
import com.westial.alexa.jumpandread.application.exception.IteratingNoParagraphsException;
import com.westial.alexa.jumpandread.domain.NoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.infrastructure.structure.HtmlTextContent;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediumTextContentParser extends TextContentParser
{
    private final Pattern filterPattern;
    private final static int CONTENT_DEBUG_CHAR_NUMBER = 100;

    public MediumTextContentParser(String filterRegex)
    {
        filterPattern = Pattern.compile(
                filterRegex,
                Pattern.DOTALL | Pattern.MULTILINE
        );
    }

    private Map<String, Object> toMap(String content) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                content,
                new TypeReference<HashMap<String, Object>>()
                {
                }
        );
    }

    private LinkedList<TextContent> extractTextContents(Map<String, Object> contentMap)
    {
        LinkedList<TextContent> textContents = new LinkedList<>();
        Map payload = (Map) contentMap.get("payload");
        Map value = (Map) payload.get("value");
        Map content = (Map) value.get("content");
        Map bodyModel = (Map) content.get("bodyModel");
        List paragraphs = (List) bodyModel.get("paragraphs");
        for (Object item : paragraphs)
        {
            String label;
            Map paragraph = (Map) item;
            List markups = (List) paragraph.get("markups");
            if (markups.isEmpty())
            {
                label = "p";
            }
            else
            {
                label = "h2";
            }

            textContents.add(
                    new HtmlTextContent(
                            label,
                            (String) paragraph.get("text")
                    )
            );
        }
        return textContents;
    }

    @Override
    public LinkedList<TextContent> parse(String content) throws NoParagraphsException
    {
        Map<String, Object> contentMap;
        Matcher contentMatcher = filterPattern.matcher(content);
        if (!contentMatcher.matches())
        {
            throw new FilteringNoParagraphsException(
                    String.format(
                            "Content does not match the expected filter " +
                                    "regex. Content starts as '%s'",
                            content.substring(0, CONTENT_DEBUG_CHAR_NUMBER)
                    )
            );
        }
        try
        {
            contentMap = toMap(contentMatcher.group(1));
        } catch (IOException e)
        {
            throw new FilteringNoParagraphsException(
                    String.format(
                            "%s. Converting content to map failed. Content " +
                                    "starts as '%s'",
                            e.getMessage(),
                            content.substring(0, CONTENT_DEBUG_CHAR_NUMBER)
                    )
            );
        }

        try
        {
            return extractTextContents(contentMap);
        }
        catch (NullPointerException keyError)
        {
            throw new IteratingNoParagraphsException(
                    String.format(
                            "%s. Deserializing content map found a null item " +
                                    "going deep on key/value chain",
                            keyError.getMessage()
                    )
            );
        }
    }
}
