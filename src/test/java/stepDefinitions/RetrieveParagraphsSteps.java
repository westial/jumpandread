package stepDefinitions;

import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.MockCandidateGetter;
import com.westial.alexa.jumpandread.infrastructure.MockCandidateRepository;
import com.westial.alexa.jumpandread.infrastructure.MockQueueCandidateGetter;
import com.westial.alexa.jumpandread.infrastructure.MockStateRepository;
import com.westial.alexa.jumpandread.infrastructure.service.AlexaOutputFormatter;
import com.westial.alexa.jumpandread.infrastructure.service.DynamoDbCandidateFactory;
import com.westial.alexa.jumpandread.infrastructure.service.FileSystemService;
import com.westial.alexa.jumpandread.infrastructure.service.JsoupCandidateParser;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbState;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RetrieveParagraphsSteps
{
    private CandidateParser candidateParser;
    private Candidate candidate;
    private CandidateRepository candidateRepository;
    private CandidateGetter candidateGetter;
    private RuntimeException commandException;
    private OutputFormatter outputFormatter;
    private int paragraphsGroup;
    private RetrieveParagraphsCommand retrieveCommand;
    private String userId;
    private String sessionId;
    private String searchId;
    private String resultText;
    private String errorMessage;
    private StateRepository stateRepository;
    private static final String INTENT_NAME = "RetrievingParagraphs";
    private State state;
    private StateFactory stateFactory;
    private CandidateFactory candidateFactory;
    private JumpCommand jumpCommand;
    private int candidateIndex;
    private List<Candidate> candidates;

    @Given("^An Alexa output formatter$")
    public void anAlexaOutputFormatter() throws Throwable
    {
        outputFormatter = new AlexaOutputFormatter();
    }

    @Given("^An html candidate parser$")
    public void anHtmlFormatCandidateParser() throws Throwable
    {
        candidateParser = new JsoupCandidateParser();
    }

    @Given("^A configuration value for joining paragraphs as \"([^\"]*)\"$")
    public void aConfigurationValueForJoiningParagraphsAs(String paragraphsGroup) throws Throwable
    {
        this.paragraphsGroup = Integer.parseInt(paragraphsGroup);
    }

    @Given("^A user with user id as \"([^\"]*)\", session id as \"([^\"]*)\", search id as \"([^\"]*)\"$")
    public void aUserWithUserIdAs(String userId, String sessionId, String searchId) throws Throwable
    {
        this.userId = userId;
        this.sessionId = sessionId;
        this.searchId = searchId;
    }

    @Given("^A candidate document getter with forced content as in file \"([^\"]*)\"$")
    public void aSampleHtmlDocumentAs(String filePath) throws Throwable
    {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(filePath);

        String content = FileUtils.readFileToString(
                new File(url.toURI()),
                StandardCharsets.UTF_8
        );
        candidateGetter = new MockCandidateGetter(content);
    }

    @Given("^A candidate repository for parsing$")
    public void aCandidateRepository() throws Throwable
    {
        candidateRepository = new MockCandidateRepository(candidate);
    }

    @Given("^A selected candidate as follows$")
    public void aSelectedCandidateAsFollows(DataTable candidateTable) throws Throwable
    {
        candidate = buildCandidate(1, candidateTable.raw());
    }

    private Candidate buildCandidate(int index, List<List<String>> candidateData)
    {
        return new DynamoDbCandidate(
                DynamoDbCandidate.buildId(
                        candidateData.get(index).get(3),
                        Integer.parseInt(candidateData.get(index).get(0))
                ),
                Integer.parseInt(candidateData.get(index).get(0)),
                candidateData.get(index).get(1),
                candidateData.get(index).get(2),
                candidateData.get(index).get(3),
                candidateData.get(index).get(4),
                candidateData.get(index).get(5),
                candidateData.get(index).get(6),
                candidateGetter,
                candidateParser,
                candidateRepository,
                outputFormatter,
                Integer.parseInt(candidateData.get(index).get(7))
        );
    }

    @Given("^A searching result candidate list as follows$")
    public void aSearchingResultCandidateListAsFollows(DataTable candidateTable) throws Throwable
    {
        List<List<String>> candidateData = candidateTable.raw();
        candidates = new ArrayList<>();
        for (int i = 1; i < candidateData.size(); i ++)
        {
            candidates.add(
                    buildCandidate(i, candidateData)
            );
        }
    }

    @Given("^A candidate factory for parsing$")
    public void aCandidateFactory() throws Throwable
    {
        candidateFactory = new DynamoDbCandidateFactory(
                candidateGetter,
                candidateParser,
                candidateRepository,
                outputFormatter
        );
    }

    @Given("^A retrieving paragraphs command$")
    public void aRetrievingParagraphsCommand() throws Throwable
    {
        retrieveCommand = new RetrieveParagraphsCommand(
                candidateFactory,
                paragraphsGroup
        );
    }

    @Then("^Command returned a text as in file \"([^\"]*)\"$")
    public void retrievingCommandReturnedATextAsInFile(String filePath) throws Throwable
    {
        String expected = FileSystemService.readResourceFile(filePath);
        System.out.println("Expected: " + expected);
        System.out.println("Result Text: " + resultText);
        Assert.assertEquals(expected, resultText);
    }

    @Then("^Command thrown an exception with the message as \"([^\"]*)\"$")
    public void commandThrownAnExceptionWithTheMessageAs(String expected) throws Throwable
    {
        Assert.assertEquals(expected, errorMessage);
    }

    @Given("^A user state repository for parsing$")
    public void aUserStateRepositoryForReading() throws Throwable
    {
        stateRepository = new MockStateRepository();
    }

    @Given("^A current state with user Id as \"([^\"]*)\", session Id as \"([^\"]*)\", search Id as \"([^\"]*)\"$")
    public void aCurrentStateWithUserIdAsSessionIdAsSearchIdAs(String userId, String sessionId, String searchId) throws Throwable
    {
        state = new DynamoDbState(stateRepository, userId, sessionId, INTENT_NAME, searchId);
    }

    @When("^I execute retrieving command for user and session and number of candidate as \"([^\"]*)\" for intent \"([^\"]*)\"$")
    public void iExecuteRetrievingCommandForUserAndSessionAndNumberOfCandidateAsForIntent(String candidateIndex, String intent) throws Throwable
    {
        try
        {
            resultText = retrieveCommand.execute(state, Integer.parseInt(candidateIndex));
        }
        catch (NoCandidateMandatorySearchException exception)
        {
            errorMessage = exception.getMessage();
        }
    }

    @Given("^A jump command$")
    public void aJumpCommand() throws Throwable
    {
        jumpCommand = new JumpCommand(
                candidateFactory,
                paragraphsGroup,
                outputFormatter
        );
    }

    @When("^I execute jump command for current state$")
    public void iExecuteJumpCommandForCurrentState() throws Throwable
    {
        try
        {
            resultText = jumpCommand.execute(state);
        }
        catch (IncompleteStateMandatorySearchException | NoCandidateMandatorySearchException exception)
        {
            errorMessage = exception.getMessage();
        }
    }

    @Given("^A current state with user Id as \"([^\"]*)\", session Id as \"([^\"]*)\", search Id as \"([^\"]*)\", candidateIndex as \"([^\"]*)\"$")
    public void aCurrentStateWithUserIdAsSessionIdAsSearchIdAsCandidateIndexAs(String userId, String sessionId, String searchId, String rawCandidateIndex) throws Throwable
    {
        state = new DynamoDbState(stateRepository, userId, sessionId, INTENT_NAME, searchId);
        state.updateCandidateIndex(Integer.parseInt(rawCandidateIndex));
    }

    @Given("^A candidate document getter with forced and queued contents as in files as follows$")
    public void aCandidateDocumentGetterWithForcedAndQueuedContentsAsInFilesAsFollows(DataTable dataTable) throws Throwable
    {
        List<List<String>> dataTableList = dataTable.raw();
        Queue<String> contents = new LinkedList<>();
        for (int i = 0; i <= dataTableList.get(0).size(); i ++)
        {
            String content = FileSystemService.readResourceFile(
                    dataTableList.get(i).get(0)
            );
            contents.add(content);
        }
        candidateGetter = new MockQueueCandidateGetter(contents);
    }

    @Given("^A multiple candidate repository for jumping$")
    public void aMultipleCandidateRepositoryForJumping() throws Throwable
    {
        candidateRepository = new MockCandidateRepository(candidates);
    }
}
