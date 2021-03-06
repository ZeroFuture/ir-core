package org.zeroqu.ircore.repository;

import lombok.Getter;
import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.model.Record;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class RecordRepository implements Serializable {
    private final Map<String, Record> records;
    private int averageRecordLength;

    private RecordRepository() {
        this.records = new HashMap<>();
    }

    public static RecordRepository build(Document document) {
        RecordRepository recordRepository = new RecordRepository();
        document.getRecords().forEach(recordRepository::addRecord);
        recordRepository.averageRecordLength = recordRepository.records.values().stream()
                .map(record -> record.getTokens().size())
                .reduce(0, Integer::sum) / recordRepository.records.size();
        return recordRepository;
    }

    public static RecordRepository build(List<Document> documents) {
        RecordRepository recordRepository = new RecordRepository();
        documents.forEach(document -> document.getRecords().forEach(recordRepository::addRecord));
        recordRepository.averageRecordLength = recordRepository.records.values().stream()
                .map(record -> record.getTokens().size())
                .reduce(0, Integer::sum) / recordRepository.records.size();
        return recordRepository;
    }

    public void addRecord(Record record) {
        records.put(record.getRecordNum(), record);
    }
}
