package org.zeroqu.ircore.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "CITE")
@XmlAccessorType(XmlAccessType.NONE)
public class Cite {
    private String num;
    private String author;
    private String publication;
    private String d1;
    private String d2;
    private String d3;

    @XmlAttribute
    public String getNum() {
        return num;
    }

    @XmlAttribute
    public String getAuthor() {
        return author;
    }

    @XmlAttribute
    public String getPublication() {
        return publication;
    }

    @XmlAttribute
    public String getD1() {
        return d1;
    }

    @XmlAttribute
    public String getD2() {
        return d2;
    }

    @XmlAttribute
    public String getD3() {
        return d3;
    }
}
