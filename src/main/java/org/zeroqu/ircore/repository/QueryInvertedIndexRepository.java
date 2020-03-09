package org.zeroqu.ircore.repository;

import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Queries;
import org.zeroqu.ircore.tokenizer.Tokenizer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryInvertedIndexRepository extends AbstractInvertedIndexRepository<Queries> {
    private QueryInvertedIndexRepository(Tokenizer tokenizer) {
        super(tokenizer);
    }

    public static QueryInvertedIndexRepository build(List<Queries> documents, Tokenizer tokenizer) {
        QueryInvertedIndexRepository queryInvertedIndexRepository = new QueryInvertedIndexRepository(tokenizer);
        documents.forEach(queryInvertedIndexRepository::add);
        return queryInvertedIndexRepository;
    }

    public static QueryInvertedIndexRepository build(Queries documents, Tokenizer tokenizer) {
        QueryInvertedIndexRepository queryInvertedIndexRepository = new QueryInvertedIndexRepository(tokenizer);
        queryInvertedIndexRepository.add(documents);
        return queryInvertedIndexRepository;
    }

    @Override
    public void add(Queries document) {
        document.getQueries().forEach(query -> {
            Map<String, Posting> postings = new HashMap<>();
            String text = query.getQueryText();
            List<String> tokens = tokenizer.tokenize(text);

            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                postings.putIfAbsent(token, new Posting(token, query.getQueryNumber()));
                postings.get(token).addPosition(i);
            }

            postings.values().forEach(this::addPosting);
        });

        invertedIndexes.values().forEach(postingList ->
                postingList.getPostings().sort(Comparator.comparing(Posting::getRecordNum)));
    }
}
