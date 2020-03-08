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
import org.zeroqu.ircore.collection.RecordCollection;
import org.zeroqu.ircore.collection.TokenizerCollection;
import org.zeroqu.ircore.model.ResultRecord;
import org.zeroqu.ircore.ranker.Ranker;
import org.zeroqu.ircore.ranker.RankerFactory;
import org.zeroqu.ircore.ranker.RankerType;

import java.util.*;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping(path = "/search")
public class SearchController {
    private final RecordCollection recordCollection;
    private final TokenizerCollection tokenizerCollection;

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class.getName());

    @Autowired
    public SearchController(RecordCollection recordCollection, TokenizerCollection tokenizerCollection) {
        this.recordCollection = recordCollection;
        this.tokenizerCollection = tokenizerCollection;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> handleSearch(@RequestParam("query") String query) {
        try {
            long startTime = System.currentTimeMillis();
            Ranker ranker = RankerFactory.buildRanker(RankerType.TfIdfRanker, tokenizerCollection.getTokenizer(),
                    recordCollection.getInvertedIndexRepository(), recordCollection.getRecordRepository());
            Map<String, Object> responseMap = new HashMap<>();
            List<ResultRecord> resultRecords = ranker.rank(query);
            double searchTime = (System.currentTimeMillis() - startTime) / 1000.0;
            responseMap.put("resultRecords", resultRecords);
            responseMap.put("searchTime", searchTime);
            ResponseEntity<String> response = new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseMap),
                    HttpStatus.OK);
            logger.info(String.format("msg=\"Successfully created response!\" query=%s searchTime=%f",
                    query, searchTime));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("msg=\"Failed processing query search\" query=%s error=%s",
                    query, e.getMessage()));
            return ResponseEntity.badRequest().build();
        }
    }
}
