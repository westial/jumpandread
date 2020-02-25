package com.westial.alexa.jumpandread.application.service.wordcheck;

import java.io.IOException;
import java.util.List;

public interface FileToWordList
{
    List<String> convert() throws IOException;
}
