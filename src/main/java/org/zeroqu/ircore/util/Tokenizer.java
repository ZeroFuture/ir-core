package org.zeroqu.ircore.util;

import org.zeroqu.ircore.model.Posting;
import org.zeroqu.ircore.model.Record;
import org.zeroqu.ircore.repository.InvertedIndexRepository;
import org.zeroqu.ircore.repository.StopWordsRepository;

import java.util.HashMap;
import java.util.Map;

public class Tokenizer {
    private static final String CLEAN_TEXT_REGEX = "[.,!?()\\[\\]+:;_<>\"/\n%&#@^*]";
    private static final String TOKEN_MATCH_REGEX = ".*[a-z]+.*";

    public static void tokenize(Record record, InvertedIndexRepository invertedIndexes, StopWordsRepository stopWords) {
        Map<String, Posting> postings = new HashMap<>();
        String title = record.getTitle();
        String content = record.getContent();
        String aggregate = title + content;

        String cleanedText = aggregate.trim().toLowerCase().replaceAll(CLEAN_TEXT_REGEX, " ");
        String[] tokens = cleanedText.split(" ");

        int pos = 0;
        for (String token: tokens) {
            if (token.length() > 0 && token.matches(TOKEN_MATCH_REGEX) && !stopWords.containsStopWord(token)) {
                postings.putIfAbsent(token, new Posting(token, record.getRecordNum()));
                postings.get(token).addPosition(pos);
                record.getTokens().add(token);
                pos++;
            }
        }

        for (Posting posting : postings.values()) {
            invertedIndexes.addPosting(posting);
        }
    }
}
