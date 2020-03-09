package org.zeroqu.ircore.collection;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.repository.DocumentInvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.util.XMLObjectMapper;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Component
public class RecordCollection {
    private DocumentInvertedIndexRepository documentInvertedIndexRepository;
    private RecordRepository recordRepository;

    private static Logger logger = LoggerFactory.getLogger(RecordCollection.class.getName());

    @Autowired
    public RecordCollection(TokenizerCollection tokenizerCollection) {
        List<File> files = new ArrayList<>();
        for (int i = 74; i <= 79; i++) {
            File file = new File(String.format("./src/main/resources/static/cfc-xml/cf%d.xml", i));
            files.add(file);
        }

        List<Document> documents = files.stream().map(file -> {
            try {
                return XMLObjectMapper.map(file, Document.class);
            } catch (JAXBException e) {
                logger.error(String.format("msg=\"Failed parse XML file!\" fileName=%s error=%s",
                        file.getName(), e.getMessage()));
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        this.documentInvertedIndexRepository = DocumentInvertedIndexRepository.build(documents, tokenizerCollection.getTokenizer());
        this.recordRepository = RecordRepository.build(documents);
    }
}
