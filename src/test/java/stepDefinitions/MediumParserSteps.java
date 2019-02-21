package stepDefinitions;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.infrastructure.MockContentAddress;
import com.westial.alexa.jumpandread.infrastructure.service.LocalContentGetter;
import com.westial.alexa.jumpandread.infrastructure.service.MediumTextContentParser;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import java.net.URL;
import java.util.LinkedList;

public class MediumParserSteps
{
    private String contentFilePath;
    private ContentGetter contentGetter;
    private TextContentParser parser;
    private LinkedList<TextContent> results;
    private String exceptionName;

    @Given("^A medium article content as in file \"([^\"]*)\"$")
    public void iGotAnArticleContentAsInFileWithABreakingPurposeOnlyPrefixAs(String filePath) throws Throwable
    {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(filePath);

        contentFilePath = "file://" + url.getPath();
    }

    @And("^A local content getter for medium parser test$")
    public void aLocalContentGetterForMediumParserTest()
    {
        contentGetter = new LocalContentGetter();
    }

    @And("^A medium parser service implemented from TextContentParser with the regex pattern as \"([^\"]*)\"$")
    public void aMediumParserServiceImplementedFromTextContentParser(String filterRegex)
    {
        parser = new MediumTextContentParser(filterRegex);
    }

    @When("^I parse the medium file content$")
    public void iParseTheMediumFileContentWithTheRegexPatternAs() throws Throwable
    {
        ContentAddress address = new MockContentAddress(contentFilePath);
        try
        {
            results = parser.parse(contentGetter.getContent(address));
        }
        catch (Exception e)
        {
            exceptionName = e.getClass().getName();
        }
    }

    @Then("^The list of medium parsing results length is as \"([^\"]*)\"$")
    public void theListOfMediumParsingResultsLengthIsAs(String expectedNumber) throws Throwable
    {
        Assert.assertEquals(Integer.parseInt(expectedNumber), results.size());
    }

    @And("^The medium parsed item indexed as \"([^\"]*)\" has content as \"([^\"]*)\", has label as \"([^\"]*)\"$")
    public void theMediumParsedItemIndexedAsHasContentAsHasLabelAs(String index, String expectedContent, String expectedLabel) throws Throwable
    {
        Assert.assertEquals(
                expectedContent,
                results.get(Integer.parseInt(index)).getContent()
        );

        Assert.assertEquals(
                expectedLabel,
                results.get(Integer.parseInt(index)).getLabel()
        );
    }

    @Then("^The parser threw an exception as \"([^\"]*)\"$")
    public void theParserThrewAnExceptionAs(String expectedExceptionName) throws Throwable
    {
        Assert.assertEquals(expectedExceptionName, exceptionName);
    }
}
