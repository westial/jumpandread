package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.PartEdgesCalculator;

public class MarginPartEdgesCalculator extends PartEdgesCalculator
{
    private final int marginWidth;
    private final int partWidth;

    public MarginPartEdgesCalculator(
            int totalWidth,
            int partStart,
            int partWidth,
            int marginWidth
    )
    {
        super(partStart, totalWidth);
        this.marginWidth = marginWidth;
        this.partWidth = partWidth;
    }

    private int partEnd(int start)
    {
        return start + partWidth - 1;
    }

    @Override
    protected int movePart(int start, int newPosition)
    {
        int movedMarginStart = newPosition - (marginWidth / 2) - 1;
        int movedMarginEnd = newPosition + (marginWidth / 2) - 1;

        if (start > movedMarginStart)
        {
            start = movedMarginStart;
        }
        else if (partEnd(start) < movedMarginEnd)
        {
            start += movedMarginEnd - partEnd(start) - 1;
        }

        if (start < 0)
        {
            start = 0;
        }
        else if (partEnd(start) > totalWidth - 1)
        {
            start = totalWidth - partWidth - 1;
        }
        return start;
    }
}
