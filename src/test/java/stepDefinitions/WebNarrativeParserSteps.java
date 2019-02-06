package stepDefinitions;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.infrastructure.MockContentAddress;
import com.westial.alexa.jumpandread.infrastructure.service.LocalContentGetter;
import com.westial.alexa.jumpandread.infrastructure.service.WebNarrativeTextContentParser;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import java.net.URL;
import java.util.LinkedList;

public class WebNarrativeParserSteps
{
    private String content;
    private TextContentParser parser;
    private LinkedList<TextContent> results;
    private String contentFilePath;
    private ContentGetter contentGetter;

    @Given("^A large html narrative content file \"([^\"]*)\"$")
    public void aLargeHtmlNarrativeContentAsInFile(String filePath) throws Throwable
    {
        URL url = Thread.currentThread()
                .getContextClassLoader()
                .getResource(filePath);

        contentFilePath = "file://" + url.getPath();
    }

    @And("^A web narrative parser service implemented from TextContentParser$")
    public void aWebNarrativeParserServiceImplementedFromTextContentParser()
    {
        parser = new WebNarrativeTextContentParser();
    }

    @When("^I parse the narrative file content$")
    public void iParseTheNarrativeFileContent()
    {
        ContentAddress address = new MockContentAddress(contentFilePath);
        results = parser.parse(contentGetter.getContent(address));
    }

    @Then("^The list of results length is as \"([^\"]*)\"$")
    public void theListOfResultsLengthIsAs(String expectedNumber) throws Throwable
    {
        Assert.assertEquals(Integer.parseInt(expectedNumber), results.size());
    }

    @And("^The item indexed as \"([^\"]*)\" has content as \"([^\"]*)\", has label as \"([^\"]*)\"$")
    public void theItemIndexedAsHasContentAsHasLabelAs(String index, String expectedContent, String expectedLabel) throws Throwable
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

    @And("^A local content getter$")
    public void aLocalContentGetter()
    {
        contentGetter = new LocalContentGetter();
    }
}
