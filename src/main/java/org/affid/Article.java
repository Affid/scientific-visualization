package org.affid;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;

@JsonPropertyOrder({"authors", "name", "year", "rate", "doi", "link", "refs"})
public class Article {
    private ArrayList<Long> authors;
    private String name;
    private String year;
    private int rate;
    private String DOI;
    private String link;
    private ArrayList<String> refs;
    private ArrayList<String> keys;

    public ArrayList<String> getKeys() {
        return keys;
    }


    public String getYear() {
        return year;
    }

    public Article(ArrayList<Long> authors, String name,
                   String year, int rate, String DOI,
                   String link, ArrayList<String> refs,
                   ArrayList<String> keys) {
        this.authors = authors;
        this.name = name;
        this.year = year;
        this.rate = rate;
        this.DOI = DOI;
        this.link = link;
        this.refs = refs;
        this.keys = keys;
    }

    public ArrayList<Long> getAuthors() {
        return authors;
    }

    public String getName() {
        return name;
    }

    public int getRate() {
        return rate;
    }

    public String getDOI() {
        return DOI;
    }

    public String getLink() {
        return link;
    }

    public ArrayList<String> getRefs() {
        return refs;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void setRefs(ArrayList<String> refs) {
        this.refs = refs;
    }
}
