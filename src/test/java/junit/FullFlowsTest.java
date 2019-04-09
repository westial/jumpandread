package junit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.westial.alexa.jumpandread.DuckDuckGoJumpAndReadRouter;
import com.westial.alexa.jumpandread.FreeFirstFailSafeJumpAndReadRouter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import utils.FileSystemHelper;
import utils.JsonService;
import utils.JvmEnvironment;
import utils.SampleAwsContext;

import java.io.*;
import java.util.*;

/**
 * There are two ways to execute this test class.
 * <p>
 * 1. As default you can play one or multiple annotated test functions and
 * userId and sessionId will be generated new every time.
 * <p>
 * 2. Pass userSession system property to java command if you want to recycle
 * a userId and sessionId and do not overload the search engine service.
 * User and session values are separated by a colon character ":"
 * Command option example: `-DuserSession=myuserid:mysessionId`
 */
public class FullFlowsTest
{
    private static final String USER_SESSION_PARAM_SEPARATOR = ":";

    private Context context;

    private final static String AWS_PROFILE = "westial";

    private final static String LAZY_EXPECTED_PATTERN = "^<speak>.+</speak>$";
    private final static String LAZY_FULL_LIST_EXPECTED_PATTERN = "^<speak>1.+</speak>$";

    private final static String SAMPLE_INTENTS_PATH = "intents/websearch";
    private final static String SAMPLE_INTENTS_EXPECTED_FILENAME = "ssml_expected.json";
    private final static String SAMPLE_INTENTS_EXTENSION = ".json";
    private final static String SAMPLE_ENVIRONMENT_VARS = "environment_bypattern_es.json";
    private OutputStream outputStreamResult;
    private static String userId;
    private static String sessionId;
    private RequestStreamHandler handler;

    enum INTENT
    {
        backward,
        forward,
        launch,
        next,
        previous,
        pause,
        readthis,
        repeat,
        searchmath,
        searchmediumnarrative,
        searchcustommedium,
        stop,
        list
    }

    @Before
    public void setUp() throws Exception
    {
        context = new SampleAwsContext();
    }

    private void setEnvironment(String rawConfigFile) throws Exception
    {
        String rawConfig = FileSystemHelper.readResourceFile(rawConfigFile);

        Map<String, String> configuration = (HashMap<String, String>) JsonService.loads(rawConfig).get("Variables");
        configuration.put("AWS_PROFILE", AWS_PROFILE);
        JvmEnvironment.setEnv(configuration);

        String token = UUID.randomUUID().toString();

        if (null != System.getProperty("userSession"))
        {
            List<String> userSessionItems = Arrays.asList(
                    System.getProperty("userSession").split(USER_SESSION_PARAM_SEPARATOR)
            );
            userId = userSessionItems.get(0);
            sessionId = userSessionItems.get(1);
        }
        else
        {
            userId = String.format(
                    "amzn1.ask.account.%s.%s", "test",
                    token
            );
            sessionId = String.format(
                    "amzn1.echo-api.session.%s.%s",
                    "test",
                    token
            );
        }
    }

    private static String buildInputEvent(INTENT intent) throws IOException
    {

        Map<String, String> replacements = new HashMap<>();
        replacements.put("\\{\\{ userId \\}\\}", userId);
        replacements.put("\\{\\{ sessionId \\}\\}", sessionId);

        return FileSystemHelper.readResourceFile(
                String.format(
                        "%s/%s%s",
                        SAMPLE_INTENTS_PATH,
                        intent.name(),
                        SAMPLE_INTENTS_EXTENSION
                ),
                replacements
        );
    }

