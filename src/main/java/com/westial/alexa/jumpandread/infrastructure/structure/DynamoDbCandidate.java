package com.westial.alexa.jumpandread.infrastructure.structure;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import com.westial.alexa.jumpandread.domain.PagerEdgesCalculator;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.domain.content.*;
import com.westial.alexa.jumpandread.infrastructure.service.DynamoDbParagraphListConverter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@DynamoDBDocument
public class DynamoDbCandidate extends Candidate
{
    private final PagerEdgesCalculator partCalculator;

    public DynamoDbCandidate()
    {
        super(null, null, null, null, null, null, null, null, null, null, null, null);
        partCalculator = null;
    }

    public DynamoDbCandidate(
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
            int maxParagraphsNumber,
            PagerEdgesCalculator partCalculator
    )
    {
        super(id, index, userId, sessionId, searchId, title, url, description, contentProvider, repository, paragraphPosition, maxParagraphsNumber);
        this.partCalculator = partCalculator;
    }

    @Override
    @JsonIgnore
    protected LinkedHashMap<Integer, Pair<String, TextTag>> provideContents(
            TextContentProvider provider,
            ContentCounter contentCounter,
            ContentAddress address,
            Integer lastPosition,
            int newPosition,
            int contentsNumber,
            Integer totalNumber
    ) throws EmptyContent
    {
        if (null != totalNumber)
        {
            partCalculator.init(
                    null == lastPosition ? 0 : lastPosition,
                    totalNumber
            );
            Integer optimalPosition = partCalculator.movePosition(newPosition);
            if (null != lastPosition && null == optimalPosition)
            {
                return null;
            } else if (null != optimalPosition)
            {
                newPosition = optimalPosition;
            }
        }

        LinkedList<Pair<String, TextTag>> contents = provider.provide(
                contentCounter,
                address,
                newPosition,
                contentsNumber
        );

        LinkedHashMap<Integer, Pair<String, TextTag>> results = new LinkedHashMap<>();

        while (!contents.isEmpty())
        {
            Pair<String, TextTag> content = contents.removeFirst();
            results.put(newPosition, content);
            newPosition ++;
        }
        return results;
    }

    @Override
    protected Paragraph buildParagraph(String label, TextTag tag)
    {
        return new DynamoDbParagraph(label, tag);
    }

    public DynamoDbCandidate(
            String id,
            int index,
            String userId,
            String sessionId,
            String searchId,
            TextContentProvider contentProvider,
            CandidateRepository repository,
            int maxParagraphsNumber,
            PagerEdgesCalculator partCalculator)
    {
        super(id, index, userId, sessionId, searchId, contentProvider, repository, maxParagraphsNumber);
        this.partCalculator = partCalculator;
    }

    @DynamoDBTypeConverted(converter = DynamoDbParagraphListConverter.class)
    @DynamoDBAttribute(attributeName = "paragraphs")
    public LinkedHashMap<Integer, Paragraph> getParagraphs()
    {
        return super.getParagraphs();
    }

    public void setParagraphs(LinkedHashMap<Integer, Paragraph> paragraphs)
    {
        this.paragraphs = paragraphs;
    }

    @Override
    @DynamoDBAttribute(attributeName = "paragraph_position")
    public Integer getParagraphPosition()
    {
        return super.getParagraphPosition();
    }

    public void setParagraphPosition(Integer paragraphPosition)
    {
        super.paragraphPosition = paragraphPosition;
    }

    @Override
    @DynamoDBAttribute(attributeName = "title")
    public String getTitle()
    {
        return super.getTitle();
    }


    public void setTitle(String title)
    {
        this.title = title;
    }

    @Override
    @DynamoDBAttribute(attributeName = "url")
    public String getUrl()
    {
        return super.getUrl();
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    @DynamoDBAttribute(attributeName = "description")
    public String getDescription()
    {
        return super.getDescription();
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    @DynamoDBIndexRangeKey(attributeName = "index", globalSecondaryIndexName = "search_id-index-index")
    public Integer getIndex()
    {
        return super.getIndex();
    }

    public void setIndex(Integer index)
    {
        this.index = index;
    }

    @Override
    @DynamoDBAttribute(attributeName = "user_id")
    public String getUserId()
    {
        return super.getUserId();
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @Override
    @DynamoDBAttribute(attributeName = "session_id")
    public String getSessionId()
    {
        return super.getSessionId();
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    @Override
    @DynamoDBIndexHashKey(attributeName = "search_id", globalSecondaryIndexName = "search_id-index-index")
    @DynamoDBIndexRangeKey(attributeName = "search_id", globalSecondaryIndexName = "search_id-index-index")
    public String getSearchId()
    {
        return super.getSearchId();
    }

    public void setSearchId(String searchId)
    {
        this.searchId = searchId;
    }

    @Override
    @DynamoDBHashKey(attributeName = "id")
    public String getId()
    {
        return super.getId();
    }

    public void setId(String id)
    {
        super.id = id;
    }

    @Override
    @DynamoDBAttribute(attributeName = "totalContentParagraphsCount")
    public Integer getTotalContentParagraphsCount()
    {
        return super.getTotalContentParagraphsCount();
    }

    public void setTotalContentParagraphsCount(Integer totalContentParagraphsCount)
    {
        this.totalContentParagraphsCount = totalContentParagraphsCount;
    }

    @Override
    @DynamoDBAttribute(attributeName = "updatedAt")
    public Calendar getUpdatedAt()
    {
        return super.getUpdatedAt();
    }

    public void setUpdatedAt(Calendar updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    @Override
    @DynamoDBAttribute(attributeName = "children")
    public Map<String, Candidate> getChildren()
    {
        return super.getChildren();
    }

    public void setChildren(Map<String, Candidate> children)
    {
        this.children = children;
    }

    @Override
    protected Candidate create(
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
            int maxParagraphsNumber)
    {
        return new DynamoDbCandidate(
                id,
                index,
                userId,
                sessionId,
                searchId,
                title,
                url,
                description,
                contentProvider,
                repository,
                paragraphPosition,
                maxParagraphsNumber,
                partCalculator
        );
    }
}
