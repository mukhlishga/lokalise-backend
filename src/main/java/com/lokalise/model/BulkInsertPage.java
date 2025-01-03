package com.lokalise.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkInsertPage {
    @JsonProperty("page_name")
    private String pageName;
    private List<String> tags;
    @JsonProperty("image_url")
    private String imageUrl;
    private List<Locale> locale;
}
