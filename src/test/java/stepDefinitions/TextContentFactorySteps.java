package stepDefinitions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.domain.Configuration;
import com.westial.alexa.jumpandread.domain.content.AddressModifier;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.MockContentGetter;
import com.westial.alexa.jumpandread.infrastructure.MockContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import com.westial.alexa.jumpandread.infrastructure.service.content.*;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.ByPatternTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.MediumTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.WebNarrativeTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.WebSearchTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.structure.SimpleContentAddress;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import utils.FileSystemHelper;

import java.io.IOException;
import java.util.Map;

public class TextContentFactorySteps
{
    private Configuration configuration;
    private TextContentParser defaultParser;
    private ContentGetter contentGetter;
    private ByConfigurationTextContentProviderFactory providerFactory;
    private TextContentProvider provider;
    private String exceptionName;
    private ByPatternTextContentParser byPatternParser;
    private ParserFactory parserFactory;
    private ContentGetterFactory getterFactory;
    private ContentGetter byPatternContentGetter;
    private String gotContent;
    private ContentGetter defaultContentGetter;
    private AddressModifier defaultAddressModifier;

    @Given("^A parsers by pattern configuration as follows$")
    public void aParsersByPatternConfigurationAsFollows(String parsersByPattern)
    {
        configuration.register(
                "PARSER_TYPES_BY_PATTERN",
                parsersByPattern
        );
    }

    @And("^A parsers by pattern configuration as in file \"([^\"]*)\"$")
    public void aParsersByPatternConfigurationAsInFile(String filePath) throws Throwable
    {
        configuration.register(
                "PARSER_TYPES_BY_PATTERN",
                FileSystemHelper.readResourceFile(filePath)
        );
    }

    @And("^An environment configuration service$")
    public void aMockConfigurationService()
    {
        configuration = new EnvironmentConfiguration();
    }

    @And("^A mock web search parser as default$")
    public void aMockWebSearchParserAsDefault()
    {
        defaultParser = new MockContentParser();
    }

    @And("^An empty mock content getter$")
    public void aMockContentGetter()
    {
        contentGetter = new MockContentGetter(null);
    }

    @And("^A parser type configuration value as \"([^\"]*)\"$")
    public void aParserByTypeConfigurationValueAs(String parserType) throws Throwable
    {
        configuration.register("PARSER_TYPE", parserType);
    }

    @And("^A mock text content providers factory by configuration with the test only method to get the parser$")
    public void aMockTextContentProvidersFactoryByConfigurationWithTheTestOnlyMethodToGetTheParser()
    {
        providerFactory = new ByConfigurationTextContentProviderFactory(
                contentGetter,
                getterFactory,
                defaultParser,
                parserFactory,
                defaultAddressModifier
        );
    }

    @When("^I create the text content provider$")
    public void iCreateTheTextContentProvider()
    {
        try
        {
            provider = providerFactory.create(configuration);
        }
        catch (Exception e)
        {
            exceptionName = e.getClass().getName();
        }
    }

    @Then("^The providers factory parser class name is as \"([^\"]*)\"$")
    public void theProvidersFactoryParserClassNameIsAs(String expectedClassName) throws Throwable
    {
        Assert.assertEquals(expectedClassName, provider.getClass().getName());
    }

    @Then("^The factory threw an exception with name as \"([^\"]*)\"$")
    public void theFactoryThrewAnExceptionWithNameAs(String expected) throws Throwable
    {
        Assert.assertEquals(expected, exceptionName);
    }

    @And("^A by pattern content parser mock extension for checking parsing test purpose only$")
    public void aByPatternContentParserMockExtensionForCheckingParsingTestPurposeOnly()
    {
        byPatternParser = new ByPatternTextContentParser(
                defaultParser
        );
    }

    @When("^I create a by pattern parser according to configuration$")
    public void iCreateAByPatternParserAccordingToConfiguration() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> patternParserConfig = mapper.readValue(
                configuration.retrieve("PARSER_TYPES_BY_PATTERN"),
                new TypeReference<Map<String, String>>()
                {
                }
        );
        byPatternParser = parserFactory.buildByPatternParser(
                patternParserConfig,
                defaultParser
        );
    }

    @And("^I configure the by pattern parser for \"([^\"]*)\"$")
    public void iConfigureTheByPatternParserFor(String contentUrl) throws Throwable
    {
        byPatternParser.configure(contentUrl);
    }

    @Then("^The configured parser into by pattern parser is an instance of \"([^\"]*)\"$")
    public void theConfiguredParserIntoByPatternParserIsAnInstanceOf(String expected) throws Throwable
    {
        TextContentParser parser = (TextContentParser) FieldUtils.readField(
                byPatternParser, "parser",
                true
        );

        switch (expected)
        {
            case "WebSearchTextContentParser":
                Assert.assertTrue(parser instanceof WebSearchTextContentParser);
                break;

            case "WebNarrativeTextContentParser":
                Assert.assertTrue(parser instanceof WebNarrativeTextContentParser);
                break;

            case "MockContentParser":
                Assert.assertTrue(parser instanceof MockContentParser);
                break;

            case "MediumTextContentParser":
                Assert.assertTrue(parser instanceof MediumTextContentParser);
                break;
        }
    }

    @And("^A parser factory with empty value for medium prefix filter regex$")
    public void aParserFactoryWithNullValueForMediumPrefixFilterRegex()
    {
        parserFactory = new ParserFactory("", null);
    }

    @And("^A unirest content getter as default$")
    public void aUnirestContentGetterAsDefault()
    {
        contentGetter = new UnirestContentGetter("Gherkin-User-Agent");
    }

    @And("^A content getter factory$")
    public void aContentGetterFactory()
    {
        getterFactory = new ContentGetterFactory();
    }

    @When("^I create a by pattern content getter according to configuration$")
    public void iCreateAByPatternContentGetterAccordingToConfiguration() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> patternParserConfig = mapper.readValue(
                configuration.retrieve("PARSER_TYPES_BY_PATTERN"),
                new TypeReference<Map<String, String>>()
                {
                }
        );

        byPatternContentGetter = getterFactory.buildByPatternContentGetter(
                patternParserConfig,
                defaultContentGetter,
                defaultAddressModifier
        );
    }

    @And("^I get content from \"([^\"]*)\" by content getter$")
    public void iGetContentFromByContentGetter(String contentUrl) throws Throwable
    {
        gotContent = byPatternContentGetter.getContent(
                new SimpleContentAddress(contentUrl)
        );
    }

    @And("^An url returning mock content getter a default$")
    public void anUrlReturningMockContentGetterADefault()
    {
        defaultContentGetter = new MockReturnUrlContentGetter();
    }

    @Then("^The returned content is as \"([^\"]*)\"$")
    public void theReturnedContentIsAs(String expectedUrl) throws Throwable
    {
        Assert.assertEquals(expectedUrl, gotContent);
    }

    @And("^A default address modifier$")
    public void aDefaultAddressModifier()
    {
        defaultAddressModifier = new DefaultAddressModifier();
    }
}
