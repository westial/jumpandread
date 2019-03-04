package com.westial.alexa.jumpandread.infrastructure.service.content.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.application.exception.FilteringNoParagraphsException;
import com.westial.alexa.jumpandread.application.exception.IteratingNoParagraphsException;
import com.westial.alexa.jumpandread.domain.NoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.*;
import com.westial.alexa.jumpandread.infrastructure.service.content.HtmlTag;
import com.westial.alexa.jumpandread.infrastructure.service.content.LinkHtmlTag;
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
    private final String uriRoot;       // I got really good results with "https://medium.com/p".
    private final static int CONTENT_DEBUG_CHAR_NUMBER = 100;

    public MediumTextContentParser(String filterRegex, String uriRoot)
    {
        filterPattern = Pattern.compile(
                filterRegex,
                Pattern.DOTALL | Pattern.MULTILINE
        );
        this.uriRoot = uriRoot;
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

    private LinkedList<TextContent> extractPost(Map payload)
    {
        LinkedList<TextContent> textContents = new LinkedList<>();
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
                            new HtmlTag((String) paragraph.get("text"))
                    )
            );
        }
        return textContents;
    }

    private LinkedList<TextContent> extractMultiplePost(Map payload)
    {
        LinkedList<TextContent> textContents = new LinkedList<>();

        Map references = (Map) payload.get("references");
        Map posts = (Map) references.get("Post");
        for (Object item: posts.values())
        {
            Map post = (Map) item;
            if (!post.get("type").equals("Post"))
            {
                continue;
            }
            String url = String.format(
                    "%s/%s",
                    uriRoot,
                    post.get("uniqueSlug")
            );
            String title = (String) post.get("title");

            LinkHtmlTag tag = new LinkHtmlTag(title);
            tag.setSrc(url);

            textContents.add(
                    new HtmlTextContent(
                            XtraTagType.X_CANDIDATE.name(),
                            tag
                    )
            );
        }
        return textContents;
    }

    private LinkedList<TextContent> extractTextContents(Map contentMap)
    {
        Map payload = (Map) contentMap.get("payload");

        if (payload.containsKey("value"))
        {
            return extractPost(payload);
        }

        return extractMultiplePost(payload);
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
