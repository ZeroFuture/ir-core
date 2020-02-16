package org.zeroqu.ircore.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Data
@XmlRootElement(name = "ROOT")
@XmlAccessorType(XmlAccessType.NONE)
public class Document implements Serializable {
    @XmlElement(name = "RECORD")
    private List<Record> records;
}
