package stepDefinitions;

import com.westial.alexa.jumpandread.application.command.SearchCandidatesCommand;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.application.exception.UnappropriateContentException;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.*;
import com.westial.alexa.jumpandread.infrastructure.exception.EngineNoSearchResultException;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import com.westial.alexa.jumpandread.infrastructure.exception.WebClientSearchException;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import com.westial.alexa.jumpandread.infrastructure.service.content.RemoteTextContentProvider;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import utils.FileSystemHelper;
import utils.JsonService;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

public class SearchCandidatesSteps
{
    private static int STARTING_CANDIDATE_INDEX = 1;
    private CandidatesSearch candidatesSearch;
    private StateRepository stateRepository;
    private SearchCandidatesCommand searchCandidates;
    private String foundCandidates;
    private CandidateFactory candidateFactory;
    private TextContentParser contentParser;
    private CandidateRepository candidateRepository;
    private ContentGetter contentGetter;
    private Presenter presenter;
    private StateFactory stateFactory;
    private State state;
    private DuckDuckGoResultParser pageParser;
    private WebClient pageClient;
    private List<Candidate> candidates;
    private HeadersProvider headersProvider;
    private DuckDuckGoLocaleProvider duckLocaleProvider;
    private TextContentProvider contentProvider;
    private Exception exception;
    private CandidatesSearchFactory searchFactory;
    private CandidatesSearchFactory failSafeSearchFactory;
    private Configuration configuration;
    private CandidatesSearchFactory secondSearchFactory;

    @Given("^A local web client service with a forced content as in file as \"([^\"]*)\"$")
    public void anHtmlSearchResultPageAsInFileAs(String fileName) throws Throwable
    {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(fileName);

        String filePath = "file://" + url.getPath();
        pageClient = new LocalWebClient(filePath);
    }

    @Given("^A DuckDuckGo page parser service$")
    public void aDuckDuckGoPageParserServiceForUrlAs() throws Throwable
    {
        pageParser = new JsoupDuckDuckGoResultParser(new RegexTextCleaner("\\b[^-]+"));
    }

    @Given("^A DuckDuckGo candidates search service for url as \"([^\"]*)\", iso 4-letters locale as \"([^\"]*)\"$")
    public void aDuckDuckGoCandidatesSearchService(String duckUrl, String iso4Locale)
    {
        candidatesSearch = new DuckDuckGoCandidatesSearch(
                STARTING_CANDIDATE_INDEX,
                pageClient,
                pageParser,
                duckUrl,
                headersProvider,
                duckLocaleProvider,
                iso4Locale,
                false,
                candidateFactory,
                "filetype:html OR filetype:htm"
        );
    }

    @Given("^A random headers provider service for agents file as \"([^\"]*)\", languages file as \"([^\"]*)\", referrers file as \"([^\"]*)\"$")
    public void aRandomHeadersProviderServiceForAgentsFileAsLanguagesFileAsReferrersFileAs(
            String agentsFileName,
            String languagesFileName,
            String referrersFileName
    ) throws Throwable
    {
        headersProvider = new RandomDuckDuckGoHeadersProvider(
                agentsFileName,
                languagesFileName,
                referrersFileName
        );
    }

    @Given("^A random duckduckgo locale provider service for available locales file as \"([^\"]*)\"$")
    public void aRandomDuckduckgoLocaleProviderServiceForAvailableLocalesFileAs(String filePath) throws Throwable
    {
        duckLocaleProvider = new RandomDuckDuckGoLocaleProvider(filePath);
    }

    @When("^I ask to find candidates to search service for user with ID as \"([^\"]*)\", session ID as \"([^\"]*)\", search ID as \"([^\"]*)\", terms as \"([^\"]*)\"$")
    public void iAskToFindCandidatesToSearchServiceForUserWithIDAsSessionIDAsSearchIDAsTermsAs(String userId, String sessionId, String searchId, String terms)
    {
        try
        {
            candidates = candidatesSearch.find(
                    new User(userId, sessionId),
                    searchId,
                    terms
            );
        } catch (SearchException | NoSearchResultException e)
        {
            exception = e;
        }
    }

    @Then("^The service returned a list with \"([^\"]*)\" candidates$")
    public void theServiceReturnedAListWithCandidates(String expectedCount) throws Throwable
    {
        Assert.assertEquals(Integer.parseInt(expectedCount), candidates.size());
    }

