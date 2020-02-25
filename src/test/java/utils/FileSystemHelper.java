package utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class FileSystemHelper
{
    public static String readResourceFile(String filePath) throws IOException
    {
        Writer writer = new StringWriter();
        IOUtils.copy(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath),
                writer,
                "UTF-8"
        );
        return writer.toString();
    }

    public static String readResourceFile(
            String filePath,
            Map<String, String> regexsReplacement
    ) throws IOException
    {
        String content = FileSystemHelper.readResourceFile(filePath);
        for (Map.Entry<String, String> entry: regexsReplacement.entrySet())
        {
            content = content.replaceAll(
                    entry.getKey(),
                    entry.getValue()
            );
        }
        return content;
    }
}
