package utils;

import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import com.westial.alexa.jumpandread.domain.PagerEdgesCalculator;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CandidateHelper
{
    public static Candidate buildCandidate(
            int index,
            List<List<String>> candidateData,
            TextContentProvider contentProvider,
            CandidateRepository candidateRepository,
            PagerEdgesCalculator partCalculator,
            boolean isListed
    )
    {
        String id = null;
        Integer candidateListIndex = null;

        if (isListed)
        {
            id = DynamoDbCandidate.buildId(
                    candidateData.get(index).get(3),
                    Integer.parseInt(candidateData.get(index).get(0))
            );
            candidateListIndex = Integer.parseInt(candidateData.get(index).get(0));
        }

        return new DynamoDbCandidate(
                id,
                candidateListIndex,
                candidateData.get(index).get(1),
                candidateData.get(index).get(2),
                candidateData.get(index).get(3),
                candidateData.get(index).get(4),
                candidateData.get(index).get(5),
                candidateData.get(index).get(6),
                contentProvider,
                candidateRepository,
                Integer.parseInt(candidateData.get(index).get(7)),
                100,
                partCalculator
        );
    }
    public static Candidate buildUnlistedCandidate(
            String userId,
            String sessionId,
            String searchId,
            String title,
            String url,
            String description,
            TextContentProvider contentProvider,
            CandidateRepository candidateRepository,
            int paragraphPosition,
            PagerEdgesCalculator partCalculator
    )
    {

        return new DynamoDbCandidate(
                null,
                null,
                userId,
                sessionId,
                searchId,
                title,
                url,
                description,
                contentProvider,
                candidateRepository,
                paragraphPosition,
                100,
                partCalculator
        );
    }

    public static List<String> createCandidateDataBySearchIdAndUrl(int index, String userId, String sessionId, String searchId, String url)
    {
        List<String> candidateDataItem = new ArrayList<>();
        candidateDataItem.add(String.format("%d", index));
        if (null == userId)
            candidateDataItem.add("user:" + UUID.randomUUID().toString());
        else
            candidateDataItem.add(userId);
        if (null == sessionId)
            candidateDataItem.add("session:" + UUID.randomUUID().toString());
        else
            candidateDataItem.add(sessionId);
        candidateDataItem.add(searchId);
        candidateDataItem.add("title:" + UUID.randomUUID().toString());
        candidateDataItem.add(url);
        candidateDataItem.add("description:" + UUID.randomUUID().toString());
        candidateDataItem.add("0");
        return candidateDataItem;
    }

    public static List<Candidate> buildRandomCandidatesBySearchId(
            int count,
            String userId,
            String sessionId,
            String searchId,
            TextContentProvider contentProvider,
            CandidateRepository candidateRepository,
            PagerEdgesCalculator partCalculator,
            boolean areListed
    )
    {
        List<List<String>> candidateData = new ArrayList<>();

        for (int i = 0; i < count; i ++)
        {
            String url = "http://" + UUID.randomUUID().toString() + ".com";
            candidateData.add(createCandidateDataBySearchIdAndUrl(i + 1, userId, sessionId,searchId, url));
        }

        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            candidates.add(
                    buildCandidate(
                            i,
                            candidateData,
                            contentProvider,
                            candidateRepository,
                            partCalculator,
                            areListed
                    )
            );
        }
        return candidates;
    }
}
