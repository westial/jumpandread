package utils;

import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.domain.content.HtmlTag;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbParagraph;
import com.westial.alexa.jumpandread.infrastructure.structure.HtmlTextContent;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomContent
{
    public static String randomWord()
    {
        Random random = new Random();
        int min = 1;
        int med = 7;
        int max = 14;
        return createWords(
                1,
                random.nextInt(med + 1 - min) + min,
                random.nextInt(max + 1 - med) + med,
                true,
                false
                );
    }

    public static String createWords(
            int maxWords,
            int minWordLength,
            int maxWordLength,
            boolean useLetters,
            boolean useNumbers
    )
    {
        int length;
        Random random = new Random();
        String word;
        String text = "";

        for (int i = 0; i < maxWords; i++)
        {

            length = random.nextInt(maxWordLength + 1 - minWordLength) + minWordLength;
            word = RandomStringUtils.random(
                    length,
                    useLetters,
                    useNumbers
            );
            text = word + " ";
        }

        return text.trim();
    }

    public static String createPhrase(int minWords, int maxWords)
    {
        Random random = new Random();
        int wordsCount = random.nextInt(maxWords + 1 - minWords) + minWords;
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < maxWords; i++)
        {
            text.append(
                    createWords(
                            wordsCount,
                            2,
                            12,
                            true,
                            false
                    ).concat(" ")
            );
        }
        return text.toString().trim();
    }

    public static DynamoDbParagraph createParagraph(int minPhrases, int maxPhrases)
    {
        String label = "p";
        StringBuilder text = new StringBuilder();
        Random random = new Random();
        int phrasesCount = random.nextInt(maxPhrases + 1 - minPhrases) + minPhrases;

        for (int i = 0; i < phrasesCount; i++)
        {
            text.append(createPhrase(4, 9) + ". ");
        }

        return new DynamoDbParagraph(label, new HtmlTag(text.toString().trim()));
    }

    public static TextContent createTextContent(int minPhrases, int maxPhrases)
    {
        String label = "p";
        StringBuilder text = new StringBuilder();
        Random random = new Random();
        int phrasesCount = random.nextInt(maxPhrases + 1 - minPhrases) + minPhrases;

        for (int i = 0; i < phrasesCount; i++)
        {
            text.append(createPhrase(4, 9) + ". ");
        }

        return new HtmlTextContent(label, new HtmlTag(text.toString().trim()));
    }

    public static List<Paragraph> createParagraphs(int minParagr, int maxParagr)
    {
        List<Paragraph> paragraphs = new ArrayList<>();
        Random random = new Random();
        int phrasesCount = random.nextInt(maxParagr + 1 - minParagr) + minParagr;

        for (int i = 0; i < phrasesCount; i++)
        {
            paragraphs.add(createParagraph(5, 9));
        }

        return paragraphs;
    }

    public static List<TextContent> createContents(int minParagr, int maxParagr)
    {
        List<TextContent> contents = new ArrayList<>();
        Random random = new Random();
        int phrasesCount = random.nextInt(maxParagr + 1 - minParagr) + minParagr;

        for (int i = 0; i < phrasesCount; i++)
        {
            contents.add(createTextContent(5, 9));
        }

        return contents;
    }
}
