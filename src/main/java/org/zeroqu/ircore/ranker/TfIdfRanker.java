package org.zeroqu.ircore.ranker;

import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Record;
import org.zeroqu.ircore.model.ResultRecord;
import org.zeroqu.ircore.repository.DocumentInvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.util.*;
import java.util.stream.Collectors;

public class TfIdfRanker implements Ranker {
    private final Tokenizer tokenizer;
    private final DocumentInvertedIndexRepository documentInvertedIndexes;
    private final RecordRepository recordRepository;

    private TfIdfRanker(Tokenizer tokenizer, DocumentInvertedIndexRepository documentInvertedIndexes,
                        RecordRepository recordRepository) {
        this.tokenizer = tokenizer;
        this.documentInvertedIndexes = documentInvertedIndexes;
        this.recordRepository = recordRepository;
    }

    public static TfIdfRanker build(Tokenizer tokenizer, DocumentInvertedIndexRepository documentInvertedIndexes,
                                    RecordRepository recordRepository) {
        return new TfIdfRanker(tokenizer, documentInvertedIndexes, recordRepository);
    }

    @Override
    public double score(String query, String recordNum) {
        Record record = recordRepository.getRecords().get(recordNum);
        List<String> queryTokens = tokenizer.tokenize(query);

        Map<String, Posting> recordPostings = record.getPostings();
        Set<String> queryTokenSet = new HashSet<>(queryTokens);
        double docN = recordRepository.getRecords().size();

        Set<String> intersectTokens = recordPostings.keySet().stream()
                .map(recordToken -> queryTokenSet.contains(recordToken) ? recordToken : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return intersectTokens.stream().map(token -> {
            double docTf = recordPostings.get(token).getFrequency();
            double docDf = documentInvertedIndexes.getInvertedIndexes().get(token).getDocumentFrequency();
            return Math.log10(1.0 + docTf) * Math.log10(docN / docDf);
        }).reduce(0.0, Double::sum);
    }

    @Override
    public List<ResultRecord> rank(String query) {
        return recordRepository.getRecords().keySet()
                .stream()
                .map(recordNum -> {
                    Record record = recordRepository.getRecords().get(recordNum);
                    double score = score(query, recordNum);
                    return score > 0.0 ? new ResultRecord(record, score) : null;
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> {
                    double diff = b.getScore() - a.getScore();
                    return (int) (diff < 0 ? Math.floor(diff) : Math.ceil(diff));
                })
                .collect(Collectors.toList());
    }
}
