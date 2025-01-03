package com.lokalise.contract;

import com.lokalise.model.Page;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class PageContract {

    @Data
    @Builder
    public static class CreatePageRequest {
        String name;
        List<String> tags;
        String locale;
    }

    @Data
    @Builder
    public static class GetPageRequest {
        String name;
        String tags;
    }

    @Data
    @Builder
    public static class EditPageRequest {
        String locale;
    }


    @Data
    @Builder
    public static class GenericPageResponse {
        boolean success;
        GenericError error;
    }

    @Data
    @Builder
    public static class GetPagesResponse {
        boolean success;
        List<Page> data;
        GenericError error;
    }

    @Data
    @Builder
    public static class GetPageResponse {
        boolean success;
        Page data;
        GenericError error;
    }
}
