package junit;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.google.api.client.util.Lists;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.*;
import com.westial.alexa.jumpandread.infrastructure.MockQueueContentGetter;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import com.westial.alexa.jumpandread.infrastructure.service.content.LinkHtmlTag;
import com.westial.alexa.jumpandread.infrastructure.service.content.RemoteTextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.structure.HtmlTextContent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class DynamoDbCandidateRepositoryTest
{
    private static final String SAMPLE_SRC = "http://westial.com";
    private TextContentParser contentParser;
    private CandidateRepository candidateRepository;
    private ContentGetter contentGetter;
    private TextContentProvider contentProvider;
    private CandidateFactory candidateFactory;
    private String searchId;
    private int candidateIndex;

    private AmazonDynamoDB createDynamoDbClient()
    {
        AmazonDynamoDBClientBuilder builder =
                AmazonDynamoDBClientBuilder.standard()
                        .withCredentials(
                                new ProfileCredentialsProvider(
                                        "westial"
                                )
                        );
        return builder.build();
    }

    @Before
    public void setUp()
    {
        candidateRepository = new DynamoDbCandidateRepository(
                "jnr_candidate",
                createDynamoDbClient()
        );
        LinkHtmlTag link = new LinkHtmlTag("This is my url");
        link.setSrc(SAMPLE_SRC);
        TextContent forceXtraContent = new HtmlTextContent(
                XtraTagType.X_CANDIDATE.name(),
                link
        );
        contentParser = new WithAppendedMockWebSearchTextContentParser(forceXtraContent);
        contentGetter = buildMockContentGetter();
        contentProvider = new RemoteTextContentProvider(contentGetter, contentParser);

        PagerEdgesCalculator partCalculator = new MarginPagerEdgesCalculator(
                50,
                20
        );

        candidateFactory = new DynamoDbCandidateFactory(
                contentProvider,
                candidateRepository,
                100,
                partCalculator
        );
        searchId = UUID.randomUUID().toString();
        candidateIndex = 2;
    }

    private ContentGetter buildMockContentGetter()
    {
        Queue<String> contents = new LinkedList<>();
        String content;
        try
        {
            content = FileSystemService.readResourceFile(
                    "sample_7_paragraphs.html"
            );
        } catch (URISyntaxException | IOException e)
        {
            throw new RuntimeException();
        }
        contents.add(content);

        return new MockQueueContentGetter(contents);
    }

    private Candidate createNewCandidate()
    {
        return candidateFactory.create(
                candidateIndex,
                new User("userId", "sessionId"),
                searchId,
                "title",
                "https://url.dot",
                "description"
        );
    }

    private void assertEqualCandidate(Candidate candidateA, Candidate candidateB)
    {
        Assert.assertEquals(candidateA.getId(), candidateB.getId());
        Assert.assertEquals(
                candidateA.getDescription(),
                candidateB.getDescription()
        );
        Assert.assertEquals(candidateA.getIndex(), candidateB.getIndex());
        Assert.assertEquals(candidateA.getParagraphPosition(), candidateB.getParagraphPosition());
        Assert.assertEquals(candidateA.getSearchId(), candidateB.getSearchId());
        Assert.assertEquals(candidateA.getSessionId(), candidateB.getSessionId());
        Assert.assertEquals(candidateA.getTitle(), candidateB.getTitle());
        Assert.assertEquals(candidateA.getUrl(), candidateB.getUrl());
        Assert.assertEquals(candidateA.getUserId(), candidateB.getUserId());
    }

    @Test
    public void insertAndGetTest()
    {
        Candidate createdCandidate = createNewCandidate();
        createdCandidate.persist();
        Candidate gotCandidate = candidateRepository.get(searchId, candidateIndex);
        assertEqualCandidate(createdCandidate, gotCandidate);
    }

    @Test
    public void updateAndGetTest() throws NoParagraphsException
    {
        Candidate createdCandidate = createNewCandidate();
        createdCandidate.dump(3, Presenter.STRONG_TOKEN);
        Candidate gotCandidate = candidateRepository.get(searchId, candidateIndex);
        List<Map.Entry<Integer, Paragraph>> paragraphsList = Lists.newArrayList(gotCandidate.getParagraphs().entrySet().iterator());
        for (Integer index = 0; index < paragraphsList.size(); index ++)
        {
            Map.Entry<Integer, Paragraph> entry = paragraphsList.get(index);
            Assert.assertEquals(entry.getKey(), index);
        }
        assertEqualCandidate(createdCandidate, gotCandidate);
        Assert.assertEquals(
                SAMPLE_SRC,
                ((Paragraph)((Map.Entry)((LinkedHashMap)gotCandidate.getParagraphs()).entrySet().toArray()[7]).getValue()).getContent().get("src")
        );
    }
}