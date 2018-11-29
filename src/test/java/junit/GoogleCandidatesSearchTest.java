package junit;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.*;
import com.westial.alexa.jumpandread.infrastructure.service.AlexaOutputFormatter;
import com.westial.alexa.jumpandread.infrastructure.service.DynamoDbCandidateFactory;
import com.westial.alexa.jumpandread.infrastructure.service.GoogleCandidatesSearch;
import com.westial.alexa.jumpandread.infrastructure.service.UnirestCandidateGetter;
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
    private CandidateGetter candidateGetter;
    private OutputFormatter outputFormatter;
    private CandidateParser candidateParser;
    private CandidateRepository candidateRepository;

    @Before
    public void setUp() throws Exception
    {
        candidateParser = new MockCandidateParser();
        candidateRepository = new MockCandidateRepository();
        candidateGetter = new UnirestCandidateGetter("fakebrowser");
        outputFormatter = new AlexaOutputFormatter();

        candidateFactory = new DynamoDbCandidateFactory(
                candidateGetter,
                candidateParser,
                candidateRepository,
                outputFormatter
        );

        engine = new GoogleCandidatesSearch(
                STARTING_CANDIDATE_INDEX,
                getConfig("google.key"),
                getConfig("google.cx"),
                "es-ES",
                candidateFactory);
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
                "japan rail pass"
        );
        Assert.assertEquals(10, candidates.size());
    }
}