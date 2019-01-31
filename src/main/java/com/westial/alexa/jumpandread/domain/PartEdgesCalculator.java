package com.westial.alexa.jumpandread.domain;

/**
 * Calculates the new starting index for a part of a width and returns null
 * if the part has to do not move else returns the new starting index.
 */
public abstract class PartEdgesCalculator
{
    protected int partStart;

    public Integer movePosition(int newPosition)
    {
        int newStart = movePart(partStart, newPosition);
        if (newStart == partStart)
        {
            return null;
        }
        else {
            partStart = newStart;
        }
        return partStart;
    }

    protected abstract int movePart(int start, int newPosition);
}
