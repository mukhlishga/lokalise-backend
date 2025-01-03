package com.lokalise.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class Page {
    private long id;
    private String name;
    private String imageLink;
    private String annotatedImageLink;
    private String locale;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<String> tags;
}
