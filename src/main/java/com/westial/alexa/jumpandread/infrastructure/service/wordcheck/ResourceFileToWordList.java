package com.westial.alexa.jumpandread.infrastructure.service.wordcheck;

import com.westial.alexa.jumpandread.application.service.wordcheck.FileToWordList;
import com.westial.alexa.jumpandread.infrastructure.service.FileSystemService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResourceFileToWordList implements FileToWordList
{
    private final List<String> resourceFiles;
    private final Pattern validatorPattern;

    public ResourceFileToWordList(String... filePaths)
    {
        resourceFiles = Arrays.asList(filePaths);
        String regex = "(.)*(\\d)(.)*";
        validatorPattern = Pattern.compile(regex);
    }

    private boolean validateWord(String word)
    {
        return !word.isEmpty() && !validatorPattern.matcher(word).matches();
    }

    @Override
    public List<String> convert() throws IOException
    {
        Set<String> wordList = new HashSet<>();
        for (String resourceFile : resourceFiles)
        {
            List<String> reads;
            try
            {
                reads = Arrays.asList(
                        FileSystemService.readResourceFile(resourceFile).split("\\n")
                );
            } catch (URISyntaxException e)
            {
                throw new IOException(e.getMessage());
            }
            wordList.addAll(reads);
        }
        return new ArrayList<>(wordList).stream()
                .filter(this::validateWord)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

    }
}
