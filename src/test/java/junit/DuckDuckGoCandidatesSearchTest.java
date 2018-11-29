package junit;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.MockCandidateParser;
import com.westial.alexa.jumpandread.infrastructure.MockCandidateRepository;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.JvmEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuckDuckGoCandidatesSearchTest
{
    private CandidatesSearch engine;
    private CandidateFactory candidateFactory;
    private CandidateGetter candidateGetter;
    private OutputFormatter outputFormatter;
    private CandidateParser candidateParser;
    private CandidateRepository candidateRepository;

    private static void configure() throws Exception
    {
        Map<String, String> testEnv = new HashMap<>();
        testEnv.put("STARTING_CANDIDATE_INDEX", "1");
        testEnv.put("DUCK_URL", "https://duckduckgo.com/html/");
        testEnv.put("AGENTS_RESOURCE_PATH", "useragent.pc.list");
        testEnv.put("LANGUAGES_RESOURCE_PATH", "languages.list");
        testEnv.put("REFERRERS_RESOURCE_PATH", "referrers.list");
        testEnv.put("DUCK_LOCALE_RESOURCE_PATH", "duckduckgo.kl.lang.es.list");
        testEnv.put("ISO4_LANGUAGE", "es-ES");
        JvmEnvironment.setEnv(testEnv);
    }

    @Before
    public void setUp() throws Exception
    {
        configure();
        Configuration config = new EnvironmentConfiguration();

        candidateParser = new MockCandidateParser();
        candidateRepository = new MockCandidateRepository();
        candidateGetter = new UnirestCandidateGetter("fakebrowser");
        outputFormatter = new AlexaOutputFormatter();

        candidateFactory = new DynamoDbCandidateFactory(
                candidateGetter,
                candidateParser,
                candidateRepository
        );

        CandidatesSearchFactory factory = new DuckDuckGoCandidatesSearchFactory();
        engine = factory.create(config, candidateFactory);
    }

    @Test
    public void find()
    {
        List<Candidate> candidates = engine.find(
                new User("user id", "session id"),
                "search id",
                "japan rail pass"
        );
        Assert.assertNotNull(candidates);
    }
}