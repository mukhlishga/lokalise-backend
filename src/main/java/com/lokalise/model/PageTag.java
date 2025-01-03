package com.lokalise.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class PageTag {
    private long id;
    private long pageId;
    private long tagId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
