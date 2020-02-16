package org.zeroqu.ircore.model;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Posting implements Serializable {
    private final String term;
    private final String recordNum;
    private final List<Integer> positions;
    private int frequency;

    public Posting(String term, String recordNum) {
        this.term = term;
        this.recordNum = recordNum;
        this.frequency = 0;
        this.positions = new ArrayList<>();
    }

    public void addPosition(int position) {
        this.positions.add(position);
        this.frequency++;
    }
}
