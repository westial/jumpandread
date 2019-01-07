package com.westial.alexa.jumpandread.domain;

import java.util.Calendar;
import java.util.List;

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
    protected List<Paragraph> paragraphs;
    protected Integer paragraphPosition;
    protected String content;
    protected Calendar updatedAt;

    private final CandidateGetter getter;
    private final CandidateParser parser;
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
            CandidateGetter getter,
            CandidateParser parser,
            CandidateRepository repository,
            Integer paragraphPosition
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
        this.getter = getter;
        this.parser = parser;
        this.repository = repository;
        this.paragraphPosition = paragraphPosition;
    }

    public Candidate(
            String id,
            Integer index,
            String userId,
            String sessionId,
            String searchId,
            CandidateGetter getter,
            CandidateParser parser,
            CandidateRepository repository
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
                getter,
                parser,
                repository,
                null
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
        if (paragraphs.size() < index)
        {
            return paragraphs.size();
        }
        return index;
    }

    private int adjustLastParagraphIndex(int index)
    {
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

    public String dump(int number, String pauseToken)
    {
        Paragraph paragraph;
        StringBuilder text = new StringBuilder();

        int ending = adjustNextParagraphIndex(paragraphPosition + number);

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

    public boolean isFinished()
    {
        return paragraphPosition >= paragraphs.size();
    }

    public void persist()
    {
        updatedAt = Calendar.getInstance();
        repository.update(this);
    }

    public void parse()
    {
        paragraphs = parser.parse(content);
    }

    public void provideContent()
    {
        content = getter.getContent(this);
    }

    public List<Paragraph> getParagraphs()
    {
        return paragraphs;
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

    public String getId()
    {
        return id;
    }

    public Calendar getUpdatedAt()
    {
        return updatedAt;
    }
}
