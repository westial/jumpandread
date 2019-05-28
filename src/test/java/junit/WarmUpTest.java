package junit;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.westial.alexa.jumpandread.DuckDuckGoJumpAndReadRouter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.FileSystemHelper;
import utils.JsonService;
import utils.JvmEnvironment;
import utils.SampleAwsContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;


public class WarmUpTest
{
    private static final String USER_SESSION_PARAM_SEPARATOR = ":";

    private Context context;

    private final static String AWS_PROFILE = "westial";

    private final static String LAZY_EXPECTED_PATTERN = "^<speak>.+</speak>$";
    private final static String LAZY_FULL_LIST_EXPECTED_PATTERN = "^<speak>1.+</speak>$";

    private final static String SAMPLE_INTENTS_PATH = "intents/websearch";
    private final static String SAMPLE_INTENTS_EXPECTED_FILENAME = "ssml_expected.json";
    private final static String SAMPLE_NO_RESULTS_DIALOG_EXPECTED_FILENAME = "ssml_noresults_dialog.json";
    private final static String SAMPLE_INTENTS_EXTENSION = ".json";
    private final static String SAMPLE_ENVIRONMENT_VARS = "environment_bypattern_es.json";
    private final static String SAMPLE_FORCE_NO_RESULTS_ENVIRONMENT_VARS = "environment_bypattern_forcenoresults.json";
    private OutputStream outputStreamResult;
    private RequestStreamHandler handler;

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
    }

    @Test
    public void warmUpTest()
    {
        try
        {
            String input = FileSystemHelper.readResourceFile(
                    String.format(
                            "%s/%s%s",
                            SAMPLE_INTENTS_PATH,
                            "warmup",
                            SAMPLE_INTENTS_EXTENSION
                    )
            );
            outputStreamResult = handle(input);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        Assert.assertTrue(outputStreamResult.toString().isEmpty());
    }


    private OutputStream handle(String inputEvent) throws Exception
    {
        setEnvironment("environment_bypattern_force_custommedium_authorlist_en.json");
        handler = new DuckDuckGoJumpAndReadRouter();
        InputStream inputStream = new ByteArrayInputStream(inputEvent.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        handler.handleRequest(inputStream, outputStream, context);
        return outputStream;
    }
}