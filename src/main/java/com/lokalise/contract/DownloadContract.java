package com.lokalise.contract;

import com.lokalise.model.Locale;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class DownloadContract {

    @Data
    @Builder
    public static class DownloadRequest {
        String tags;
    }

    @Data
    @Builder
    public static class DownloadResponse {
        boolean success;
        List<Locale> data;
        GenericError error;
    }
}


