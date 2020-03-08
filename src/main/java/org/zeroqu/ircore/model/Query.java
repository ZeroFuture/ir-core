package org.zeroqu.ircore.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.NONE)
public class Query implements Serializable {
    @XmlElement(name = "QueryNumber")
    private String queryNumber;

    @XmlElement(name = "QueryText")
    private String queryText;

    @XmlElement(name = "Results")
    private int results;

    @XmlElementWrapper(name = "Records")
    @XmlElement(name = "Item")
    private List<Item> records;

    public Query() {
        records = new ArrayList<>();
    }
}
