package stepDefinitions;

import com.westial.alexa.jumpandread.application.*;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.MockCandidateRepository;
import com.westial.alexa.jumpandread.infrastructure.MockPresenter;
import com.westial.alexa.jumpandread.infrastructure.MockQueueContentGetter;
import com.westial.alexa.jumpandread.infrastructure.MockStateRepository;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbState;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RetrieveParagraphsSteps
{
    private TextContentParser contentParser;
    private CandidateRepository candidateRepository;
    private ContentGetter contentGetter;
    private Presenter presenter;
    private StateRepository stateRepository;
    private static final String INTENT_NAME = "RetrievingParagraphs";
    private State state;
    private CandidateFactory candidateFactory;
    private List<Candidate> candidates;
    private int defaultCandidatesFactor;
    private UseCaseFactory useCaseFactory;
    private CurrentUseCase currentUseCase;
    private View resultView;
    private ForwardUseCase nextUseCase;
    private BackwardUseCase repeatUseCase;
    private int defaultParagraphsGroup;
    private ForwardUseCase forwardUseCase;
    private int defaultJumpingFactor;
    private BackwardUseCase backwardUseCase;
    private PauseUseCase pauseUseCase;
    private TextContentProvider contentProvider;
    private PagerEdgesCalculator partCalculator = new MarginPagerEdgesCalculator(
            50,
            20
    );

    @Given("^A candidate factory for parsing$")
    public void aCandidateFactory() throws Throwable
    {
        candidateFactory = new DynamoDbCandidateFactory(
                contentProvider,
                candidateRepository,
                100,
                partCalculator
        );
    }

    @Given("^A multiple candidate repository for jumping$")
    public void aMultipleCandidateRepositoryForJumping() throws Throwable
    {
        candidateRepository = new MockCandidateRepository(candidates);
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
                contentProvider,
                candidateRepository,
                Integer.parseInt(candidateData.get(index).get(7)),
                100,
                partCalculator
        );
    }

    @Given("^A searching result candidate list as follows$")
    public void aSearchingResultCandidateListAsFollows(DataTable candidateTable) throws Throwable
    {
        List<List<String>> candidateData = candidateTable.raw();
        candidates = new ArrayList<>();
        for (int i = 1; i < candidateData.size(); i++)
        {
            candidates.add(
                    buildCandidate(i, candidateData)
            );
        }
    }

    @Given("^An address document getter with forced and queued contents as in files as follows$")
    public void aCandidateDocumentGetterWithForcedAndQueuedContentsAsInFilesAsFollows(DataTable dataTable) throws Throwable
    {
        List<List<String>> dataTableList = dataTable.raw();
        Queue<String> contents = new LinkedList<>();
        for (int i = 0; i < dataTableList.size(); i++)
        {
            String content = FileSystemService.readResourceFile(
                    dataTableList.get(i).get(0)
            );
            contents.add(content);
        }
        contentGetter = new MockQueueContentGetter(contents);
    }

    @Given("^A current state with user Id as \"([^\"]*)\", session Id as \"([^\"]*)\", search Id as \"([^\"]*)\", candidateIndex as \"([^\"]*)\"$")
    public void aCurrentStateWithUserIdAsSessionIdAsSearchIdAsCandidateIndexAs(String userId, String sessionId, String searchId, String rawCandidateIndex) throws Throwable
    {
        state = new DynamoDbState(stateRepository, userId, sessionId, INTENT_NAME, searchId);
        state.updateCandidateIndex(Integer.parseInt(rawCandidateIndex));
    }

    @Given("^A user state repository for parsing$")
    public void aUserStateRepositoryForReading() throws Throwable
    {
        stateRepository = new MockStateRepository();
    }

    @Given("^A web search candidate parser$")
    public void anHtmlFormatCandidateParser() throws Throwable
    {
        contentParser = new WebSearchTextContentParser();
    }

    @Given("^An Alexa Presenter service$")
    public void anAlexaPresenterService()
    {
        presenter = new AlexaPresenter(new MockTranslator());
    }

    @Given("^A Mock Presenter service$")
    public void aMockPresenterService()
    {
        presenter = new MockPresenter(new MockTranslator());
    }

    @Given("^A configuration value for default grouping paragraphs as \"([^\"]*)\"$")
    public void aConfigurationValueForJoiningParagraphsAs(String rawParagraphsGroup) throws Throwable
    {
        defaultParagraphsGroup = Integer.parseInt(rawParagraphsGroup);
    }

    @Given("^A configuration value for default candidates factor as \"([^\"]*)\"$")
    public void aConfigurationValueForCandidatesFactorAs(String rawCandidatesFactor) throws Throwable
    {
        defaultCandidatesFactor = Integer.parseInt(rawCandidatesFactor);
    }

    @Given("^A Use Case Factory for reading only$")
    public void aUseCaseFactoryForReadingOnly()
    {
        useCaseFactory = new UseCaseFactory(
                candidateRepository,
                candidateFactory,
                null,
                state,
                presenter,
                defaultCandidatesFactor,
                defaultParagraphsGroup
        );
    }

    @Given("^A newly created use case for current candidate reading$")
    public void aNewlyCreatedUseCaseForCurrentCandidateReading()
    {
        currentUseCase = useCaseFactory.createCurrent();
    }

    @When("^I invoke current candidate use case for intent name as \"([^\"]*)\", candidate index as \"([^\"]*)\", paragraphs group as \"([^\"]*)\"$")
    public void iInvokeCurrentCandidateUseCaseForIntentNameAsCandidateIndexAs(String intentName, String rawCandidateIndex, String rawParagraphsGroup) throws Throwable
    {
        resultView = currentUseCase.invoke(intentName, Integer.parseInt(rawCandidateIndex), Integer.parseInt(rawParagraphsGroup));
    }

    @Then("^The result after invocation is not null$")
    public void theResultAfterInvocationIsAViewTypedObject()
    {
        Assert.assertNotNull(resultView);
    }

    @Then("^The speech contained in result is as in file \"([^\"]*)\"$")
    public void theSpeechContainedInResultIsAsInFile(String expectedResultFilePath) throws Throwable
    {
        String expected = FileSystemService.readResourceFile(expectedResultFilePath);
        System.out.println("\nExpected: " + expected);
        System.out.println("\nResult Text: " + resultView.getSpeech());
        Assert.assertEquals(expected, resultView.getSpeech());
    }

    @Given("^A newly created use case for reading next$")
    public void aNewlyCreatedUseCaseForReadingNextWithParagraphsGroupingAs() throws Throwable
    {
        nextUseCase = useCaseFactory.createForward();
    }

    @When("^I invoke next candidate use case for intent name as \"([^\"]*)\", paragraphs group as \"([^\"]*)\"$")
    public void iInvokeNextCandidateUseCaseForIntentNameAs(String intentName, String rawParagraphsGroup) throws Throwable
    {
        resultView = nextUseCase.invoke(intentName, null, 1, 1, Integer.parseInt(rawParagraphsGroup));
    }

    @Given("^A newly created use case for repeat reading$")
    public void aNewlyCreatedUseCaseForRepeatReadingWithParagraphsGroupingAs() throws Throwable
    {
        repeatUseCase = useCaseFactory.createBackward();
    }

    @When("^I invoke repeat reading use case for intent name as \"([^\"]*)\", paragraphs group as \"([^\"]*)\"$")
    public void iInvokeRepeatReadingUseCaseForIntentNameAs(String intentName, String rawParagraphsGroup) throws Throwable
    {
        resultView = repeatUseCase.invoke(intentName, null, 1, 0, Integer.parseInt(rawParagraphsGroup));
    }

    @Given("^The current state candidate index is as \"([^\"]*)\"$")
    public void theCurrentStateCandidateIndexIsAs(String rawCandidateIndex) throws Throwable
    {
        Assert.assertEquals(rawCandidateIndex, String.valueOf(state.getCandidateIndex()));
    }

    @Then("^The speech contained in result is as \"([^\"]*)\"$")
    public void theSpeechContainedInResultIsAs(String expected) throws Throwable
    {
        Assert.assertEquals(expected, resultView.getSpeech());
    }

    @Given("^A new state with user Id as \"([^\"]*)\", session Id as \"([^\"]*)\"$")
    public void aNewStateWithUserIdAsSessionIdAs(String userId, String sessionId) throws Throwable
    {
        state = new DynamoDbState(stateRepository, userId, sessionId);
    }

    @Given("^A configuration value for default jumping factor as \"([^\"]*)\"$")
    public void aConfigurationValueForDefaultJumpingFactorAs(String rawFactor) throws Throwable
    {
        defaultJumpingFactor = Integer.parseInt(rawFactor);
    }

    @Given("^A forward created use case for reading as times as default jumping factor after next$")
    public void aForwardCreatedUseCaseForReadingAfterNext()
    {
        forwardUseCase = useCaseFactory.createForward();
    }

    @When("^I invoke forward candidate use case for intent name as \"([^\"]*)\", paragraphs group as \"([^\"]*)\"$")
    public void iInvokeForwardCandidateUseCaseForIntentNameAsParagraphsGroupAs(String intentName, String rawParagraphsGroup) throws Throwable
    {
        resultView = forwardUseCase.invoke(
                intentName,
                null,
                1,
                defaultJumpingFactor,
                Integer.parseInt(rawParagraphsGroup)
        );
    }

    @Given("^A backward created use case for reading as times as default jumping factor after last$")
    public void aBackwardCreatedUseCaseForReadingAsTimesAsDefaultJumpingFactorAfterLast()
    {
        backwardUseCase = useCaseFactory.createBackward();
    }

    @When("^I invoke backward candidate use case for intent name as \"([^\"]*)\", paragraphs group as \"([^\"]*)\"$")
    public void iInvokeBackwardCandidateUseCaseForIntentNameAsParagraphsGroupAs(String intentName, String rawParagraphsGroup) throws Throwable
    {
        resultView = backwardUseCase.invoke(
                intentName,
                null,
                1,
                defaultJumpingFactor,
                Integer.parseInt(rawParagraphsGroup)
        );
    }

    @Then("^The current state candidate paragraph position is as \"([^\"]*)\"$")
    public void theCurrentStateCandidateParagraphPositionIsAs(String expected) throws Throwable
    {
        Candidate candidate = candidateRepository.get(
                state.getSearchId(),
                state.getCandidateIndex()
        );
        int currentPosition = candidate.getParagraphPosition();
        Assert.assertEquals(expected, String.valueOf(currentPosition));
    }

    @Given("^A newly created use case for pause candidate reading$")
    public void aNewlyCreatedUseCaseForPauseCandidateReading()
    {
        pauseUseCase = useCaseFactory.createPause();
    }

    @When("^I invoke pause candidate use case for intent name as \"([^\"]*)\", paragraphs group as \"([^\"]*)\"$")
    public void iInvokePauseCandidateUseCaseForIntentNameAsParagraphsGroupAs(String intentName, String rawParagraphsGroup) throws Throwable
    {
        resultView = pauseUseCase.invoke(
                intentName,
                defaultJumpingFactor,
                Integer.parseInt(rawParagraphsGroup)
        );
    }

    @Given("^A mock text content provider for retrieval$")
    public void aMockContentProvider()
    {
        contentProvider = new RemoteTextContentProvider(contentGetter, contentParser);
    }
}
