package org.zeroqu.ircore.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "RECORD")
@XmlAccessorType(XmlAccessType.NONE)
public class Record {
    @XmlElement(name = "PAPERNUM")
    private String paperNum;

    @XmlElement(name = "RECORDNUM")
    private String recordNum;

    @XmlElement(name = "MEDLINENUM")
    private String medLineNum;

    @XmlElementWrapper(name = "AUTHORS")
    @XmlElement(name = "AUTHOR")
    private List<String> authors;

    @XmlElement(name = "TITLE")
    private String title;

    @XmlElement(name = "SOURCE")
    private String source;

    @XmlElementWrapper(name = "MAJORSUBJ")
    @XmlElement(name = "TOPIC")
    private List<String> majorSubjects;

    @XmlElementWrapper(name = "MINORSUBJ")
    @XmlElement(name = "TOPIC")
    private List<String> minorSubjects;

    @XmlElement(name = "ABSTRACT")
    private String content;

    @XmlElementWrapper(name = "REFERENCES")
    @XmlElement(name = "CITE")
    private List<Cite> references;

    @XmlElementWrapper(name = "CITATIONS")
    @XmlElement(name = "CITE")
    private List<Cite> citations;

    @XmlTransient
    private List<String> tokens;


    public Record() {
        this.authors = new ArrayList<>();
        this.citations = new ArrayList<>();
        this.references = new ArrayList<>();
        this.majorSubjects = new ArrayList<>();
        this.minorSubjects = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }
}