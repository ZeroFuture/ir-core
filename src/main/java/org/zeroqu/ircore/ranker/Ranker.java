package org.zeroqu.ircore.ranker;

import org.zeroqu.ircore.model.ResultRecord;

import java.io.Serializable;
import java.util.List;

public interface Ranker extends Serializable {
    double score(String query, String recordNum);
    List<ResultRecord> rank(String query);
}