    @Then("^Candidate with position \"([^\"]*)\" in list has property \"([^\"]*)\" as \"([^\"]*)\"$")
    public void candidateWithPositionInListHasPropertyAs(String candidateIndex, String property, String expected) throws Throwable
    {
        Candidate candidate = candidates.get(Integer.parseInt(candidateIndex));
        String result;
        switch (property)
        {
            case "title":
                result = candidate.getTitle();
                break;

            case "description":
                result = candidate.getDescription();
                break;

            case "url":
                result = candidate.getUrl();
                break;

            default:
                throw new RuntimeException("Unexpected property to assert");
        }
        Assert.assertEquals(expected, result);
    }

    @Given("^A candidates search service with \"([^\"]*)\" forced results$")
    public void aSearchingServiceWithForcedResults(String forcedResults) throws Throwable
    {
        candidatesSearch = new MockCandidatesSearch(
                Integer.parseInt(forcedResults),
                candidateFactory
        );
    }

    @Given("^A user state repository$")
    public void aUserSessionRepositoryWithCurrentIntentAt() throws Throwable
    {
        stateRepository = new MockStateRepository();
    }

    @Given("^A mock text content parser$")
    public void aMockPageParser() throws Throwable
    {
        contentParser = new MockContentParser();
    }

    @Given("^A searching step command$")
    public void aSearchingCommand() throws Throwable
    {
        searchCandidates = new SearchCandidatesCommand(
                candidatesSearch,
                null
        );
    }

    @When("^I execute the step command with the terms \"([^\"]*)\"$")
    public void iExecuteTheCommandWithTheTerms(String searchTerms)
    {
        try
        {
            Pair<Integer, String> results = searchCandidates.execute(state, searchTerms);
            foundCandidates = results.getRight();
        } catch (SearchException | NoSearchResultException | UnappropriateContentException e)
        {
            exception = e;
        }
    }

    @Then("^Command returns nothing$")
    public void commandReturnsNothing() throws Throwable
    {
        Assert.assertNull(foundCandidates);
    }

    @Then("^The current intent in state repository is \"([^\"]*)\"$")
    public void theCurrentIntentInStateRepositoryIs(String expectedIntent) throws Throwable
    {
        Assert.assertEquals(
                expectedIntent,
                stateRepository.get(state.getUserId(), state.getSessionId()).getIntent()
        );
    }

    @Given("^A candidate repository$")
    public void aCandidateRepository() throws Throwable
    {
        candidateRepository = new MockCandidateRepository();
    }

    @Given("^A candidate factory$")
    public void aCandidateFactory() throws Throwable
    {
        PagerEdgesCalculator partCalculator = new MarginPagerEdgesCalculator(
                50,
                20
        );
        candidateFactory = new DynamoDbCandidateFactory(
                contentProvider,
                candidateRepository,
                100,
                partCalculator
        );
    }

    @Given("^An address document getter$")
    public void aCandidateDocumentGetter() throws Throwable
    {
        contentGetter = new MockContentGetter("<html></html>");
    }

    @Given("^An Alexa output formatter for searching$")
    public void anAlexaOutputFormatterForSearching() throws Throwable
    {
        presenter = new AlexaPresenter(new MockTranslator());
    }

    @Then("^Searching command returned a text with points \"([^\"]*)\" to \"([^\"]*)\"$")
    public void searchingCommandReturnedATextWithPointsTo(String from, String to) throws Throwable
    {
        for (int number = Integer.parseInt(from); number <= Integer.parseInt(to); number++)
        {
            Assert.assertThat(
                    foundCandidates,
                    containsString(
                            String.format("%d.", number)
                    )
            );
        }
        Assert.assertThat(
                foundCandidates,
                not(
                        containsString(
                                String.format("%d.", Integer.parseInt(to) + 1)
                        )
                )
        );
    }

    @Then("^Candidate repository contains exactly \"([^\"]*)\" candidates$")
    public void candidateRepositoryContainsExactlyCandidates(String expected) throws Throwable
    {
        String searchId = ((MockCandidateRepository) candidateRepository).testOnlyGetLastSearchId();
        Assert.assertEquals(
                Integer.parseInt(expected),
                (candidateRepository.countBySearch(searchId))
        );
    }

    @Given("^A user state factory$")
    public void aStateFactory() throws Throwable
    {
        stateFactory = new DynamoDbStateFactory(stateRepository);
    }

