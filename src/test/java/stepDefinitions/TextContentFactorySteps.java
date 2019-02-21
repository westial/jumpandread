package stepDefinitions;

import com.westial.alexa.jumpandread.domain.Configuration;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.MockContentGetter;
import com.westial.alexa.jumpandread.infrastructure.MockContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import utils.FileSystemHelper;

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
                defaultParser,
                parserFactory
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
    public void iCreateAByPatternParserAccordingToConfiguration()
    {
        byPatternParser = parserFactory.createByPatternParser(
                configuration.retrieve("PARSER_TYPES_BY_PATTERN"),
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

        }
    }

    @And("^A parser factory with null value for medium prefix filter regex$")
    public void aParserFactoryWithNullValueForMediumPrefixFilterRegex()
    {
        parserFactory = new ParserFactory(null);
    }
}
