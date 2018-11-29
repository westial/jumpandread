package junit;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.service.UnirestCandidateGetter;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnirestCandidateGetterTest
{
    private Candidate candidate;
    private UnirestCandidateGetter getter;

    @Before
    public void setUp() throws Exception
    {
        candidate = new DynamoDbCandidate(
                "id",
                0,
                "userId",
                "sessionId",
                "searchId",
                "title",
                "https://www.laylita.com/recetas/pastel-de-manzana-receta/",
                "description",
                null,
                null,
                null,
                null,
                null
        );

        getter = new UnirestCandidateGetter(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"
        );
    }

    @Test
    public void getContent()
    {
        String body = getter.getContent(candidate);
        Assert.assertTrue(body.length() > 0);
    }
}