package org.zeroqu.ircore.collection;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.zeroqu.ircore.tokenizer.Tokenizer;
import org.zeroqu.ircore.tokenizer.TokenizerFactory;
import org.zeroqu.ircore.tokenizer.TokenizerType;

@Getter
@Component
public class TokenizerCollection {
    private Tokenizer tokenizer;

    public TokenizerCollection(StopWordCollection stopWordCollection) {
        this.tokenizer = TokenizerFactory.buildTokenizer(TokenizerType.SimpleTextTokenizer,
                stopWordCollection.getStopWordsRepository());
    }
}
