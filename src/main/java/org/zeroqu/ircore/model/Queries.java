package org.zeroqu.ircore.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Data
@XmlRootElement(name = "QUERIES")
@XmlAccessorType(XmlAccessType.NONE)
public class Queries implements Serializable {
    @XmlElement(name = "QUERY")
    private List<Query> queries;
}
