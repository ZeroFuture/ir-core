package org.zeroqu.ircore.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Data
@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.NONE)
public class Item implements Serializable {
    @XmlValue
    private String recordNum;

    private String score;

    @XmlAttribute
    public String getScore() {
        return score;
    }
}
