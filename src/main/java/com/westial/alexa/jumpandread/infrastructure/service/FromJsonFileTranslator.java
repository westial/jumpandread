package com.westial.alexa.jumpandread.infrastructure.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.domain.Translator;
import com.westial.alexa.jumpandread.infrastructure.exception.InitializationError;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class FromJsonFileTranslator implements Translator
{
    private final Map<String, String> locales;
    private final String currentLocale;

    private static Map<String, String> loadFile(String filePath) throws IOException, URISyntaxException
    {
        String i18nContent = FileSystemService.readResourceFile(filePath);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                i18nContent,
                new TypeReference<Map<String, String>>()
                {
                }
        );
    }

    public FromJsonFileTranslator(String iso4Locale, String... resourceFilenames)
    {
        locales = new HashMap<>();
        iso4Locale = iso4Locale.toLowerCase();
        iso4Locale = iso4Locale.replace("_", "-");
        currentLocale = iso4Locale;
        String filename = null;
        LinkedList<String> filenamesList = new LinkedList<>(
                Arrays.asList(resourceFilenames)
        );

        try
        {
            filename = filenamesList.poll();

            while (null != filename)
            {
                String resourceFilePath = String.format(
                        "i18n/%s/%s",
                        iso4Locale,
                        filename
                );
                locales.putAll(
                        loadFile(resourceFilePath)
                );
                
                filename = filenamesList.poll();
            }

        } catch (URISyntaxException | IOException exc)
        {
            throw new InitializationError(
                    String.format(
                            "Error trying to read and format locales from file as %s. %s",
                            filename,
                            exc.getMessage()
                    )
            );
        }
    }

    @Override
    public String translate(String localeKey, List<String> params)
    {
        String translated = translate(localeKey);
        if (null == params)
        {
            return translated;
        }
        String[] formatArgs = new String[params.size()];
        formatArgs = params.toArray(formatArgs);
        return String.format(translated, (Object[]) formatArgs);
    }

    @Override
    public String translate(String localeKey)
    {
        String translated = locales.get(localeKey);
        if (null == translated)
        {
            translated = localeKey;
            System.out.printf(
                    "WARNING: Formatting text for translation, " +
                            "no locale for key %s in language as %s. " +
                            "Returned default language instead of.\n",
                    localeKey,
                    currentLocale
            );
        }
        return translated;
    }
}
