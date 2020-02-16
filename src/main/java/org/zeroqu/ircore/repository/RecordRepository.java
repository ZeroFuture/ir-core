package org.zeroqu.ircore.repository;

import org.zeroqu.ircore.model.Document;
import org.zeroqu.ircore.model.Record;

import java.util.HashMap;
import java.util.Map;

public class RecordRepository {
    private final Map<String, Record> records;

    private RecordRepository() {
        this.records = new HashMap<>();
    }

    public static RecordRepository build(Document document) {
        RecordRepository recordRepository = new RecordRepository();
        for (Record record : document.getRecords()) {
            recordRepository.addRecord(record);
        }
        return recordRepository;
    }

    public void addRecord(Record record) {
        records.put(record.getRecordNum(), record);
    }

    public String printRecordRepository() {
        return records.toString();
    }
}
