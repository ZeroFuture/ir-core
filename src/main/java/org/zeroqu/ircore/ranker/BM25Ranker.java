package org.zeroqu.ircore.ranker;

import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Record;
import org.zeroqu.ircore.repository.DocumentInvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.util.*;

public class BM25Ranker extends TfIdfRanker {

    private BM25Ranker(Tokenizer tokenizer,
                       DocumentInvertedIndexRepository documentInvertedIndexes,
                       RecordRepository recordRepository) {
        super(tokenizer, documentInvertedIndexes, recordRepository);
    }

    public static BM25Ranker build(Tokenizer tokenizer,
                                   DocumentInvertedIndexRepository documentInvertedIndexes,
                                   RecordRepository recordRepository) {
        return new BM25Ranker(tokenizer, documentInvertedIndexes, recordRepository);
    }

    @Override
    public double score(String query, String recordNum) {
        Record record = recordRepository.getRecords().get(recordNum);
        List<String> queryTokens = tokenizer.tokenize(query);
        Map<String, Integer> queryPosting = new HashMap<>();
        for (String queryToken : queryTokens) {
            queryPosting.put(queryToken, queryPosting.getOrDefault(queryToken, 0) + 1);
        }
        Map<String, Posting> recordPostings = record.getPostings();
        Set<String> intersectTokens = getIntersectTokens(recordPostings.keySet(), new HashSet<>(queryTokens));
        double docN = recordRepository.getRecords().size();

        double sum = 0.0;
        for (String token : intersectTokens) {
            double docTf = recordPostings.get(token).getFrequency();
            double docDf = documentInvertedIndexes.getInvertedIndexes().get(token).getDocumentFrequency();
            double queryTf = queryPosting.get(token);
            double dl = record.getTokens().size();
            double avgDl = recordRepository.getAverageRecordLength();

            double k1 = 1.2;
            double k2 = 500.0;
            double b = 0.75;
            double K = k1 * ((1 - b) + b * (dl / avgDl));

            double idf = Math.log10(docN / docDf);

            double p2 = (k1 + 1) * docTf / (K + docTf);
            double p3 = (k2 + 1) * queryTf / (k2 + queryTf);

            sum += idf * p2 * p3;
        }

        return sum;
    }
}
