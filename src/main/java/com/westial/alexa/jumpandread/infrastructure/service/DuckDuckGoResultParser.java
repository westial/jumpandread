package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.infrastructure.structure.DuckDuckGoResult;

import java.util.List;

public interface DuckDuckGoResultParser
{
    List<DuckDuckGoResult> parse(String content);
}
