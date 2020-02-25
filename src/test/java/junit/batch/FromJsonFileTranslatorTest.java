package junit.batch;

import com.westial.alexa.jumpandread.infrastructure.service.FromJsonFileTranslator;
import com.westial.alexa.jumpandread.domain.Translator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FromJsonFileTranslatorTest
{
    private Translator translator;

    @Before
    public void setUp()
    {
        translator = new FromJsonFileTranslator(
                "es_ES",
                "common.json",
                "test.json"
        );
    }

    @Test
    public void formatFromList()
    {
        String format = "Nom %s quantitat %s";
        List<String> params = new ArrayList<>();
        params.add("Jaume");
        params.add("123455555555");
        String translated = translator.translate(format, params);
        Assert.assertEquals(
                "Nom Jaume quantitat 123455555555",
                translated
        );
        System.out.println(translated);
    }

    @Test
    public void withFormat()
    {
        String format = "Hello %s thanks a lot, how many? ... like that %s";
        String expected = "Hola Jaume muchas gracias, cuántas? ... como unas 123455555555";
        List<String> params = new ArrayList<>();
        params.add("Jaume");
        params.add("123455555555");
        String translated = translator.translate(format, params);
        Assert.assertEquals(expected, translated);
        System.out.println(translated);
    }

    @Test
    public void noFormat()
    {
        String format = "hello";
        String translated = translator.translate(format);
        Assert.assertEquals("hola", translated);
        System.out.println(translated);
    }

}