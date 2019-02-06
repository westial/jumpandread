package junit;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.infrastructure.MockContentAddress;
import com.westial.alexa.jumpandread.infrastructure.service.UnirestContentGetter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnirestContentGetterTest
{
    private UnirestContentGetter getter;
    private ContentAddress utf8Address;
    private MockContentAddress win1252Address;

    @Before
    public void setUp() throws Exception
    {
        utf8Address = new MockContentAddress(
                "https://www.laylita.com/recetas/pastel-de-manzana-receta/"
        );

        win1252Address = new MockContentAddress(
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
    }
}