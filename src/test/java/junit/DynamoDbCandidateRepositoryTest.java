package junit;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.MockContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class DynamoDbCandidateRepositoryTest
{
    private MockContentParser contentParser;
    private CandidateRepository candidateRepository;
    private ContentGetter contentGetter;
    private Presenter presenter;
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
        contentParser = new MockContentParser();
        contentGetter = new UnirestContentGetter("fakebrowser");
        presenter = new AlexaPresenter(new MockTranslator());
        contentProvider = new RemoteTextContentProvider(contentGetter, contentParser);

        candidateFactory = new DynamoDbCandidateFactory(
                contentProvider,
                candidateRepository,
                100
        );
        searchId = UUID.randomUUID().toString();
        candidateIndex = 2;
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
    public void updateAndGetTest()
    {
        Candidate createdCandidate = createNewCandidate();
        createdCandidate.persist();
        Candidate gotCandidate = candidateRepository.get(searchId, candidateIndex);
        assertEqualCandidate(createdCandidate, gotCandidate);
    }
}