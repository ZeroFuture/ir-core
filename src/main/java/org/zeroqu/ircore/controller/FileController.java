package org.zeroqu.ircore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zeroqu.ircore.collection.TokenizerCollection;
import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.repository.InvertedIndexRepository;
import org.zeroqu.ircore.util.ObjectSizeFetcher;
import org.zeroqu.ircore.util.XMLObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping(path = "/upload")
public class FileController {
    private final TokenizerCollection tokenizerCollection;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class.getName());

    @Autowired
    public FileController(TokenizerCollection tokenizerCollection) {
        this.tokenizerCollection = tokenizerCollection;
    }

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

        Document document = null;
        try {
            document = XMLObjectMapper.map(localCopy, Document.class);
        } catch (Exception e) {
            logger.error(String.format("msg=\"Failed parse XML file!\" fileName=%s fileSize=%s error=%s",
                    fileName, fileSize, e.getMessage()));
        }

        if (document == null) {
            logger.error(String.format("msg=\"Invalid uploaded file!\" fileName=%s fileSize=%s", fileName, fileSize));
            return ResponseEntity.badRequest().build();
        }

        try {
            InvertedIndexRepository invertedIndexRepository = InvertedIndexRepository.build(document,
                    tokenizerCollection.getTokenizer());

            long endTime = System.currentTimeMillis();
            long indexingTime = endTime - startTime;

            Map<String, Object> responseMap = new HashMap<>();
            double indexingTimeInSec = indexingTime / 1000.0;
            long memoryUsageInKb = ObjectSizeFetcher.calculateObjectSize(invertedIndexRepository);
            responseMap.put("indexingTime", indexingTimeInSec);
            responseMap.put("memoryUsage", memoryUsageInKb);
            responseMap.put("invertedIndexes", invertedIndexRepository.getInvertedIndexes());

            ResponseEntity<String> response = new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseMap),
                    HttpStatus.OK);
            logger.info(String.format("msg=\"Successfully created response!\" fileName=%s fileSize=%s indexingTime=%s memoryUsage=%s",
                    fileName, fileSize, indexingTimeInSec, memoryUsageInKb));

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("msg=\"Failed processing uploaded file\" fileName=%s fileSize=%s error=%s",
                    fileName, fileSize, e.getMessage()));
            return ResponseEntity.badRequest().build();
        }
    }
}
