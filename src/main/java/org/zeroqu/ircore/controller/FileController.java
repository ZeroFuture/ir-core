package org.zeroqu.ircore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.repository.InvertedIndexRepository;
import org.zeroqu.ircore.repository.RecordRepository;
import org.zeroqu.ircore.repository.StopWordsRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping(path = "/upload")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class.getName());

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();

        if (fileName == null) {
            logger.error(String.format("msg=\"Invalid uploaded file!\""));
            return ResponseEntity.badRequest().build();
        }

        logger.info(String.format("msg=\"Successfully received uploaded file\" fileName=%s fileSize=%s",
                fileName, fileSize));

        String currentPath = System.getProperty("user.dir");
        File localCopy = new File(currentPath + "/" + fileName);

        try {
            file.transferTo(localCopy);
            logger.info(String.format("msg=\"Successfully stored uploaded file\" fileName=%s fileSize=%s",
                    fileName, fileSize));
        } catch (Exception e) {
            logger.error(String.format("msg=\"Failed transfer file to local\" fileName=%s fileSize=%s error=%s",
                    fileName, fileSize, e.getMessage()));
        }

        long startTime = System.currentTimeMillis();
        Document document = buildDocument(localCopy);

        if (document == null) {
            logger.error(String.format("msg=\"Invalid uploaded file!\" fileName=%s fileSize=%s", fileName, fileSize));
            return ResponseEntity.badRequest().build();
        }

        try {
            File stopWords = new File("./src/main/resources/static/stoplist.txt");
            StopWordsRepository stopWordsRepository = StopWordsRepository.build(stopWords);
            RecordRepository recordRepository = RecordRepository.build(document);
            InvertedIndexRepository invertedIndexRepository = InvertedIndexRepository.build(document, stopWordsRepository);

            long endTime = System.currentTimeMillis();
            long indexingTime = endTime - startTime;

            Map<String, Object> responseMap = invertedIndexRepository.getStoreMap();
            double indexingTimeInSec = indexingTime / 1000.0;
            long memoryUsageInKb = calculateObjectSize(invertedIndexRepository);
            responseMap.put("indexingTime", indexingTimeInSec);
            responseMap.put("memoryUsage", memoryUsageInKb);

            ResponseEntity<String> response = new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseMap), HttpStatus.OK);
            logger.info(String.format("msg=\"Successfully created response!\" fileName=%s fileSize=%s indexingTime=%s memoryUsage=%s",
                    fileName, fileSize, indexingTimeInSec, memoryUsageInKb));

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("msg=\"Failed processing uploaded file\" fileName=%s fileSize=%s error=%s", fileName, fileSize, e.getMessage()));
            return ResponseEntity.badRequest().build();
        }
    }

    private Document buildDocument(File file) {
        Document document = null;
        try {
            JAXBContext context = JAXBContext.newInstance(Document.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            document = (Document) unmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("msg=\"Failed to parse XML document!\" error=%s", e.getMessage()));
        }
        return document;
    }

    private long calculateObjectSize(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);

        oos.close();
        bos.close();
        byte[] byteArr = bos.toByteArray();

        return byteArr.length / 1000;
    }
}
