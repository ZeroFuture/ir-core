package org.zeroqu.ircore.ranker;

import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Record;
import org.zeroqu.ircore.repository.DocumentInvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.util.*;

public class CosineSimilarityRanker extends TfIdfRanker {

    private CosineSimilarityRanker(Tokenizer tokenizer, DocumentInvertedIndexRepository documentInvertedIndexes,
                                  RecordRepository recordRepository) {
        super(tokenizer, documentInvertedIndexes, recordRepository);
    }

    public static CosineSimilarityRanker build(Tokenizer tokenizer, DocumentInvertedIndexRepository documentInvertedIndexes,
                                               RecordRepository recordRepository) {
        return new CosineSimilarityRanker(tokenizer, documentInvertedIndexes, recordRepository);
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

        double normalizer = 0.0;
        double cosineProduct = 0.0;
        for (String token : intersectTokens) {
            double docTf = recordPostings.get(token).getFrequency();
            double docDf = documentInvertedIndexes.getInvertedIndexes().get(token).getDocumentFrequency();
            double queryTf = queryPosting.get(token);
            double idf = Math.log10(docN / docDf);

            double queryTfIdf = Math.log10(1.0 + queryTf) * idf;
            double weightedDocTf = Math.log10(1.0 + docTf);
            cosineProduct += queryTfIdf * weightedDocTf;
            normalizer += Math.pow(weightedDocTf, 2);
        }

        return cosineProduct / Math.sqrt(normalizer);
    }
}
