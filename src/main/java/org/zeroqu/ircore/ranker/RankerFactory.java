package org.zeroqu.ircore.ranker;

import org.zeroqu.ircore.repository.InvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.tokenizer.Tokenizer;

public class RankerFactory {
    public static Ranker buildRanker(RankerType rankerType, Tokenizer tokenizer,
                                     InvertedIndexRepository invertedIndexes, RecordRepository recordRepository) {
        switch (rankerType) {
            case TfIdfRanker:
                return TfIdfRanker.build(tokenizer, invertedIndexes, recordRepository);
            default:
                throw new IllegalArgumentException("Invalid rankerType!");
        }
    }
}
