package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.PagerEdgesCalculator;

public class MarginPagerEdgesCalculator extends PagerEdgesCalculator
{
    private final int marginWidth;
    private final int pageWidth;

    public MarginPagerEdgesCalculator(
            int pageWidth,
            int marginWidth
    )
    {
        if (marginWidth >= pageWidth)
        {
            throw new RuntimeException(
                    String.format(
                            "Margin width %d is larger or equal than part width %d",
                            marginWidth,
                            pageWidth
                    )
            );
        }
        this.marginWidth = marginWidth;
        this.pageWidth = pageWidth;
    }

    private int pageEnd(int start)
    {
        return start + pageWidth - 1;
    }

    @Override
    protected int movePage(int start, int newPosition)
    {
        int movedMarginStart = newPosition - (marginWidth / 2) - 1;
        int movedMarginEnd = newPosition + (marginWidth / 2) - 1;

        if (start > movedMarginStart)
        {
            start = newPosition + (marginWidth / 2) - pageWidth;
        }
        else if (pageEnd(start) < movedMarginEnd)
        {
            start = newPosition - (marginWidth / 2);
        }

        if (start < 0)
        {
            start = 0;
        }
        else if (pageEnd(start) >= totalWidth - 1)
        {
            start = totalWidth - pageWidth - 1;
        }
        return start;
    }
}
