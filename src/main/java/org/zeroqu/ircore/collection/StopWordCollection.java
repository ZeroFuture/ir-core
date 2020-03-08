package org.zeroqu.ircore.collection;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.zeroqu.ircore.repository.StopWordsRepository;

import java.io.File;
import java.io.IOException;

@Getter
@Component
public class StopWordCollection {
    private StopWordsRepository stopWordsRepository;

    public StopWordCollection() throws IOException {
        File stopWords = new File("./src/main/resources/static/stoplist.txt");
        this.stopWordsRepository = StopWordsRepository.build(stopWords);
    }
}
