package org.zeroqu.ircore.ranker;

import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Record;
import org.zeroqu.ircore.model.ResultRecord;
import org.zeroqu.ircore.repository.InvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.util.*;
import java.util.stream.Collectors;

public class TfIdfRanker implements Ranker {
    private final Tokenizer tokenizer;
    private final InvertedIndexRepository invertedIndexes;
    private final RecordRepository recordRepository;

    private TfIdfRanker(Tokenizer tokenizer, InvertedIndexRepository invertedIndexes,
                        RecordRepository recordRepository) {
        this.tokenizer = tokenizer;
        this.invertedIndexes = invertedIndexes;
        this.recordRepository = recordRepository;
    }

    public static TfIdfRanker build(Tokenizer tokenizer, InvertedIndexRepository invertedIndexes,
                                    RecordRepository recordRepository) {
        return new TfIdfRanker(tokenizer, invertedIndexes, recordRepository);
    }

    @Override
    public double score(String query, String recordNum) {
        Record record = recordRepository.getRecords().get(recordNum);
        List<String> queryTokens = tokenizer.tokenize(query);
        Map<String, Posting> recordPostings = record.getPostings();
        Set<String> queryTokenSet = new HashSet<>(queryTokens);
        double N = recordRepository.getRecords().size();

        return recordPostings.keySet().stream().map(recordToken -> {
            if (queryTokenSet.contains(recordToken)) {
                Posting posting = recordPostings.get(recordToken);
                double tf = posting.getFrequency();
                double df = invertedIndexes.getInvertedIndexes().get(recordToken).getDocumentFrequency();
                return Math.log10(1.0 + tf) * Math.log10(N / df);
            }
            else return 0.0;
        }).reduce(0.0, Double::sum);
    }

    @Override
    public List<ResultRecord> rank(String query) {
        List<ResultRecord> resultRecords = recordRepository.getRecords().keySet()
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

        return resultRecords;
    }
}
