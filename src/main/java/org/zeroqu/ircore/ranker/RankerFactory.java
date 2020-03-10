package org.zeroqu.ircore.ranker;

import org.zeroqu.ircore.repository.DocumentInvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.tokenizer.Tokenizer;

public class RankerFactory {
    public static Ranker buildRanker(RankerType rankerType, Tokenizer tokenizer,
                                     DocumentInvertedIndexRepository documentInvertedIndexRepository,
                                     RecordRepository recordRepository) {
        switch (rankerType) {
            case TfIdfRanker:
                return TfIdfRanker.build(tokenizer, documentInvertedIndexRepository, recordRepository);
            case CosineSimilarityRanker:
                return CosineSimilarityRanker.build(tokenizer, documentInvertedIndexRepository, recordRepository);
            case BM25Ranker:
                return BM25Ranker.build(tokenizer, documentInvertedIndexRepository, recordRepository);
            default:
                throw new IllegalArgumentException("Invalid rankerType!");
        }
    }
}
