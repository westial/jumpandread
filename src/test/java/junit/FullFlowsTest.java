package junit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.DuckDuckGoJumpAndReadRouter;
import com.westial.alexa.jumpandread.FreeFirstFailSafeJumpAndReadRouter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
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
    private final static String AWS_PROFILE_USA = "westial_usa";

    private final static String LAZY_EXPECTED_PATTERN = "^<speak>.+</speak>$";
    private final static String LAZY_FULL_LIST_EXPECTED_PATTERN = "^<speak>1.+</speak>$";

    private final static String READING_REPROMPT_EXPECTED_PATTERN = "^<speak>.{0,100}</speak>$";

    private final static String SAMPLE_INTENTS_PATH = "intents/websearch";
    private final static String SAMPLE_INTENTS_EXPECTED_FILENAME =
            "ssml_expected.json";
    private final static String SAMPLE_NO_RESULTS_DIALOG_EXPECTED_FILENAME =
            "ssml_noresults_dialog.json";
    private final static String SAMPLE_INTENTS_EXTENSION = ".json";
    private final static String SAMPLE_ENVIRONMENT_VARS =
            "environment_bypattern_es.json";
    private final static String WORD_VALIDATOR_TERMS_ENVIRONMENT_VARS =
            "environment_wordvalidator_bypattern_en_usa.json";
    private final static String WORD_VALIDATOR_READING_CONTENT_ENVIRONMENT_VARS =
            "environment_wordvalidator_reading_content_bypattern_en_usa.json";
    private final static String SAMPLE_FORCE_NO_RESULTS_ENVIRONMENT_VARS =
            "environment_bypattern_forcenoresults.json";
    private OutputStream outputStreamResult;
    private static String userId;
    private static String sessionId;
    private RequestStreamHandler handler;

    enum INTENT
    {
        backward,
        forward,
        launch,
        help,
        next,
        previous,
        pause,
        readthis,
        readnumber,
        repeat,
        searchmath,
        searchbusiness,
        searchwithnoresults,
        searchmediumnarrative,
        searchcustommedium,
        search1percentbadwords,
        search25percentbadwords,
        search50percentbadwords,
        stop,
        list
    }

    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    @Before
    public void setUp() throws Exception
    {
        context = new SampleAwsContext();
    }

    private void setEnvironment(String rawConfigFile) throws Exception
    {
        String rawConfig = FileSystemHelper.readResourceFile(rawConfigFile);

        Map<String, String> configuration =
                (HashMap<String, String>) JsonService.loads(rawConfig).get(
                        "Variables");
        configuration.put("AWS_PROFILE", AWS_PROFILE);

        for (Map.Entry<String, String> entry: configuration.entrySet())
        {
            environmentVariables.set(entry.getKey(), entry.getValue());
        }

        String token = UUID.randomUUID().toString();

        if (null != System.getProperty("userSession"))
        {
            List<String> userSessionItems = Arrays.asList(
                    System.getProperty("userSession").split(USER_SESSION_PARAM_SEPARATOR)
            );
            userId = userSessionItems.get(0);
            sessionId = userSessionItems.get(1);
        } else
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

    private void setEnvironmentUsa(String rawConfigFile) throws Exception
    {
        JvmEnvironment.setEnv(new HashMap<>());
        String rawConfig = FileSystemHelper.readResourceFile(rawConfigFile);

        Map<String, String> configuration =
                (HashMap<String, String>) JsonService.loads(rawConfig).get(
                        "Variables");
        configuration.put("AWS_PROFILE", AWS_PROFILE_USA);
        JvmEnvironment.setEnv(configuration);

        String token = UUID.randomUUID().toString();

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
    public void basicIntentsFlowWebSearch() throws Throwable
    {
        setEnvironment(SAMPLE_ENVIRONMENT_VARS);
        handler = new DuckDuckGoJumpAndReadRouter();
        if (null != System.getProperty("userSession"))
        {
            // It has to be lazy checked and avoid first intents when
            // recycling session.
            recycledFlow();
        } else
        {
            runAndCheckIntentLaunch("^<speak>.+(?=Voy a explicarte el " +
                    "funcionamiento básico brevemente).+(?=Puedes pedirme que" +
                    " te repita los últimos párrafos del contenido).{100," +
                    "}</speak>$");
            runAndCheckIntentSearchThat("^<speak>.+1<break[^/]+/>(?=SkyMath and" +
                    " the NCTM Standards).{200,}</speak>$", INTENT.searchmath);
            runAndCheckIntentRead("^<speak>.+(?=Project SkyMath: Making " +
                    "Mathematical Connections).{200,}</speak>$");
            runAndCheckIntentList("^<speak>1.+(?=SkyMath and the NCTM " +
                    "Standards.).{200,}</speak>$");
            runAndCheckIntentNext("^<speak><voice name=\".+\">" +
                    "The University Corporation for " +
                    "Atmospheric Research \\(UCAR\\) received funding from " +
                    "the National Science Foundation.{200,}</speak>$");
            runAndCheckIntentNext("^<speak>.+(?=calls for the development of " +
                    "several mathematical concepts using a single).{200," +
                    "}</speak>$");
            runAndCheckIntentRepeat("^<speak>.+(?=calls for the development " +
                    "of several mathematical concepts using a single).{200," +
                    "}</speak>$");
            runAndCheckIntentForward("^<speak>.+Those standards which are " +
                    "given explicit conceptual development are shown in the " +
                    "chart below.{200,}</speak>$");
            runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentForward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward(LAZY_EXPECTED_PATTERN);
            runAndCheckIntentBackward("^<speak>.+(?=calls for the development" +
                    " of several mathematical concepts using a single).{200," +
                    "}</speak>$");
            runAndCheckIntentPause("^<speak>.+Alexa.+</speak>$");
            runAndCheckIntentNext("^<speak>.+(?=calls for the development of " +
                    "several mathematical concepts using a single).{200," +
                    "}</speak>$");
            runAndCheckIntentNext("^<speak><voice name=\".+\">Brainstorming: Temperature and " +
                    "Temperature Changes.{200,}</speak>$");
            runAndCheckIntentPrevious("^<speak>.+(?=calls for the development" +
                    " of several mathematical concepts using a single).{200," +
                    "}</speak>$");
            runAndCheckIntentPrevious("^<speak>.+(?=received funding from the" +
                    " National Science Foundation to prepare a middle school " +
                    "mathematics module incorporating real-time weather data)" +
                    ".{200,}</speak>$");
        }
    }

    /**
     * Both with and without user and session. You, please, use without to get
     * credentials and refresh session from time to time.
     * <p>
     * Fast check with a given session and user with testing commandline
     * parameters as:
     * `-ea -DuserSession=amzn1.ask.account.test
     * .cfd24248-c697-431f-8bbd-3ce3ff30a256:amzn1.echo-api.session.test
     * .cfd24248-c697-431f-8bbd-3ce3ff30a256`
     *
     * @throws Throwable
     */
    @Test
    public void fastCheckIntentsFlowCustomMedium() throws Throwable
    {
        setEnvironment(
                "environment_bypattern_force_custommedium_authorlist_en.json");
        handler = new DuckDuckGoJumpAndReadRouter();
        if (null == System.getProperty("userSession"))
        {
            runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN,
                    INTENT.searchcustommedium);
        }
        String witness;
        runAndCheckIntentRead("^<speak>.+(?=The Tech).{100," +
                "}</speak>$");
        runAndCheckIntentRepeat("^<speak>.+(?=The Tech).+</speak>$");
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        witness = outputStreamResult.toString();
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPrevious(LAZY_EXPECTED_PATTERN);
        Assert.assertNotNull(witness);
        Assert.assertEquals("Next/Previous result is not as expected",
                witness, outputStreamResult.toString());
    }

    @Test
    public void fastCheckIntentsFlowCustomMediumUsa() throws Throwable
    {
        setEnvironmentUsa(
                "environment_bypattern_en_usa.json");
        handler = new DuckDuckGoJumpAndReadRouter();
        if (null == System.getProperty("userSession"))
        {
            runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN,
                    INTENT.searchcustommedium);
        }
        String witness;
        runAndCheckIntentRead(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentRepeat(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        witness = outputStreamResult.toString();
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPrevious(LAZY_EXPECTED_PATTERN);
        Assert.assertNotNull(witness);
        Assert.assertEquals("Next/Previous result is not as expected",
                witness, outputStreamResult.toString());
    }

    @Test
    public void fastCheckIntentsFlowWebNarrative() throws Throwable
    {
        setEnvironment("environment_bypattern_force_mediumwebnarrative_es" +
                ".json");
        handler = new DuckDuckGoJumpAndReadRouter();
        String witness;
        runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN,
                INTENT.searchmediumnarrative);
        runAndCheckIntentRead(LAZY_EXPECTED_PATTERN);
        witness = outputStreamResult.toString();
        runAndCheckIntentNext(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentPrevious(LAZY_EXPECTED_PATTERN);
        Assert.assertNotNull(witness);
        Assert.assertEquals(witness, outputStreamResult.toString());
    }

    @Test
    public void readCandidateNumberInANewSession() throws Throwable
    {
        setEnvironment("environment_bypattern_force_mediumwebnarrative_es" +
                ".json");
        handler = new DuckDuckGoJumpAndReadRouter();

        String witness;

        runIntent(INTENT.readthis);
        witness = extractSsmlMessageOnly(outputStreamResult.toString());
        String fallbackWitness = witness;

        runIntent(INTENT.next);
        Assert.assertNotNull(witness);
        Assert.assertEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.previous);
        Assert.assertNotNull(witness);
        Assert.assertEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.list);
        Assert.assertNotNull(witness);
        Assert.assertEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.pause);
        Assert.assertNotNull(witness);
        Assert.assertEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.readnumber);
        Assert.assertNotNull(witness);
        Assert.assertEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.forward);
        Assert.assertNotNull(witness);
        Assert.assertEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.backward);
        Assert.assertNotNull(witness);
        Assert.assertEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());




        runIntent(INTENT.launch);
        Assert.assertNotNull(witness);
        Assert.assertNotEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.stop);
        Assert.assertNotNull(witness);
        Assert.assertNotEquals(
                witness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        Assert.assertNotEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.help);
        Assert.assertNotNull(witness);
        Assert.assertNotEquals(
                witness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        Assert.assertNotEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());

        runIntent(INTENT.searchmath);
        Assert.assertNotNull(witness);
        Assert.assertNotEquals(
                witness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        Assert.assertNotEquals(
                fallbackWitness,
                extractSsmlMessageOnly(outputStreamResult.toString())
        );
        witness = extractSsmlMessageOnly(outputStreamResult.toString());
    }

    @Test
    public void checkFreeFirstFailSafeSearch() throws Throwable
    {
        setEnvironment(SAMPLE_ENVIRONMENT_VARS);
        handler = new FreeFirstFailSafeJumpAndReadRouter();
        runAndCheckIntentLaunch("^<speak>.+(?=Voy a explicarte el " +
                "funcionamiento básico brevemente).+(?=Puedes pedirme que te " +
                "repita los últimos párrafos del contenido).{100,}</speak>$");
        runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN, INTENT.searchmediumnarrative);
        // Search twice
        runAndCheckIntentSearchThat("^<speak>.+1<break[^/]+/>(?=SkyMath and the" +
                " NCTM Standards).{200,}</speak>$", INTENT.searchmath);
    }

    @Test
    public void checkTermsWordValidator() throws Throwable
    {
        setEnvironment(WORD_VALIDATOR_TERMS_ENVIRONMENT_VARS);
        handler = new DuckDuckGoJumpAndReadRouter();
        runAndCheckIntentLaunch(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentSearchThat("^<speak>warning.search.terms.has.bad.words.{50,200}</speak>$", INTENT.search50percentbadwords);
        runAndCheckIntentSearchThat("^<speak>.{500,}</speak>$", INTENT.search25percentbadwords);
    }

    @Test
    public void checkReadingContentWordValidator() throws Throwable
    {
        setEnvironment(WORD_VALIDATOR_READING_CONTENT_ENVIRONMENT_VARS);
        handler = new DuckDuckGoJumpAndReadRouter();
        runAndCheckIntentLaunch(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN, INTENT.search1percentbadwords);
        runAndCheckIntentReadNumber(LAZY_EXPECTED_PATTERN);
        runAndCheckIntentNext("^<speak>warning.candidate.has.bad.words.+</speak>$");
        runAndCheckIntentSearchThat(LAZY_EXPECTED_PATTERN, INTENT.searchmediumnarrative);
        runAndCheckIntentReadNumber("^<speak>(?!warning.candidate.has.bad.words.).+</speak>$");
    }

    @Test
    public void checkDialogAfterNoSearchingResults() throws Throwable
    {
        setEnvironment(SAMPLE_FORCE_NO_RESULTS_ENVIRONMENT_VARS);
        handler = new DuckDuckGoJumpAndReadRouter();
        runAndCheckIntentSearchDialogDirectives(LAZY_EXPECTED_PATTERN,
                INTENT.searchwithnoresults);
    }

    @Test
    public void checkHelpIntent() throws Throwable
    {
        setEnvironment(SAMPLE_ENVIRONMENT_VARS);
        handler = new FreeFirstFailSafeJumpAndReadRouter();
        runAndCheckIntentHelp("^<speak>(?=Para ir directamente al servicio).+" +
                "(?=Para volver a la lista de resultados).+</speak>$");
    }

    private String extractSsmlMessageOnly(String jsonInput) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {};
        Map<String, Object> inputMap = mapper.readValue(jsonInput, typeRef);
        inputMap = (Map) inputMap.get("response");
        inputMap = (Map) inputMap.get("outputSpeech");
        return (String) inputMap.get("ssml");
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

    private void runAndCheckIntentHelp(String expectedPattern)
    {
        runIntent(INTENT.help);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentSearchThat(String expectedPattern,
                                             INTENT testIntent)
    {
        runIntent(testIntent);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentSearchThatDoesNot(String expectedPattern,
                                             INTENT testIntent)
    {
        runIntent(testIntent);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentSearchDialogDirectives(String expectedPattern, INTENT testIntent)
    {
        runIntent(testIntent);
        assertSsmlRegexWithDirectives(expectedPattern);
    }

    private void runAndCheckIntentRead(String expectedPattern)
    {
        runIntent(INTENT.readthis);
        assertSsmlRegex(expectedPattern, READING_REPROMPT_EXPECTED_PATTERN);
    }

    private void runAndCheckIntentReadNumber(String expectedPattern)
    {
        runIntent(INTENT.readnumber);
        assertSsmlRegex(expectedPattern, READING_REPROMPT_EXPECTED_PATTERN);
    }

    private void runAndCheckIntentList(String expectedPattern)
    {
        runIntent(INTENT.list);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentNext(String expectedPattern)
    {
        runIntent(INTENT.next);
        assertSsmlRegex(expectedPattern, READING_REPROMPT_EXPECTED_PATTERN);
    }

    private void runAndCheckIntentRepeat(String expectedPattern)
    {
        runIntent(INTENT.repeat);
        assertSsmlRegex(expectedPattern, READING_REPROMPT_EXPECTED_PATTERN);
    }

    private void runAndCheckIntentForward(String expectedPattern)
    {
        runIntent(INTENT.forward);
        assertSsmlRegex(expectedPattern, READING_REPROMPT_EXPECTED_PATTERN);
    }

    private void runAndCheckIntentBackward(String expectedPattern)
    {
        runIntent(INTENT.backward);
        assertSsmlRegex(expectedPattern, READING_REPROMPT_EXPECTED_PATTERN);
    }

    private void runAndCheckIntentPause(String expectedPattern)
    {
        runIntent(INTENT.pause);
        assertSsmlRegex(expectedPattern);
    }

    private void runAndCheckIntentPrevious(String expectedPattern)
    {
        runIntent(INTENT.previous);
        assertSsmlRegex(expectedPattern, READING_REPROMPT_EXPECTED_PATTERN);
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

    private void assertSsmlRegex(String expectedSsmlPattern, String expectedSsmlRepromptPattern)
    {
        String expected;
        if (null == expectedSsmlRepromptPattern)
        {
            expectedSsmlRepromptPattern = expectedSsmlPattern;
        }
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
                        JSONCompareMode.LENIENT,
                        new Customization(
                                "response.outputSpeech.ssml",
                                new RegularExpressionValueMatcher<>(expectedSsmlPattern)
                        ),
                        new Customization(
                                "response.reprompt.outputSpeech.ssml",
                                new RegularExpressionValueMatcher<>(expectedSsmlRepromptPattern)
                        )
                )
        );
    }

    private void assertSsmlRegex(String expectedSsmlPattern)
    {
        assertSsmlRegex(expectedSsmlPattern, null);
    }

    private void assertSsmlRegexWithDirectives(String expectedSsmlPattern)
    {
        String expected;
        try
        {
            expected = FileSystemHelper.readResourceFile(
                    String.format(
                            "%s/%s",
                            SAMPLE_INTENTS_PATH,
                            SAMPLE_NO_RESULTS_DIALOG_EXPECTED_FILENAME
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
                        ),
                        new Customization(
                                "response.directives.updatedIntent.slots" +
                                        ".searchTerms.value",
                                new RegularExpressionValueMatcher<>(".+")
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
        InputStream inputStream =
                new ByteArrayInputStream(inputEvent.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        return outputStream;
    }
}