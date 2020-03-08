package org.zeroqu.ircore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultRecord implements Serializable {
    private Record record;
    private double score;
}
