package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.NoReadingElements;
import com.westial.alexa.jumpandread.domain.CandidateParser;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbParagraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class JsoupCandidateParser implements CandidateParser
{
    public List<Paragraph> parse(String content)
    {
        List<Paragraph> paragraphs = new ArrayList<Paragraph>();
        Document document = Jsoup.parse(content);
        Elements readElements = document.select("h1, h2, h3, h4, h5, h6, p, pre, li");
        if (0 < readElements.size())
        {
            readElements = filter(readElements);
        }
        if (0 == readElements.size())
        {
            throw new NoReadingElements("No reading elements found");
        }
        readElements = sort(readElements);
        for (Element readElement: readElements)
        {
            paragraphs.add(
                    new DynamoDbParagraph(
                            readElement.tagName(), readElement.text()
                    )
            );
        }
        return paragraphs;
    }

    private static Elements filter(Elements elements)
    {
        Elements filtered = elements.clone();

        for (Element element: elements)
        {
            if (0 == element.text().length())
            {
                filtered.remove(element);
            }
        }
        return filtered;
    }

    private static Elements sort(Elements elements)
    {
        Elements firsts = null;
        int firstElementIndex;
        Queue<String> mainTags = new LinkedList<>();
        Elements sortedElements = new Elements();

        mainTags.add("h1");
        mainTags.add("h2");
        mainTags.add("h3");
        mainTags.add("h4");
        mainTags.add("h5");

        while (null == firsts && !mainTags.isEmpty())
        {
            firsts = elements.select(mainTags.poll());
        }

        if (null == firsts || firsts.isEmpty())
        {
            return elements;
        }

        firstElementIndex = elements.indexOf(firsts.first());

        if (0 == firstElementIndex)
        {
            return elements;
        }
        sortedElements.addAll(
                elements.subList(
                        firstElementIndex,
                        elements.size()
                )
        );
        sortedElements.addAll(
                elements.subList(
                        0,
                        firstElementIndex
                )
        );
        return sortedElements;
    }

    private static Boolean isChildOf(Element child, Element parent)
    {
        for (Element parentElement: parent.getAllElements())
        {
            if (parentElement.equals(child))
            {
                return true;
            }
        }
        return false;
    }
}
