package com.westial.alexa.jumpandread.infrastructure.service.wordcheck;

import com.westial.alexa.jumpandread.application.service.wordcheck.MatchingWordsChecker;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexMatchingWordsChecker implements MatchingWordsChecker
{
    private final Pattern pattern;

    public RegexMatchingWordsChecker(List<String> lowerCaseWords)
    {
        pattern = buildSamplesPattern(lowerCaseWords);
    }

    private Pattern buildSamplesPattern(List<String> words)
    {
        String patternContent = "";
        patternContent = String.format(
                "\\b(?:%s)\\b",
                words.stream()
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.joining("|"))
        );
        return Pattern.compile(patternContent);
    }

    private int countWords(String content)
    {
        return content.split("\\s+").length;
    }

    private int countMatches(String content)
    {
        Matcher matcher = pattern.matcher(content.toLowerCase());
        int count = 0;
        while (matcher.find())
        {
            count ++;
        }
        return count;
    }

    @Override
    public double check(String content)
    {
        return (countMatches(content) / (double)countWords(content)) * 100;
    }
}
