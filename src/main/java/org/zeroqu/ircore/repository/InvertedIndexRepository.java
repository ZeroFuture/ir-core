package org.zeroqu.ircore.repository;

import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Record;
import org.zeroqu.ircore.util.Tokenizer;

import java.io.Serializable;
import java.util.*;

public class InvertedIndexRepository implements Serializable {
    private final Map<String, List<Posting>> invertedIndexes;
    private final Map<String, Long> termFrequencies;

    private InvertedIndexRepository() {
        this.invertedIndexes = new HashMap<>();
        this.termFrequencies = new HashMap<>();
    }

    public static InvertedIndexRepository build(Document document, StopWordsRepository stopWordsRepository) {
        InvertedIndexRepository invertedIndexRepository = new InvertedIndexRepository();
        for (Record record : document.getRecords()) {
            Tokenizer.tokenize(record, invertedIndexRepository, stopWordsRepository);
        }

        for (List<Posting> postingList: invertedIndexRepository.invertedIndexes.values()) {
            postingList.sort(Comparator.comparing(Posting::getRecordNum));
        }

        return invertedIndexRepository;
    }

    public void addPosting(Posting posting) {
        String term = posting.getTerm();
        invertedIndexes.putIfAbsent(term, new ArrayList<>());
        termFrequencies.putIfAbsent(term, 0L);
        invertedIndexes.get(term).add(posting);
        termFrequencies.put(term, termFrequencies.get(term) + posting.getFrequency());
    }

    public String printInvertedInvertedIndexes() {
        return invertedIndexes.toString();
    }

    public String printTermFrequencies() {
        return termFrequencies.toString();
    }

    public Map<String, Object> getStoreMap() {
        Map<String, Object> storeMap = new HashMap<>();
        storeMap.put("invertedIndexes", invertedIndexes);
        storeMap.put("termFrequencies", termFrequencies);
        return storeMap;
    }
}
