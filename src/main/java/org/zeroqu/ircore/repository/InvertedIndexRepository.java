package org.zeroqu.ircore.repository;

import lombok.Getter;
import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.PostingList;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.io.Serializable;
import java.util.*;

@Getter
public class InvertedIndexRepository implements Serializable {
    private final Map<String, PostingList> invertedIndexes;
    private final Tokenizer tokenizer;

    private InvertedIndexRepository(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.invertedIndexes = new HashMap<>();
    }

    public static InvertedIndexRepository build(List<Document> documents, Tokenizer tokenizer) {
        InvertedIndexRepository invertedIndexRepository = new InvertedIndexRepository(tokenizer);
        documents.forEach(invertedIndexRepository::addDocument);
        return invertedIndexRepository;
    }

    public static InvertedIndexRepository build(Document document, Tokenizer tokenizer) {
        InvertedIndexRepository invertedIndexRepository = new InvertedIndexRepository(tokenizer);
        invertedIndexRepository.addDocument(document);
        return invertedIndexRepository;
    }

    private void addPosting(Posting posting) {
        String term = posting.getTerm();
        invertedIndexes.putIfAbsent(term, new PostingList());
        invertedIndexes.get(term).addPosting(posting);
    }

    private void addDocument(Document document) {
        document.getRecords().forEach(record -> {
            Map<String, Posting> postings = new HashMap<>();
            String title = record.getTitle();
            String content = record.getContent();
            String aggregate = title + content;
            List<String> tokens = tokenizer.tokenize(aggregate);

            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                postings.putIfAbsent(token, new Posting(token, record.getRecordNum()));
                postings.get(token).addPosition(i);
                record.getTokens().add(token);
            }

            postings.values().forEach(this::addPosting);
            record.setPostings(postings);
        });

        invertedIndexes.values().forEach(postingList ->
                postingList.getPostings().sort(Comparator.comparing(Posting::getRecordNum)));
    }
}
