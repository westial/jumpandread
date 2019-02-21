package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.infrastructure.structure.HtmlTextContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

public class WebNarrativeTextContentParser extends TextContentParser
{
    @Override
    public LinkedList<TextContent> parse(String content) throws NoParagraphsException
    {
        LinkedList<TextContent> contents = new LinkedList<>();
        Document document = Jsoup.parse(content);
        // TODO check if there is any child else throw an exception
        buildContents(document.body().children(), contents);
        return contents;
    }

    private void buildContents(
            Elements unsortedElements,
            LinkedList<TextContent> contents
    )
    {
        for (Element element : unsortedElements)
        {
            List<Node> nodes = element.childNodes();
            for (Node node : nodes)
            {
                if (node instanceof TextNode && !((TextNode) node).isBlank())
                {
                    TextContent content = new HtmlTextContent(
                            element.tagName(),
                            ((TextNode) node).text()
                    );
                    contents.add(content);
                }
            }
            buildContents(element.children(), contents);
        }
    }
}
