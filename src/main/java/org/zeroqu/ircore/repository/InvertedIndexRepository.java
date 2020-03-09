package org.zeroqu.ircore.repository;

import org.zeroqu.ircore.model.Posting;

import java.io.Serializable;

public interface InvertedIndexRepository<T> extends Serializable {
    void addPosting(Posting posting);
    void add(T document);
}
