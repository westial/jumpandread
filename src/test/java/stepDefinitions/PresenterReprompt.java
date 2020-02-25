package stepDefinitions;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.infrastructure.service.AlexaPresenter;
import com.westial.alexa.jumpandread.infrastructure.service.MockTranslator;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

public class PresenterReprompt
{
    private Presenter presenter;
    private String result;

    @Given("^An Alexa Presenter service for reprompt$")
    public void anAlexaPresenterServiceForReprompt()
    {
        presenter = new AlexaPresenter(new MockTranslator());
    }

    @And("^I set speech content as \"([^\"]*)\" into Presenter$")
    public void iSetSpeechContentAsIntoPresenter(String content) throws Throwable
    {
        presenter.addText(content);
    }

    @And("^I set repeat content as \"([^\"]*)\" into Presenter$")
    public void iSetRepeatContentAsIntoPresenter(String content) throws Throwable
    {
        presenter.addRepeat(content);
    }

    @When("^I ask for repeating content to Presenter$")
    public void iAskForRepeatingContentToPresenter()
    {
        result = presenter.repeat();
    }

    @Then("^Presenter returned \"([^\"]*)\"$")
    public void presenterReturned(String expected) throws Throwable
    {
        Assert.assertEquals(expected, result);
    }
}
