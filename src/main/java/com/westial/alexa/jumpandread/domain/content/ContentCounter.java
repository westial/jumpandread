package com.westial.alexa.jumpandread.domain.content;

public abstract class ContentCounter implements Comparable<ContentCounter>
{

    private final String name;     // counter name
    private int count = 0;         // current value

    /**
     * Initializes a new counter starting at 0, with the given id.
     *
     * @param id the name of the counter
     */
    public ContentCounter(String id)
    {
        name = id;
    }

    /**
     * Increments the counter by 1.
     */
    public void increment()
    {
        count++;
    }

    /**
     * Increments the counter by the given value.
     */
    public void sum(int value)
    {
        count += value;
    }

    /**
     * Returns the current value of this counter.
     *
     * @return the current value of this counter
     */
    public int tally()
    {
        return count;
    }

    /**
     * Returns a string representation of this counter.
     *
     * @return a string representation of this counter
     */
    public String toString()
    {
        return count + " " + name;
    }

    /**
     * Compares this counter to the specified counter.
     *
     * @param other the other counter
     * @return {@code 0} if the value of this counter equals
     * the value of that counter; a negative integer if
     * the value of this counter is less than the value of
     * that counter; and a positive integer if the value
     * of this counter is greater than the value of that
     * counter
     */
    @Override
    public int compareTo(ContentCounter other)
    {
        return Integer.compare(this.count, other.count);
    }
}
