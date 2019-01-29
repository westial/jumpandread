package junit;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.MockCandidateRepository;
import com.westial.alexa.jumpandread.infrastructure.MockContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GoogleCandidatesSearchTest
{
    private static int STARTING_CANDIDATE_INDEX = 1;
    private CandidatesSearch engine;
    private CandidateFactory candidateFactory;
    private ContentGetter contentGetter;
    private Presenter presenter;
    private TextContentParser contentParser;
    private CandidateRepository candidateRepository;
    private TextContentProvider contentProvider;

    @Before
    public void setUp() throws Exception
    {
        contentParser = new MockContentParser();
        candidateRepository = new MockCandidateRepository();
        contentGetter = new UnirestContentGetter("fakebrowser");
        presenter = new AlexaPresenter(new MockTranslator());
        contentProvider = new RemoteTextContentProvider(contentGetter, contentParser);

        candidateFactory = new DynamoDbCandidateFactory(
                contentProvider,
                candidateRepository,
                100
        );

        engine = new GoogleCandidatesSearch(
                STARTING_CANDIDATE_INDEX,
                getConfig("google.key"),
                getConfig("google.cx"),
                "Jump and Read Junit Test",
                "es-ES",
                candidateFactory,
                10,
                "filetype:html OR filetype:htm"
        );
    }

    private String getConfig(String filePath) throws URISyntaxException, IOException
    {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(filePath);

        return FileUtils.readFileToString(
                new File(url.toURI()),
                StandardCharsets.UTF_8
        );
    }

    @Test
    public void findTest()
    {
        List<Candidate> candidates = engine.find(
                new User("user id", "session id"),
                "search id",
                "hola"
        );
        Assert.assertEquals(10, candidates.size());
    }
}