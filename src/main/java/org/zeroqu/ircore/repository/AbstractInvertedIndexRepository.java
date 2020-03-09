package org.zeroqu.ircore.repository;

import lombok.Getter;
import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.PostingList;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class AbstractInvertedIndexRepository<T> implements InvertedIndexRepository<T> {
    protected final Map<String, PostingList> invertedIndexes;
    protected final Tokenizer tokenizer;

    protected AbstractInvertedIndexRepository(Tokenizer tokenizer) {
        this.invertedIndexes = new HashMap<>();
        this.tokenizer = tokenizer;
    }

    public void addPosting(Posting posting) {
        String term = posting.getTerm();
        invertedIndexes.putIfAbsent(term, new PostingList());
        invertedIndexes.get(term).addPosting(posting);
    }

}
