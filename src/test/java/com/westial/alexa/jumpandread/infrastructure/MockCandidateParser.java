package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.Paragraph;
import utils.RandomContent;

import java.util.List;

public class MockCandidateParser implements com.westial.alexa.jumpandread.domain.CandidateParser
{
    public List<Paragraph> parse(String content)
    {
        return RandomContent.createParagraphs(3, 6);
    }
}
