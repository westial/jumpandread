package com.westial.alexa.jumpandread.domain;

import com.westial.alexa.jumpandread.application.exception.IteratingNoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.domain.content.ContentCounter;
import com.westial.alexa.jumpandread.domain.content.EmptyContent;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public abstract class Candidate
{
    public static int INDEX_START = 1;

    protected String id;
    protected Integer index;
    protected String userId;
    protected String sessionId;
    protected String searchId;
    protected String title;

    protected String url;
    protected String description;
    protected LinkedHashMap<Integer, Paragraph> paragraphs;

    protected Integer totalContentParagraphsCount;
    protected Integer paragraphPosition;
    private final Integer maxParagraphsNumber;
    protected String content;
    protected Calendar updatedAt;

    private final TextContentProvider contentProvider;
    private final CandidateRepository repository;

    public Candidate(
            String id,
            Integer index,
            String userId,
            String sessionId,
            String searchId,
            String title,
            String url,
            String description,
            TextContentProvider contentProvider,
            CandidateRepository repository,
            Integer paragraphPosition,
            Integer maxParagraphsNumber
    )
    {
        this.id = id;
        this.index = index;
        this.userId = userId;
        this.sessionId = sessionId;
        this.searchId = searchId;
        this.title = title;
        this.url = url;
        this.description = description;
        this.contentProvider = contentProvider;
        this.repository = repository;
        this.paragraphPosition = paragraphPosition;
        this.maxParagraphsNumber = maxParagraphsNumber;
    }

    public Candidate(
            String id,
            Integer index,
            String userId,
            String sessionId,
            String searchId,
            TextContentProvider contentProvider,
            CandidateRepository repository,
            Integer maxParagraphsNumber
    )
    {
        this(
                id,
                index,
                userId,
                sessionId,
                searchId,
                null,
                null,
                null,
                contentProvider,
                repository,
                null,
                maxParagraphsNumber
        );
        Candidate candidate = repository.get(
                searchId,
                index
        );
        if (null == candidate)
        {
            throw new RetrievingNoCandidateException(
                    String.format(
                            "No Candidate for index %d",
                            index
                    )
            );
        }
        this.title = candidate.getTitle();
        this.url = candidate.getUrl();
        this.description = candidate.getDescription();
        this.paragraphPosition = candidate.getParagraphPosition();
        this.paragraphs = candidate.getParagraphs();
        this.totalContentParagraphsCount =
                candidate.getTotalContentParagraphsCount();
    }

    public static String buildId(String searchId, int index)
    {
        return String.format("%d:%s", index, searchId);
    }

    public void forward(int number)
    {
        paragraphPosition += number;
        paragraphPosition = adjustNextParagraphIndex(paragraphPosition);
        persist();
    }

    private int adjustNextParagraphIndex(int index)
    {
        parse();
        if (totalContentParagraphsCount < index)
        {
            return totalContentParagraphsCount;
        }
        return index;
    }

    private int adjustLastParagraphIndex(int index)
    {
        parse();
        if (0 > index)
        {
            return 0;
        }
        return index;
    }

    public void rewind(int number)
    {
        paragraphPosition -= number;
        paragraphPosition = adjustLastParagraphIndex(paragraphPosition);
        persist();
    }

    public void reset()
    {
        paragraphPosition = 0;
        persist();
    }

    private String buildCandidateCounterId()
    {
        return String.format("%s:%s", getSearchId(), getUrl());
    }

    private void initParagraphs()
    {
        contentProvider.initCache();
        paragraphs = new LinkedHashMap<>();
    }


    private void upgradeParagraphs(
            int position,
            int maxItemsNumber
    )
    {
        LinkedList<Pair<String, String>> providedContents;
        ContentAddress address = new CandidateContentAddress(this);
        try
        {
            ContentCounter contentCounter = new CandidateContentCounter(
                buildCandidateCounterId()
            );
            providedContents = contentProvider.provide(
                    contentCounter,
                    address,
                    position,
                    maxItemsNumber
            );
            totalContentParagraphsCount = contentCounter.tally();

        } catch (EmptyContent emptyContent)
        {
            throw new IteratingNoParagraphsException("Empty Candidate");
        }
        while (!providedContents.isEmpty())
        {
            Pair<String, String> content = providedContents.removeFirst();
            paragraphs.put(position, buildParagraph(position, content));
            position ++;
        }

        persist();
    }

    public void parse()
    {
        if (null == totalContentParagraphsCount)
        {
            initParagraphs();
            upgradeParagraphs(paragraphPosition, maxParagraphsNumber);

        } else if (paragraphPosition > maxParagraphsNumber / 2)
        {
            upgradeParagraphs(paragraphPosition, maxParagraphsNumber);
        }
    }

    protected abstract Paragraph buildParagraph(int index, Pair<String, String> content);

    public String dump(int number, String pauseToken)
    {
        Paragraph paragraph;
        StringBuilder text = new StringBuilder();

        int ending = adjustNextParagraphIndex(paragraphPosition + number);

        if (paragraphPosition >= ending)
        {
            throw new IteratingNoParagraphsException("Finished Candidate");
        }

        for (int index = paragraphPosition; index < ending; index ++)
        {
            paragraph = paragraphs.get(index);
            text.append(
                    String.format(
                            "%s%s",
                            paragraph.getContent(),
                            pauseToken
                    )
            );
        }
        return text.toString();
    }

    public void persist()
    {
        updatedAt = Calendar.getInstance();
        repository.update(this);
    }

    public Integer getParagraphPosition()
    {
        return paragraphPosition;
    }

    public String getTitle()
    {
        return title;
    }

    public String buildListing(String separatorToken)
    {
        return String.format(
                "%d. %s.%s",
                index,
                title,
                separatorToken
        );
    }

    public String getUrl()
    {
        return url;
    }

    public String getDescription()
    {
        return description;
    }

    public Integer getIndex()
    {
        return index;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public String getSearchId()
    {
        return searchId;
    }

    public LinkedHashMap<Integer, Paragraph> getParagraphs()
    {
        return paragraphs;
    }

    public String getId()
    {
        return id;
    }

    public Calendar getUpdatedAt()
    {
        return updatedAt;
    }

    public Integer getTotalContentParagraphsCount()
    {
        return totalContentParagraphsCount;
    }
}
