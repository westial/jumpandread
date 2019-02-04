package stepDefinitions;

import com.westial.alexa.jumpandread.application.command.SearchCandidatesCommand;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.*;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import java.net.URL;
import java.util.List;

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
    private CandidatesSearch searchEngine;
    private List<Candidate> candidates;
    private HeadersProvider headersProvider;
    private DuckDuckGoLocaleProvider duckLocaleProvider;
    private TextContentProvider contentProvider;
    private Exception exception;

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
        pageParser = new JsoupDuckDuckGoResultParser();
    }

    @Given("^A DuckDuckGo candidates search service for url as \"([^\"]*)\", iso 4-letters locale as \"([^\"]*)\"$")
    public void aDuckDuckGoCandidatesSearchService(String duckUrl, String iso4Locale)
    {
        searchEngine = new DuckDuckGoCandidatesSearch(
                STARTING_CANDIDATE_INDEX,
                pageClient,
                pageParser,
                duckUrl,
                headersProvider,
                duckLocaleProvider,
                iso4Locale,
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
            candidates = searchEngine.find(
                    new User(userId, sessionId),
                    searchId,
                    terms
            );
        } catch (SearchException | NoSearchResultsException e)
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
                candidatesSearch
        );
    }

    @When("^I execute the step command with the terms \"([^\"]*)\"$")
    public void iExecuteTheCommandWithTheTerms(String searchTerms)
    {
        try
        {
            foundCandidates = searchCandidates.execute(state, searchTerms);
        } catch (SearchException | NoSearchResultsException e)
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
        state = stateFactory.create(userId, sessionId, intent, "no matters");
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
        Assert.assertTrue(exception instanceof NoSearchResultsException);
    }
}
