package com.lokalise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Locale {
    private String name;
    private Values values;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Values {
        private String en;
        private String id;
        private String vn;
    }
}