    /**
     * Both with and without user and session. You, please, use without to get
     * credentials and refresh session from time to time.
     * <p>
     * Fast check with a given session and user with testing commandline
     * parameters as:
     * `-ea -DuserSession=amzn1.ask.account.test.cfd24248-c697-431f-8bbd-3ce3ff30a256:amzn1.echo-api.session.test.cfd24248-c697-431f-8bbd-3ce3ff30a256`
     *
     * @throws Throwable
     */
    @Test
    public void fastCheckIntentsFlowCustomMedium() throws Throwable
    {
        setEnvironment("environment_bypattern_force_custommedium_authorlist_en.json");
        handler = new DuckDuckGoJumpAndReadRouter();
        if (null == System.getProperty("userSession"))
        {
            runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN, INTENT.searchcustommedium);
        }
        String witness;
        runAndCheckIntentRead("^<speak>.+(?=The Tech - Medium).{100,}</speak>$");
        runAndCheckIntentRepeat("^<speak>.+(?=The Tech - Medium).+</speak>$");
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        witness = outputStreamResult.toString();
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPrevious(LAZY_EXPECTED_PATTERN);
        Assert.assertNotNull(witness);
        Assert.assertEquals("Next/Previous result is not as expected", witness, outputStreamResult.toString());
    }

    @Test
    public void fastCheckIntentsFlowWebNarrative() throws Throwable
    {
        setEnvironment("environment_bypattern_force_mediumwebnarrative_es.json");
        handler = new DuckDuckGoJumpAndReadRouter();
        String witness;
        runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN, INTENT.searchmediumnarrative);
        runAndCheckIntentRead(LAZY_EXPECTED_PATTERN);
        witness = outputStreamResult.toString();
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPrevious(LAZY_EXPECTED_PATTERN);
        Assert.assertNotNull(witness);
        Assert.assertEquals(witness, outputStreamResult.toString());
    }

    @Test
    public void basicIntentsFlowWebSearch() throws Throwable
    {
        setEnvironment(SAMPLE_ENVIRONMENT_VARS);
        handler = new DuckDuckGoJumpAndReadRouter();
        if (null != System.getProperty("userSession"))
        {
            // It has to be lazy checked and avoid first intents when recycling session.
            recycledFlow();
        }
        else
        {
            runAndCheckIntentLaunch("^<speak>.+(?=Voy a explicarte el funcionamiento básico brevemente).+(?=Puedes pedirme que te repita los últimos párrafos del contenido).{100,}</speak>$");
            runAndCheckIntentSearchThat("^<speak>1<break[^/]+/>(?=SkyMath and the NCTM Standards).{200,}</speak>$", INTENT.searchmath);
            runAndCheckIntentRead("^<speak>.+(?=Project SkyMath: Making Mathematical Connections).{200,}</speak>$");
            runAndCheckIntentList("^<speak>1.+(?=SkyMath and the NCTM Standards.).{200,}</speak>$");
            runAndCheckIntentNext("^<speak>The University Corporation for Atmospheric Research \\(UCAR\\) received funding from the National Science Foundation.{200,}</speak>$");
            runAndCheckIntentNext("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentRepeat("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentForward("^<speak>.+Those standards which are given explicit conceptual development are shown in the chart below.{200,}</speak>$");
            runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentPause("^<speak>.+Alexa.+</speak>$");
            runAndCheckIntentNext("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentNext("^<speak>Brainstorming: Temperature and Temperature Changes.{200,}</speak>$");
            runAndCheckIntentPrevious("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentPrevious("^<speak>.+(?=received funding from the National Science Foundation to prepare a middle school mathematics module incorporating real-time weather data).{200,}</speak>$");
        }
    }

    @Test
    public void checkFreeFirstFailSafeSearch() throws Throwable
    {
        setEnvironment(SAMPLE_ENVIRONMENT_VARS);
        handler = new FreeFirstFailSafeJumpAndReadRouter();
        runAndCheckIntentLaunch("^<speak>.+(?=Voy a explicarte el funcionamiento básico brevemente).+(?=Puedes pedirme que te repita los últimos párrafos del contenido).{100,}</speak>$");
        runAndCheckIntentSearchThat("^<speak>1<break[^/]+/>(?=SkyMath and the NCTM Standards).{200,}</speak>$", INTENT.searchmath);
    }

    private void recycledFlow()
    {
        String readingWitness;
        String listWitness;
        runAndCheckIntentRead(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentList(LAZY_FULL_LIST_EXPECTED_PATTERN);
        listWitness = outputStreamResult.toString();
        runAndCheckIntentRepeat(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPause(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPrevious(LAZY_EXPECTED_PATTERN);
        readingWitness = outputStreamResult.toString();
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        Assert.assertNotEquals(readingWitness, outputStreamResult.toString());
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        Assert.assertEquals(readingWitness, outputStreamResult.toString());
        runAndCheckIntentList(LAZY_FULL_LIST_EXPECTED_PATTERN);
        Assert.assertEquals(listWitness, outputStreamResult.toString());
    }

    private void runAndCheckIntentLaunch(String expectedPattern)
    {
        runIntent(INTENT.launch);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentSearchThat(String expectedPattern, INTENT testIntent)
    {
        runIntent(testIntent);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentRead(String expectedPattern)
    {
        runIntent(INTENT.readthis);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentList(String expectedPattern)
    {
        runIntent(INTENT.list);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentNext(String expectedPattern)
    {
        runIntent(INTENT.next);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentRepeat(String expectedPattern)
    {
        runIntent(INTENT.repeat);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentForward(String expectedPattern)
    {
        runIntent(INTENT.forward);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentBackward(String expectedPattern)
    {
        runIntent(INTENT.backward);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentPause(String expectedPattern)
    {
        runIntent(INTENT.pause);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentPrevious(String expectedPattern)
    {
        runIntent(INTENT.previous);
        assertSsmlRegex(expectedPattern);
    }

    private void runIntent(INTENT intent)
    {
        try
        {
            outputStreamResult = handle(buildInputEvent(intent));
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        System.out.println(outputStreamResult.toString());
    }

    private void assertSsmlRegex(String expectedSsmlPattern)
    {
        String expected;
        try
        {
            expected = FileSystemHelper.readResourceFile(
                    String.format(
                            "%s/%s",
                            SAMPLE_INTENTS_PATH,
                            SAMPLE_INTENTS_EXPECTED_FILENAME
                    )
            );
        } catch (IOException e)
        {
            throw new RuntimeException();
        }
        JSONAssert.assertEquals(
                expected,
                outputStreamResult.toString(),
                new CustomComparator(
                        JSONCompareMode.STRICT,
                        new Customization(
                                "response.outputSpeech.ssml",
                                new RegularExpressionValueMatcher<>(expectedSsmlPattern)
                        ),
                        new Customization(
                                "response.reprompt.outputSpeech.ssml",
                                new RegularExpressionValueMatcher<>(expectedSsmlPattern)
                        )
                )
        );
    }

    private void assertFileStrict(String filepath)
    {
        String expected;
        try
        {
            expected = FileSystemHelper.readResourceFile(filepath);
        } catch (IOException e)
        {
            throw new RuntimeException();
        }
        JSONAssert.assertEquals(
                expected,
                outputStreamResult.toString(),
                JSONCompareMode.STRICT
        );
    }


    private OutputStream handle(String inputEvent) throws Exception
    {
        InputStream inputStream = new ByteArrayInputStream(inputEvent.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        return outputStream;
    }
}