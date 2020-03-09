package org.zeroqu.ircore.repository;

import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.util.*;

public class DocumentInvertedIndexRepository extends AbstractInvertedIndexRepository<Document> {

    private DocumentInvertedIndexRepository(Tokenizer tokenizer) {
        super(tokenizer);
    }

    public static DocumentInvertedIndexRepository build(List<Document> documents, Tokenizer tokenizer) {
        DocumentInvertedIndexRepository documentInvertedIndexRepository = new DocumentInvertedIndexRepository(tokenizer);
        documents.forEach(documentInvertedIndexRepository::add);
        return documentInvertedIndexRepository;
    }

    public static DocumentInvertedIndexRepository build(Document document, Tokenizer tokenizer) {
        DocumentInvertedIndexRepository documentInvertedIndexRepository = new DocumentInvertedIndexRepository(tokenizer);
        documentInvertedIndexRepository.add(document);
        return documentInvertedIndexRepository;
    }

    @Override
    public void add(Document document) {
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
