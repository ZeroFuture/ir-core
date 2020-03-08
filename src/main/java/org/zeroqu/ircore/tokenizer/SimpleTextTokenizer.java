package org.zeroqu.ircore.tokenizer;

import opennlp.tools.stemmer.PorterStemmer;
import org.zeroqu.ircore.repository.StopWordsRepository;

import java.util.ArrayList;
import java.util.List;

public class SimpleTextTokenizer implements Tokenizer {
    private static final String CLEAN_TEXT_REGEX = "[.,!?()\\[\\]+:;_<>\"/\n%&#@^*]";
    private static final String TOKEN_MATCH_REGEX = ".*[a-z]+.*";
    private StopWordsRepository stopWords;

    private SimpleTextTokenizer(StopWordsRepository stopWords) {
        this.stopWords = stopWords;
    }

    public static SimpleTextTokenizer build(StopWordsRepository stopWords) {
        return new SimpleTextTokenizer(stopWords);
    }

    @Override
    public List<String> tokenize(String text) {
        String cleanedText = text.trim().toLowerCase().replaceAll(CLEAN_TEXT_REGEX, " ");
        String[] tokens = cleanedText.split(" ");
        PorterStemmer stemmer = new PorterStemmer();

        List<String> result = new ArrayList<>();
        for (String token : tokens) {
            if (token.length() > 0 && token.matches(TOKEN_MATCH_REGEX) && !stopWords.containsStopWord(token)) {
                token = stemmer.stem(token);
                int startIdx = 0;
                while (startIdx < token.length() && !Character.isLetterOrDigit(token.charAt(startIdx))) {
                    startIdx++;
                }
                int endIdx = token.length() - 1;
                while (endIdx >= 0 && !Character.isLetterOrDigit(token.charAt(endIdx))) {
                    endIdx--;
                }
                token = token.substring(startIdx, endIdx + 1);
                if (token.length() > 0) result.add(token);
            }
        }
        return result;
    }
}
