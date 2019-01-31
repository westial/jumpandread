package stepDefinitions;

import com.westial.alexa.jumpandread.domain.PartEdgesCalculator;
import com.westial.alexa.jumpandread.infrastructure.service.MarginPartEdgesCalculator;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

public class EdgesCalculator
{
    private int totalWidth;
    private int partWidth;
    private Integer partStart;
    private int marginWidth;
    private PartEdgesCalculator edgesCalculator;
    private final static String NULL_TOKEN = "[null]";

    @Given("^A total width as \"([^\"]*)\"$")
    public void aTotalWidthAsStartingAt(String width) throws Throwable
    {
        totalWidth = Integer.parseInt(width);
    }

    @Given("^A part range width as \"([^\"]*)\"$")
    public void aCachedRangeWidthAs(String width) throws Throwable
    {
        partWidth = Integer.parseInt(width);
    }

    @Given("^The part range starts at index \"([^\"]*)\"$")
    public void theCachedRangeStartsAtIndex(String index) throws Throwable
    {
        partStart = Integer.parseInt(index);
    }

    @Given("^A moving margin width as \"([^\"]*)\"$")
    public void aMovingMarginWidthAs(String width) throws Throwable
    {
        marginWidth = Integer.parseInt(width);
    }

    @When("^I move the current position to \"([^\"]*)\"$")
    public void iMoveTheCurrentPositionTo(String newIndex) throws Throwable
    {
        partStart = edgesCalculator.movePosition(Integer.parseInt(newIndex));
    }

    @Given("^An edges calculator service$")
    public void anEdgesCalculatorService()
    {
        edgesCalculator = new MarginPartEdgesCalculator(
                totalWidth,
                partStart,
                partWidth,
                marginWidth
        );
    }

    @Then("^The part range has to start at \"([^\"]*)\" because \"([^\"]*)\"$")
    public void theCachedRangeHasToStartAt(String expectedCachedStart, String comment) throws Throwable
    {
        if (expectedCachedStart.equals(NULL_TOKEN))
        {
            Assert.assertNull(partStart);
        }
        else
        {
            Assert.assertEquals(
                    0,
                    partStart.compareTo(Integer.parseInt(expectedCachedStart))
            );
        }
    }
}
