package stepDefinitions;

import com.westial.alexa.jumpandread.infrastructure.service.RegexTextCleaner;
import com.westial.alexa.jumpandread.infrastructure.service.TextCleaner;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

public class TextCleanerSteps
{
    private TextCleaner cleaner;
    private String result;

    @Given("^A text cleaner service setting up with the extraction regex as \"([^\"]*)\"$")
    public void aTextCleanerServiceSettingUpWithTheExtractionRegexAs(String regex) throws Throwable
    {
        cleaner = new RegexTextCleaner(regex);
    }

    @When("^I extract the wanted content from \"([^\"]*)\"$")
    public void iExtractTheWantedContentFrom(String textContent) throws Throwable
    {
        result = cleaner.extract(textContent);
    }

    @Then("^I got the content as \"([^\"]*)\"$")
    public void iGotTheContentAs(String expected) throws Throwable
    {
        Assert.assertEquals(expected, result);
    }
}
