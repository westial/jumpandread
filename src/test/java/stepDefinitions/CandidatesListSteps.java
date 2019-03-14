package stepDefinitions;

import com.westial.alexa.jumpandread.application.GettingListUseCase;
import com.westial.alexa.jumpandread.application.command.ChildrenToSearchCommand;
import com.westial.alexa.jumpandread.application.command.GettingListCommand;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.MockCandidateRepository;
import com.westial.alexa.jumpandread.infrastructure.MockContentGetter;
import com.westial.alexa.jumpandread.infrastructure.MockContentParser;
import com.westial.alexa.jumpandread.infrastructure.MockStateRepository;
import com.westial.alexa.jumpandread.infrastructure.service.AlexaPresenter;
import com.westial.alexa.jumpandread.infrastructure.service.DynamoDbCandidateFactory;
import com.westial.alexa.jumpandread.infrastructure.service.MarginPagerEdgesCalculator;
import com.westial.alexa.jumpandread.infrastructure.service.MockTranslator;
import com.westial.alexa.jumpandread.infrastructure.service.content.RemoteTextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbState;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import utils.CandidateHelper;
import utils.FileSystemHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandidatesListSteps
{
    private CandidateRepository candidateRepository;
    private TextContentProvider contentProvider;
    private PagerEdgesCalculator partCalculator;
    private Candidate sampleCandidate;
    private ChildrenToSearchCommand addingChildrenCommand;
    private CandidateFactory candidateFactory;
    private String newOnesListing;
    private GettingListCommand gettingListCommand;
    private GettingListUseCase gettingListUseCase;
    private StateRepository stateRepository;
    private static final String INTENT_NAME = "List";
    private State state;
    private String listResult;
    private Exception exception;
    private Presenter presenter;

    @Given("^A recorded in repository sample candidate of user as \"([^\"]*)\", session as \"([^\"]*)\", from search id as \"([^\"]*)\", url as \"([^\"]*)\" with children as follows$")
    public void aSampleCandidateFromSearchIdAsWithChildren(String userId, String sessionId, String searchId, String candidateUrl, DataTable candidateTable) throws Throwable
    {
        List<List<String>> candidateData = candidateTable.raw();
        Map<String, Candidate> children = new HashMap<>();
        for (int i = 1; i < candidateData.size(); i++)
        {

            String title = candidateData.get(i).get(0);
            String url = candidateData.get(i).get(1);
            String description = candidateData.get(i).get(2);
            children.put(
                    url,
                    CandidateHelper.buildUnlistedCandidate(
                            userId,
                            sessionId,
                            searchId,
                            title,
                            url,
                            description,
                            contentProvider,
                            candidateRepository,
                            0,
                            partCalculator
                    )
            );
        }
        int candidateIndex = candidateRepository.lastIndexBySearch(searchId) + 1;
        List<List<String>> parentCandidateData = new ArrayList<>();
        parentCandidateData.add(
                CandidateHelper.createCandidateDataBySearchIdAndUrl(
                        candidateIndex, userId, sessionId, searchId, candidateUrl
                )
        );
        sampleCandidate = CandidateHelper.buildCandidate(
                0,
                parentCandidateData,
                contentProvider,
                candidateRepository,
                partCalculator,
                true
        );
        ((DynamoDbCandidate) sampleCandidate).setChildren(children);
        candidateRepository.update(sampleCandidate);
    }

    @Given("^The mock candidate repository contains candidates for user as \"([^\"]*)\", session as \"([^\"]*)\" for search id as \"([^\"]*)\" at \"([^\"]*)\"$")
    public void aMockCandidateRepositoryWithCurrentCandidatesCountForSearchIdAsAt(String userId, String sessionId, String searchId, String candidatesCount) throws Throwable
    {
        List<Candidate> candidates =
                CandidateHelper.buildRandomCandidatesBySearchId(
                        Integer.parseInt(candidatesCount),
                        userId,
                        sessionId,
                        searchId,
                        contentProvider,
                        new MockCandidateRepository(),
                        partCalculator,
                        true
                );
        for (Candidate candidate: candidates)
        {
            candidateRepository.update(candidate);
        }
    }

    @Given("^A mock text content provider for candidate listing$")
    public void aMockTextContentProviderForCandidateListing()
    {
        contentProvider = new RemoteTextContentProvider(new MockContentGetter(null), new MockContentParser());
    }

    @And("^A margin edges calculator service for candidate listing$")
    public void aMockEdgesCalculatorServiceForCandidateListing()
    {
        partCalculator = new MarginPagerEdgesCalculator(500, 100);
    }

    @And("^An adding children to search candidates command$")
    public void anAddingChildrenToSearchCandidatesCommand()
    {
        addingChildrenCommand = new ChildrenToSearchCommand(
                candidateFactory,
                candidateRepository
        );
    }

    @When("^I execute the adding children command for the given sample candidate$")
    public void iExecuteTheAddingChildrenCommandForTheGivenSearchIdAndTheSampleCandidate() throws Throwable
    {
        newOnesListing = addingChildrenCommand.execute(sampleCandidate);
    }

    @Then("^The mock candidate repository count is as \"([^\"]*)\"$")
    public void theMockCandidateRepositoryCountIsAs(String expected) throws Throwable
    {
        Assert.assertEquals(
                Integer.parseInt(expected),
                candidateRepository.countBySearch(sampleCandidate.getSearchId())
        );
    }

    @And("^A candidate factory for candidate listing$")
    public void aCandidateFactoryForCandidateListing()
    {
        candidateFactory = new DynamoDbCandidateFactory(
                contentProvider,
                candidateRepository,
                100,
                partCalculator
        );
    }

    @And("^Getting list result as in file \"([^\"]*)\"$")
    public void theListingCommandResultIsAsInFile(String expectedFileContent) throws Throwable
    {
        Assert.assertEquals(
                FileSystemHelper.readResourceFile(expectedFileContent),
                listResult
        );
    }

    @And("^The listing command result is as \"([^\"]*)\"$")
    public void theListingCommandResultIsAs(String expected) throws Throwable
    {
        Assert.assertEquals(
                expected,
                newOnesListing
        );
    }

    @And("^A recorded in repository sample candidate of user as \"([^\"]*)\", session as \"([^\"]*)\", from search id as \"([^\"]*)\", url as \"([^\"]*)\" with no children$")
    public void aRecordedInRepositorySampleCandidateOfUserAsSessionAsFromSearchIdAsUrlAsWithNoChildren(String userId, String sessionId, String searchId, String candidateUrl) throws Throwable
    {
        int candidateIndex = candidateRepository.lastIndexBySearch(searchId) + 1;
        List<List<String>> parentCandidateData = new ArrayList<>();
        parentCandidateData.add(
                CandidateHelper.createCandidateDataBySearchIdAndUrl(
                        candidateIndex, userId, sessionId, searchId, candidateUrl
                )
        );
        sampleCandidate = CandidateHelper.buildCandidate(
                0,
                parentCandidateData,
                contentProvider,
                candidateRepository,
                partCalculator,
                true
        );
        candidateRepository.update(sampleCandidate);
    }

    @And("^A mock candidate repository for listing$")
    public void aMockCandidateRepositoryForListing()
    {
        candidateRepository = new MockCandidateRepository();
    }

    @And("^A command for getting list by search ID$")
    public void aCommandForGettingListBySearchID()
    {
        gettingListCommand = new GettingListCommand(candidateRepository);
    }

    @And("^A getting search if list use case$")
    public void aGettingSearchIfListUseCase()
    {
        gettingListUseCase = new GettingListUseCase(
                state,
                gettingListCommand,
                presenter
        );
    }

    @When("^I invoke getting list use case$")
    public void iInvokeGettingListUseCaseForSearchIdAs() throws Throwable
    {
        try
        {
            listResult = gettingListUseCase.invoke().getSpeech();
        } catch (Exception e)
        {
            exception = e;
        }
    }

    @And("^A user state repository for listing$")
    public void aUserStateRepositoryForListing()
    {
        stateRepository = new MockStateRepository();
    }

    @And("^A current state for listing with user Id as \"([^\"]*)\", session Id as \"([^\"]*)\", search Id as \"([^\"]*)\", candidateIndex as \"([^\"]*)\"$")
    public void aCurrentStateForListingWithUserIdAsSessionIdAsSearchIdAsCandidateIndexAs(String userId, String sessionId, String searchId, String rawCandidateIndex) throws Throwable
    {
        state = new DynamoDbState(stateRepository, userId, sessionId, INTENT_NAME, searchId);
        state.updateCandidateIndex(Integer.parseInt(rawCandidateIndex));
    }

    @And("^A current state for listing with user Id as \"([^\"]*)\", session Id as \"([^\"]*)\", no search Id$")
    public void aCurrentStateForListingWithUserIdAsSessionIdAsNoSearchId(String userId, String sessionId) throws Throwable
    {
        state = new DynamoDbState(stateRepository, userId, sessionId);
    }

    @Then("^Use case threw an exception of type \"([^\"]*)\"$")
    public void useCaseThrewAnExceptionOfType(String expectedName) throws Throwable
    {
        switch (expectedName)
        {
            case "MandatorySearchException":
                Assert.assertTrue(exception instanceof MandatorySearchException);
                break;

            case "NoSearchResultsException":
                Assert.assertTrue(exception instanceof NoSearchResultsException);
                break;
        }
    }

    @And("^An Alexa Presenter service for listing$")
    public void anAlexaPresenterServiceForListing()
    {
        presenter = new AlexaPresenter(new MockTranslator());
    }
}
