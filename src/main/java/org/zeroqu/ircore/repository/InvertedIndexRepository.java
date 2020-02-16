package org.zeroqu.ircore.repository;

import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Record;
import org.zeroqu.ircore.util.Tokenizer;

import java.util.*;
import java.util.stream.Collectors;

public class InvertedIndexRepository {
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

    public String printInvertedIndexRepository() {
        return "Inverted Indexes: \n" + printInvertedInvertedIndexes() + "\nTerm Frequencies: \n" + printTermFrequencies();
    }

    public String printInvertedIndexRepositorySnapshot() {
        StringBuilder sb = new StringBuilder();
        List<String> tokens = new ArrayList<>(invertedIndexes.keySet());
        Collections.sort(tokens);
        for (String token : tokens) {
            sb.append("Term: ").append(token);
            sb.append(", Total Frequency: ").append(termFrequencies.get(token));
            List<String> docIds = invertedIndexes.get(token).stream().map((Posting::getRecordNum)).collect(Collectors.toList());
            sb.append(", docID List: ").append(docIds.toString()).append("\n\n");
        }
        return sb.toString();
    }
}
