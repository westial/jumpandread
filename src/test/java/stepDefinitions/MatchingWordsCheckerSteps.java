package stepDefinitions;

import com.westial.alexa.jumpandread.application.service.wordcheck.FileToWordList;
import com.westial.alexa.jumpandread.application.service.wordcheck.MatchingWordsChecker;
import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;
import com.westial.alexa.jumpandread.infrastructure.service.wordcheck.RegexMatchingWordsChecker;
import com.westial.alexa.jumpandread.infrastructure.service.wordcheck.ResourceFileToWordList;
import com.westial.alexa.jumpandread.infrastructure.service.wordcheck.UpperLimitedWordsValidator;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import utils.FileSystemHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchingWordsCheckerSteps
{
    private List<String> sampleWords;
    private MatchingWordsChecker matchingChecker;
    private double result;
    private WordsValidator wordsValidator;
    private boolean isValid;
    private FileToWordList filesToWordList;

    @Given("^A sample words content as in file \"([^\"]*)\"$")
    public void aBadWordsContentAsInFile(String filePath) throws Throwable
    {
        sampleWords = new ArrayList<>(
                Arrays.asList(
                FileSystemHelper.readResourceFile(filePath)
                        .split("\\n")
                )
        );
    }

    @And("^A matching words service$")
    public void aMatchingWordsService()
    {
        matchingChecker = new RegexMatchingWordsChecker(sampleWords);
    }

    @When("^I check the matching words as in file as \"([^\"]*)\" with the service$")
    public void iCheckTheMatchingWordsAsInFileAsWithTheService(String filePath) throws Throwable
    {
        result = matchingChecker.check(
                FileSystemHelper.readResourceFile(filePath)
        );
    }

    @Then("^The service returned a value greater than \"([^\"]*)\"$")
    public void theServiceReturnedAValueGreaterThan(String rawExpected) throws Throwable
    {
        Assert.assertTrue(Float.parseFloat(rawExpected) < result);
    }

    @Then("^The service returned a value lower than \"([^\"]*)\"$")
    public void theServiceReturnedAValueLowerThan(String rawExpected) throws Throwable
    {
        Assert.assertTrue(Float.parseFloat(rawExpected) > result);
    }

    @When("^I validate words as in file as \"([^\"]*)\" with the service$")
    public void iValidateWordsAsInFileAsWithTheService(String filePath) throws Throwable
    {
        isValid = wordsValidator.validate(
                        FileSystemHelper.readResourceFile(filePath)
                );
    }

    @Then("^The service returned a value as \"([^\"]*)\"$")
    public void theServiceReturnedAValueAs(String rawExpected) throws Throwable
    {
        switch (rawExpected)
        {
            case "true":
                Assert.assertTrue(isValid);
                break;
            case "false":
                Assert.assertFalse(isValid);
        }
    }

    @And("^A words validator service for a maximum of \"([^\"]*)\" percent of offensive words$")
    public void aWordsValidatorServiceForAMaximumOfPercentOfOffensiveWords(String rawPercent) throws Throwable
    {
        wordsValidator = new UpperLimitedWordsValidator(matchingChecker, Integer.parseInt(rawPercent));
    }

    @When("^I convert the multiple files into a word list$")
    public void iConvertTheMultipleFilesIntoAWordList() throws IOException
    {
        sampleWords = filesToWordList.convert();
    }

    @Then("^I got a list of \"([^\"]*)\" unique words$")
    public void iGotAListOfUniqueWords(String rawExpected) throws Throwable
    {
        Assert.assertEquals(Integer.parseInt(rawExpected), sampleWords.size());
    }

    @Given("^A word list multiple files to list converter service for files as \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"$")
    public void aWordListThreeFilesToListConverterServiceForFilesAs(String file1, String file2, String file3) throws Throwable
    {
        filesToWordList = new ResourceFileToWordList(file1, file2, file3);
    }

    @Given("^A word list multiple files to list converter service for files as \"([^\"]*)\", \"([^\"]*)\"$")
    public void aWordListTwoFilesToListConverterServiceForFilesAs(String file1, String file2) throws Throwable
    {
        filesToWordList = new ResourceFileToWordList(file1, file2);
    }
}
