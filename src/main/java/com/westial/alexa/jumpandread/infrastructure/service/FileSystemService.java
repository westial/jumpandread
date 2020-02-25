package com.westial.alexa.jumpandread.infrastructure.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class FileSystemService
{
    public static String readFileByUrl(String fileUrl) throws IOException
    {
        URL url;
        InputStream inputStream = null;
        StringWriter writer = new StringWriter();
        url = new URL(fileUrl);
        inputStream = url.openStream();
        try
        {
            IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
        } finally
        {
            inputStream.close();
        }
        return writer.toString();
    }

    public static String readResourceFile(String filePath) throws URISyntaxException, IOException
    {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(filePath);

        assert url != null;
        return FileUtils.readFileToString(
                new File(url.toURI()),
                StandardCharsets.UTF_8
        );
    }

    public static List<String> readListFromFile(String fileName)
    {
        List<String> list;
        try
        {
            list = Arrays.asList(
                    FileSystemService
                            .readResourceFile(fileName)
                            .split("\\n")
            );
        }
        catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException(
                    String.format(
                            "Error while reading list file as %s. %s",
                            fileName,
                            e.getMessage()
                    )
            );
        }
        list.removeAll(Arrays.asList("", null));
        if (list.isEmpty())
        {
            throw new RuntimeException(
                    String.format(
                            "Empty list after filter empty values " +
                                    "for file as %s.",
                            fileName
                    )
            );
        }
        return list;
    }
}
