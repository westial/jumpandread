package com.westial.alexa.jumpandread.domain;

public interface StateFactory
{
    /**
     * State factory when you have a new searchId and you are creating a new
     * State with new intent and new searchId.
     */
    State create(
            String userId,
            String sessionId,
            String intent,
            String searchId,
            String searchTerms);

    /**
     * State factory when you want to retrieve current searchId and create a
     * State with a new intent.
     */
    State create(
            String userId,
            String sessionId);
}
