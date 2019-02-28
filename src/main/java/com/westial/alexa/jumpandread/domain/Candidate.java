package com.westial.alexa.jumpandread.domain;

import com.westial.alexa.jumpandread.application.exception.IteratingNoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public abstract class Candidate
{
    public static int INDEX_START = 1;
    public static final int FIRST_PARAGRAPH_POSITION = 0;

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
    protected String content;
    protected Calendar updatedAt;

    protected Map<String, Candidate> children;

    private final Integer maxParagraphsNumber;

    private final TextContentProvider contentProvider;
    private final CandidateRepository repository;

    private final Set<String> xtraTagTypes;

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
        this.description = null == description || description.isEmpty() ? title : description;
        this.contentProvider = contentProvider;
        this.repository = repository;
        this.paragraphPosition = paragraphPosition;
        this.maxParagraphsNumber = maxParagraphsNumber;

        xtraTagTypes = getXtraTagNames();

    }

    private static Set<String> getXtraTagNames()
    {

        Set<String> names = new HashSet<>();

        for (XtraTagType xtraTagType : XtraTagType.values())
        {
            names.add(xtraTagType.name());
        }

        return names;
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

    private void updateParagraphPosition(int newPosition)
    {
        paragraphPosition = newPosition;
    }

    public static String buildId(String searchId, int index)
    {
        return String.format("%d:%s", index, searchId);
    }

    public void forward(int number)
    {
        updateParagraphPosition(paragraphPosition + number);
        updateParagraphPosition(adjustNextParagraphIndex(paragraphPosition));
        persist();
    }

    private int adjustNextParagraphIndex(int index)
    {
        if (null != totalContentParagraphsCount
                && totalContentParagraphsCount < index)
        {
            return totalContentParagraphsCount;
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
        updateParagraphPosition(paragraphPosition - number);
        updateParagraphPosition(adjustLastParagraphIndex(paragraphPosition));
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


    private void upgradeParagraphs(
            int position,
            int maxItemsNumber
    ) throws NoParagraphsException
    {
        LinkedHashMap<Integer, Pair<String, TextTag>> providedContents;
        ContentAddress address = new CandidateContentAddress(this);
        int firstCachedParagraphIndex = 0;
        if (null != paragraphs && !paragraphs.isEmpty())
        {
            firstCachedParagraphIndex = paragraphs.entrySet().iterator().next().getKey();
        }
        try
        {
            ContentCounter contentCounter = new CandidateContentCounter(
                    buildCandidateCounterId()
            );

            // Returns null if update is not needed
            // and throws an exception on empty
            providedContents = provideContents(
                    contentProvider,
                    contentCounter,
                    address,
                    firstCachedParagraphIndex,
                    position,
                    maxItemsNumber,
                    totalContentParagraphsCount
            );

            if (null != providedContents)       // update is not needed
            {
                totalContentParagraphsCount = contentCounter.tally();
                paragraphs = new LinkedHashMap<>();

                for (Map.Entry<Integer, Pair<String, TextTag>> entry : providedContents.entrySet())
                {
                    Pair<String, TextTag> content = entry.getValue();
                    paragraphs.put(
                            entry.getKey(),
                            buildParagraph(
                                    content.getKey(),
                                    content.getValue()
                            )
                    );
                }

                persist();
            }

        } catch (EmptyContent emptyContent)
        {
            throw new IteratingNoParagraphsException("Empty Candidate");
        }
    }

    /**
     * Provide contents to fill paragraphs after.
     *
     * @return List of retrieved content objects or return null when the content
     * provision is not needed, for example, when the provided contents would be
     * the same as got before.
     * @throws EmptyContent when there is no content to provide.
     */
    protected abstract LinkedHashMap<Integer, Pair<String, TextTag>> provideContents(
            TextContentProvider provider,
            ContentCounter contentCounter,
            ContentAddress address,
            Integer lastPosition,
            int firstCachedId,
            int contentsNumber,
            Integer totalNumber
    ) throws EmptyContent;

    protected abstract Paragraph buildParagraph(String label, TextTag text);

    public String dump(int number, String pauseToken) throws NoParagraphsException
    {
        Paragraph paragraph;
        StringBuilder text = new StringBuilder();

        if (null == totalContentParagraphsCount)
        {
            contentProvider.clean();
            upgradeParagraphs(paragraphPosition, maxParagraphsNumber);
        }

        int ending = adjustNextParagraphIndex(paragraphPosition + number);

        if (paragraphPosition >= ending)
        {
            throw new IteratingNoParagraphsException("Finished Candidate");
        }

        for (int index = paragraphPosition; index < ending; index++)
        {
            if (!paragraphs.containsKey(index))
            {
                upgradeParagraphs(paragraphPosition, maxParagraphsNumber);
            }
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

    /**
     * Check if a paragraph is an xtra tag type paragraph and handle according
     * the tag type behaviour if applies.
     *
     * @param paragraph Paragraph to check.
     * @return return true if it is an xtra tag type paragraph and return false
     * if it is not.
     */
    private boolean handleXtraTagParagraph(Paragraph paragraph)
    {
        if (xtraTagTypes.contains(paragraph.getTag()))
        {
            switch (XtraTagType.valueOf(paragraph.getTag()))
            {
                case X_CANDIDATE:
                    addChild(
                            paragraph.getContent().get("src"),
                            paragraph.getContent().getText(),
                            paragraph.getContent().get("description")
                    );
                    break;

                default:
                    return false;
            }
            return true;
        }
        return false;
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

    public Map<String, Candidate> getChildren()
    {
        return children;
    }

    private void addChild(String url, String title, String description)
    {
        if (null == children)
        {
            children = new HashMap<>();
        }
        int nextIndex = repository.countBySearch(searchId);

        Candidate child = create(
                Candidate.buildId(searchId, nextIndex),
                nextIndex,
                userId,
                sessionId,
                searchId,
                title,
                url,
                description,
                contentProvider,
                repository,
                Candidate.FIRST_PARAGRAPH_POSITION,
                maxParagraphsNumber
        );

        children.put(url, child);
    }

    protected abstract Candidate create(
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
            int maxParagraphsNumber
    );
}
