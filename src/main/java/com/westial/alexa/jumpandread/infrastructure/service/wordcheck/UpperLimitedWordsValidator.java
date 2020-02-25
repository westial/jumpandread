package com.westial.alexa.jumpandread.infrastructure.service.wordcheck;

import com.westial.alexa.jumpandread.application.service.wordcheck.MatchingWordsChecker;
import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;

public class UpperLimitedWordsValidator implements WordsValidator
{
    private final MatchingWordsChecker matchingChecker;
    private final double maxAllowed;

    public UpperLimitedWordsValidator(MatchingWordsChecker matchingChecker, double maxAllowed)
    {
        this.matchingChecker = matchingChecker;
        this.maxAllowed = maxAllowed;
    }

    @Override
    public boolean validate(String content)
    {
        double checked = matchingChecker.check(content);
        boolean isValid = checked <= maxAllowed;
        if (!isValid)
        {
            System.out.printf(
                    "DEBUG: com.westial.alexa.jumpandread.infrastructure.service.wordcheck.UpperLimitedWordsValidator.validate result: words check (%f) <= allowed (%f)\n",
                    checked,
                    maxAllowed
            );
        }
        return isValid;
    }
}
