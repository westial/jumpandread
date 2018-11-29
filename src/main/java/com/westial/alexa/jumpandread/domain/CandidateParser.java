package com.westial.alexa.jumpandread.domain;

import java.util.List;

public interface CandidateParser
{
    List<Paragraph> parse(String content);
}
