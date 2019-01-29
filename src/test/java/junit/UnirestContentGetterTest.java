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
    private ContentAddress address;

    @Before
    public void setUp() throws Exception
    {
        address = new MockContentAddress(
                "https://www.laylita.com/recetas/pastel-de-manzana-receta/"
        );

        getter = new UnirestContentGetter(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"
        );
    }

    @Test
    public void getContent()
    {
        String body = getter.getContent(address);
        Assert.assertTrue(body.length() > 0);
    }
}