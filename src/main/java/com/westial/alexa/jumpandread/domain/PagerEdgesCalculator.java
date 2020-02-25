package com.westial.alexa.jumpandread.domain;

/**
 * Calculates the new starting index for a part of a width and returns null
 * if the part has to do not move else returns the new starting index.
 */
public abstract class PagerEdgesCalculator
{
    private int pageStart;
    protected int totalWidth;

    public void init(int pageStart, int totalWidth)
    {
        this.pageStart = pageStart;
        this.totalWidth = totalWidth;
    }

    public Integer movePosition(int newPosition)
    {
        int newStart = movePage(pageStart, newPosition);
        if (newStart == pageStart)
        {
            return null;
        }
        else {
            pageStart = newStart;
        }
        return pageStart;
    }

    protected abstract int movePage(int start, int newPosition);
}
