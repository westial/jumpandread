package junit;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.Translator;
import com.westial.alexa.jumpandread.infrastructure.service.AlexaPresenter;
import com.westial.alexa.jumpandread.infrastructure.service.FromJsonFileTranslator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PresenterTest
{
    private Translator translator;
    private Presenter presenter;

    @Before
    public void setUp()
    {
        translator = new FromJsonFileTranslator(
                "es-ES",
                "test"
        );

        presenter = new AlexaPresenter(translator);
    }

    @Test
    public void testAlexaFullPresenter()
    {
        String format1 = "hello";
        String format2 = "{{ . }}";
        String format3 = "This is a proof only,{{ , }} my name is %s and I am %d years old.{{ . }} And you?";
        String expected = "<speak>hola<break time=\"1500ms\"/>Esto es una prueba,<break strength=\"x-strong\"/> mi nombre es Jaume y tengo 43 años.<break time=\"1500ms\"/> Y tú?</speak>";

        presenter.addText(format1);
        presenter.addText(format2);
        presenter.addText(format3, "Jaume", 43);

        Assert.assertEquals(expected, presenter.output());
        // ensure you output() is not idempotent
        Assert.assertEquals(expected, presenter.output());
    }
}