    @Given("^A state with the user as \"([^\"]*)\", session as \"([^\"]*)\" and intent as \"([^\"]*)\"$")
    public void aStateWithTheUserAsSessionAsAndIntentAs(String userId, String sessionId, String intent) throws Throwable
    {
        state = stateFactory.create(userId, sessionId, intent, "no matters", "no matters");
    }

    @Then("^The service returned no candidates$")
    public void theServiceReturnedNoCandidates()
    {
        Assert.assertNull(candidates);
    }

    @Given("^A mock text content provider$")
    public void aMockContentProvider()
    {
        contentProvider = new RemoteTextContentProvider(contentGetter, contentParser);
    }

    @Then("^Command threw a no results exception$")
    public void commandThrewAExceptionTypeAs() throws Throwable
    {
        Assert.assertTrue(exception instanceof NoSearchResultException);
    }

    @And("^A mock candidates search factory for the candidates search service above$")
    public void aMockCandidatesSearchFactoryForTheCandidatesSearchServiceAbove()
    {
        searchFactory = new MockCandidatesSearchFactory(candidatesSearch);
    }

    @And("^A fail-safe candidates search factory with \"([^\"]*)\" instances of first search factory$")
    public void aFailSafeCandidatesSearchFactoryWithInstancesOfOtherSearchFactories(String rawFactoriesCount) throws Throwable
    {
        int dependentOnCount = Integer.parseInt(rawFactoriesCount);
        CandidatesSearchFactory[] dependentOnFactories =
                new CandidatesSearchFactory[dependentOnCount];
        for (int index = 0; index < dependentOnFactories.length; index ++)
        {
            dependentOnFactories[index] = searchFactory;
        }
        failSafeSearchFactory = new FailSafeCandidatesSearchFactory(dependentOnFactories);
    }

    @And("^A fail-safe candidates search factory with \"([^\"]*)\" instances of first search factory and with \"([^\"]*)\" instances of second one$")
    public void aFailSafeCandidatesSearchFactoryWithInstancesOfBothSearchFactories(String rawFirstFactoryCount, String rawSecondFactoryCount) throws Throwable
    {
        int dependentOnFirstCount = Integer.parseInt(rawFirstFactoryCount);
        int dependentOnSecondCount = Integer.parseInt(rawSecondFactoryCount);
        CandidatesSearchFactory[] dependentOnFactories =
                new CandidatesSearchFactory[dependentOnFirstCount + dependentOnSecondCount];
        for (int index = 0; index < dependentOnFirstCount; index ++)
        {
            dependentOnFactories[index] = searchFactory;
        }
        for (int index = dependentOnFirstCount; index < dependentOnFactories.length; index ++)
        {
            dependentOnFactories[index] = secondSearchFactory;
        }
        failSafeSearchFactory = new FailSafeCandidatesSearchFactory(dependentOnFactories);
    }

    @When("^I create the fail-safe Candidates search service$")
    public void iCreateTheFailSafeCandidatesSearchService()
    {
        candidatesSearch = failSafeSearchFactory.create(configuration, candidateFactory);
    }

    @Given("^A configuration service created from an environment as in file \"([^\"]*)\"$")
    public void aConfigurationServiceCreatedFromAnEnvironmentAsInFile(String configFilePath) throws Throwable
    {
        configuration = new EnvironmentConfiguration();

        String rawConfig = FileSystemHelper.readResourceFile(configFilePath);

        Map<String, String> environmentVars = (HashMap<String, String>) JsonService.loads(rawConfig).get("Variables");

        for (Map.Entry<String, String> envEntry: environmentVars.entrySet())
        {
            configuration.register(envEntry.getKey(), envEntry.getValue());
        }
    }

    @And("^A candidates search service throwing an exception as \"([^\"]*)\"$")
    public void aCandidatesSearchServiceThrowingAnExceptionAs(String exceptionName) throws Throwable
    {
        switch (exceptionName)
        {
            case "SearchException":
                candidatesSearch = new MockCandidatesSearch(
                        new WebClientSearchException("Forced search exception")
                );
                break;
            case "NoSearchResultException":
                candidatesSearch = new MockCandidatesSearch(
                        new EngineNoSearchResultException("Forced no result exception")
                );
                break;
        }
    }

    @And("^A second mock candidates search factory for the candidates search service$")
    public void aSecondMockCandidatesSearchFactoryForTheCandidatesSearchService()
    {
        secondSearchFactory = new MockCandidatesSearchFactory(candidatesSearch);
    }
}
