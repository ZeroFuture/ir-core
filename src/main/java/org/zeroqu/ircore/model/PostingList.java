package org.zeroqu.ircore.model;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class PostingList implements Serializable {
    private final List<Posting> postings;
    private int documentFrequency;
    private long termFrequency;

    public PostingList() {
        postings = new ArrayList<>();
        documentFrequency = 0;
        termFrequency = 0;
    }

    public void addPosting(Posting posting) {
        postings.add(posting);
        termFrequency += posting.getFrequency();
        documentFrequency++;
    }
}
