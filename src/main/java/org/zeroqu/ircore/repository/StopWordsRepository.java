package org.zeroqu.ircore.repository;

import lombok.Getter;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Getter
public class StopWordsRepository implements Serializable {
    private final Set<String> stopWords;

    private StopWordsRepository() {
        this.stopWords = new HashSet<>();
    }

    public static StopWordsRepository build(File file) throws IOException {
        StopWordsRepository stopWordsRepository = new StopWordsRepository();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String word;
        while ((word = bufferedReader.readLine()) != null) {
            stopWordsRepository.addStopWord(word);
        }
        return stopWordsRepository;
    }

    public boolean addStopWord(String stopWord) {
        return stopWords.add(stopWord);
    }

    public boolean containsStopWord(String stopWord) {
        return stopWords.contains(stopWord);
    }
}
