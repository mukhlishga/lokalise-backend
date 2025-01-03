package com.lokalise.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Tag {
    private long id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
