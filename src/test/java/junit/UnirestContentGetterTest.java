package junit;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.infrastructure.structure.SimpleContentAddress;
import com.westial.alexa.jumpandread.infrastructure.service.UnirestContentGetter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnirestContentGetterTest
{
    private UnirestContentGetter getter;
    private ContentAddress utf8Address;
    private ContentAddress win1252Address;
    private ContentAddress redirect301Address;

    @Before
    public void setUp() throws Exception
    {
        utf8Address = new SimpleContentAddress(
                "https://www.laylita.com/recetas/pastel-de-manzana-receta/"
        );

        redirect301Address = new SimpleContentAddress(
                "http://www.medium.com/p/are-you-looking-to-buy-outer-lid-pressure-cooker-online-fd107be8a9b0"
        );

        win1252Address = new SimpleContentAddress(
                "https://gutenberg.jumpandread.com/etext96/50bab10h.htm"
        );

        getter = new UnirestContentGetter(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"
        );
    }

    @Test
    public void getContent()
    {
        String body = getter.getContent(utf8Address);
        Assert.assertTrue(body.length() > 0);

        body = getter.getContent(win1252Address);
        Assert.assertTrue(body.length() > 0);

        body = getter.getContent(redirect301Address);
        Pattern checkPattern = Pattern.compile(
                ".+It saves your important time and simplifies the " +
                        "entire process of cooking.+",
                Pattern.DOTALL | Pattern.MULTILINE
        );
        Matcher matcher = checkPattern.matcher(body);
        Assert.assertTrue(matcher.matches());
    }
}