package junit;

import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.MockOneCandidateSearch;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.JvmEnvironment;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FullHandlersTest
{
    private static int STARTING_CANDIDATE_INDEX = 1;
    private static final int ONLY_ONE_CANDIDATE_INDEX = 3;
    private static final int PARAGRAPHS_GROUP_MEMBERS_COUNT_CONFIG = 10;
    private CandidatesSearch candidatesSearch;
    private StateRepository stateRepository;
    private String candidatesList;
    private User user;
    private CandidateFactory candidateFactory;
    private CandidateParser candidateParser;
    private CandidateRepository candidateRepository;
    private CandidateGetter candidateGetter;
    private OutputFormatter outputFormatter;
    private StateFactory stateFactory;

    @Before
    public void setUp() throws Exception
    {
        configure();
    }

    private void resetDependencies()
    {
        candidateParser = new JsoupCandidateParser();
        outputFormatter = new AlexaOutputFormatter();

        user = new User("user234", "session455");
        candidateRepository = new DynamoDbCandidateRepository(
                "jnr_candidate"
        );
        stateRepository = new DynamoDbStateRepository("jnr_state");
        stateFactory = new DynamoDbStateFactory(stateRepository);
        candidateFactory = new DynamoDbCandidateFactory(
                candidateGetter,
                candidateParser,
                candidateRepository
        );
    }

    @Test
    public void withFakeSearchEngineTest() throws Exception
    {
        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_7_paragraphs.html"),
                candidateFactory
        );

        search("no matters");


        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_7_paragraphs.html"),
                candidateFactory
        );

        read(
                "<speak>Resultado de búsqueda número 3<break strength=\"x-strong\"/>titulado<break strength=\"x-strong\"/>Candidate title<break time=\"1500ms\"/>Main title<break time=\"1500ms\"/>Hey how are you?<break time=\"1500ms\"/>Lo 1 rewfksdfj dkgkdjnjfgk jhdfkjghfk<break time=\"1500ms\"/>jaume djghkfjhgjh jhgjhgjhg jdjhgk djfhgk.<break time=\"1500ms\"/>Lo 2 rewfksdfj dkgkdjnjfgk jhdfkjghfk<break time=\"1500ms\"/>djghkfjhgjh jhgjhgjhg jdjhgk djfhgk.<break time=\"1500ms\"/>Whats up bro!<break time=\"1500ms\"/>Lo 3 rewfksdfj dkgkdjnjfgk jhdfkjghfk<break time=\"1500ms\"/>djghkfjhgjh jhgjhgjhg jdjhgk djfhgk.<break time=\"1500ms\"/>Lo 4 rewfksdfj dkgkdjnjfgk jhdfkjghfk<break time=\"1500ms\"/>djghkfjhgjh jhgjhgjhg jdjhgk djfhgk.<break time=\"1500ms\"/></speak>",
                PARAGRAPHS_GROUP_MEMBERS_COUNT_CONFIG
        );
    }

    @Test
    public void withFakeSearchEngineDoEverythingTest() throws Exception
    {
        final int paragraphsCount = 2;
        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        search("no matters");


        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        read(
                "<speak>Resultado de búsqueda número 3<break strength=\"x-strong\"/>titulado<break strength=\"x-strong\"/>Candidate title<break time=\"1500ms\"/>1<break time=\"1500ms\"/>2<break time=\"1500ms\"/></speak>",
                paragraphsCount
        );

        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        doNext(
                "<speak>3<break time=\"1500ms\"/>4<break time=\"1500ms\"/></speak>",
                paragraphsCount
        );

        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        doRepeat(
                "<speak>3<break time=\"1500ms\"/>4<break time=\"1500ms\"/></speak>",
                paragraphsCount
        );

        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        jump(
                "<speak>7<break time=\"1500ms\"/>8<break time=\"1500ms\"/></speak>",
                paragraphsCount
        );

        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        doBackward(
                "<speak>5<break time=\"1500ms\"/>6<break time=\"1500ms\"/></speak>",
                paragraphsCount
        );

        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        doNext(
                "<speak>7<break time=\"1500ms\"/>8<break time=\"1500ms\"/></speak>",
                paragraphsCount
        );

        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        jump(
                "<speak>11<break time=\"1500ms\"/></speak>",
                paragraphsCount
        );

        resetDependencies();

        candidateGetter = new LocalCandidateGetter();

        candidatesSearch = new MockOneCandidateSearch(
                htmlFileCandidate("sample_11_jump_only.html"),
                candidateFactory
        );

        try
        {
            read(
                    null,
                    paragraphsCount
            );
        }
        catch (NoReadingElements exc)
        {
            assert true;
            System.out.println(exc.getMessage());
        }

        try
        {
            jump(
                    null,
                    paragraphsCount
            );
        }
        catch (NoCandidateMandatorySearchException exc)
        {
            assert true;
            System.out.println(exc.getMessage());
        }
    }

    @Test
    public void withGoogleSearchEngineTest() throws Exception
    {
        resetDependencies();

        candidateGetter = new UnirestCandidateGetter(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"
        );

        candidatesSearch = new GoogleCandidatesSearch(
                STARTING_CANDIDATE_INDEX,
                getConfig("google.key"),
                getConfig("google.cx"),
                "es-ES",
                candidateFactory);

        search("receta de peras");


        resetDependencies();

        candidateGetter = new UnirestCandidateGetter(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"
        );

        candidatesSearch = new GoogleCandidatesSearch(
                STARTING_CANDIDATE_INDEX,
                getConfig("google.key"),
                getConfig("google.cx"),
                "es-ES",
                candidateFactory);

        read(null, PARAGRAPHS_GROUP_MEMBERS_COUNT_CONFIG);
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

    private void search(String terms) throws Exception
    {
        State state = stateFactory.create(user.getId(), user.getSessionId());

        SearchCandidatesCommand search = new SearchCandidatesCommand(
                candidatesSearch
        );


        candidatesList = search.execute(state, terms);

        System.out.println(candidatesList);
        System.out.println(StringEscapeUtils.unescapeJava(candidatesList));
    }

    private void read(String expectedResult, int paragraphsCount) throws Exception
    {
        State state = stateFactory.create(user.getId(), user.getSessionId());

        RetrieveParagraphsCommand retrieve = new RetrieveParagraphsCommand(
                candidateFactory,
                paragraphsCount
        );

        String retrievedContent = retrieve.execute(
                state,
                ONLY_ONE_CANDIDATE_INDEX
        );

        if (null == expectedResult)
        {
            assert true;
        }
        else
        {
            Assert.assertEquals(expectedResult, retrievedContent);
        }

        System.out.println(retrievedContent);
    }

    private void jump(String expectedResult, int paragraphsCount) throws Exception
    {
        State state = stateFactory.create(user.getId(), user.getSessionId());

        JumpCommand jump = new JumpCommand(
                candidateFactory,
                paragraphsCount
        );

        String retrievedContent = jump.execute(state);

        if (null == expectedResult)
        {
            assert true;
        }
        else
        {
            Assert.assertEquals(expectedResult, retrievedContent);
        }

        System.out.println(retrievedContent);
    }

    private void doNext(String expectedResult, int paragraphsCount) throws Exception
    {
        State state = stateFactory.create(user.getId(), user.getSessionId());

        NextCommand goCommand = new NextCommand(
                candidateFactory,
                paragraphsCount
        );

        String retrievedContent = goCommand.execute(state);

        if (null == expectedResult)
        {
            assert true;
        }
        else
        {
            Assert.assertEquals(expectedResult, retrievedContent);
        }

        System.out.println(retrievedContent);
    }

    private void doRepeat(String expectedResult, int paragraphsCount) throws Exception
    {
        State state = stateFactory.create(user.getId(), user.getSessionId());

        RewindCommand goCommand = new RewindCommand(
                candidateFactory,
                paragraphsCount,
                1
        );

        String retrievedContent = goCommand.execute(state);

        if (null == expectedResult)
        {
            assert true;
        }
        else
        {
            Assert.assertEquals(expectedResult, retrievedContent);
        }

        System.out.println(retrievedContent);
    }

    private void doBackward(String expectedResult, int paragraphsCount) throws Exception
    {
        State state = stateFactory.create(user.getId(), user.getSessionId());

        RewindCommand goCommand = new RewindCommand(
                candidateFactory,
                paragraphsCount,
                2
        );

        String retrievedContent = goCommand.execute(state);

        if (null == expectedResult)
        {
            assert true;
        }
        else
        {
            Assert.assertEquals(expectedResult, retrievedContent);
        }

        System.out.println(retrievedContent);
    }

    private static void configure() throws Exception
    {
        Map<String, String> testEnv = new HashMap<>();
        testEnv.put("AWS_PROFILE", "westial");
        JvmEnvironment.setEnv(testEnv);
    }

    private Candidate htmlFileCandidate(String fileName) throws Exception
    {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(fileName);

        String filePath = url.getPath();

        String fileContent = FileUtils.readFileToString(
                new File(url.toURI()),
                StandardCharsets.UTF_8
        );

        return candidateFactory.create(
                ONLY_ONE_CANDIDATE_INDEX,
                user,
                "search135",
                "Candidate title",
                "file://" + filePath,
                "description"
        );
    }
}