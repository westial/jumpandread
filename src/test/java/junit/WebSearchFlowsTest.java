package junit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.westial.alexa.jumpandread.DuckDuckGoJumpAndReadRouter;
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
 *
 *  1. As default you can play one or multiple annotated test functions and
 *     userId and sessionId will be generated new every time.
 *
 *  2. Pass userSession system property to java command if you want to recycle
 *     a userId and sessionId and do not overload the search engine service.
 *     User and session values are separated by a colon character ":"
 *     Command option example: `-DuserSession=myuserid:mysessionId`
 */
public class WebSearchFlowsTest
{
    private static final String USER_SESSION_PARAM_SEPARATOR = ":";

    private Context context;

    private final static String AWS_PROFILE = "westial";

    private final static String LAZY_EXPECTED_PATTERN = "^<speak>.+</speak>$";

    private final static String SAMPLE_INTENTS_PATH = "intents/websearch";
    private final static String SAMPLE_INTENTS_EXPECTED_FILENAME = "ssml_expected.json";
    private final static String SAMPLE_INTENTS_EXTENSION = ".json";
    private final static String SAMPLE_ENVIRONMENT_VARS = "environment_websearch_es.json";
    private OutputStream outputStreamResult;
    private static String userId;
    private static String sessionId;

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
        searchthat,
        stop
    }

    @Before
    public void setUp() throws Exception
    {
        context = new SampleAwsContext();
        String rawConfig = FileSystemHelper.readResourceFile(SAMPLE_ENVIRONMENT_VARS);

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
        else {
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

    @Test
    public void basicIntentsFlow()
    {
        if (null != System.getProperty("userSession"))
        {
            // It has to be lazy checked and avoid first intents when recycling session.
            recycledFlow();
        }
        else {
            runAndCheckIntentLaunch("^<speak>.+(?=Voy a explicarte el funcionamiento básico brevemente).+(?=Puedes pedirme que te repita los últimos párrafos del contenido).{100,}</speak>$");
            runAndCheckIntentSearchThat("^<speak>1<break[^/]+/>(?=SkyMath and the NCTM Standards.).{200,}</speak>$");
            runAndCheckIntentRead("^<speak>.+(?=Project SkyMath: Making Mathematical Connections).{200,}</speak>$");
            runAndCheckIntentNext("^<speak>The University Corporation for Atmospheric Research \\(UCAR\\) received funding from the National Science Foundation.{200,}</speak>$");
            runAndCheckIntentNext("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentRepeat("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentForward("^<speak>.+Those standards which are given explicit conceptual development are shown in the chart below.{200,}</speak>$");
            runAndCheckIntentBackward("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentPause("^<speak>.+Alexa.+</speak>$");
            runAndCheckIntentNext("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentNext("^<speak>Brainstorming: Temperature and Temperature Changes.{200,}</speak>$");
            runAndCheckIntentPrevious("^<speak>.+(?=calls for the development of several mathematical concepts using a single).{200,}</speak>$");
            runAndCheckIntentPrevious("^<speak>.+(?=received funding from the National Science Foundation to prepare a middle school mathematics module incorporating real-time weather data).{200,}</speak>$");
        }
    }

    private void recycledFlow()
    {
        String witness;
        runAndCheckIntentRead(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentRepeat(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPause(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPrevious(LAZY_EXPECTED_PATTERN);
        witness = outputStreamResult.toString();
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
        Assert.assertNotEquals(witness, outputStreamResult.toString());
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
        Assert.assertEquals(witness, outputStreamResult.toString());
    }

    private void runAndCheckIntentLaunch(String expectedPattern)
    {
        runIntent(INTENT.launch);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentSearchThat(String expectedPattern)
    {
        runIntent(INTENT.searchthat);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentRead(String expectedPattern)
    {
        runIntent(INTENT.readthis);
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
        RequestStreamHandler handler = new DuckDuckGoJumpAndReadRouter();
        handler.handleRequest(inputStream, outputStream, context);
        return outputStream;
    }
}