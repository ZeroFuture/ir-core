package org.zeroqu.ircore.tokenizer;

import org.zeroqu.ircore.repository.StopWordsRepository;

public class TokenizerFactory {
    public static Tokenizer buildTokenizer(TokenizerType tokenizerType, StopWordsRepository stopWords) {
        switch (tokenizerType) {
            case SimpleTextTokenizer:
                return SimpleTextTokenizer.build(stopWords);
            default:
                throw new IllegalArgumentException("Invalid tokenizerType!");
        }
    }
}
