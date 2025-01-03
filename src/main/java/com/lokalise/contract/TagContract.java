package com.lokalise.contract;

import com.lokalise.model.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.List;

public class TagContract {

    @Data
    @Builder
    public static class CreateTagRequest {
        String name;
    }

    @Data
    @Builder
    public static class GenericTagResponse {
        boolean success;
        GenericError error;
    }

    @Data
    @Builder
    public static class GetTagsResponse {
        boolean success;
        List<Tag> data;
        GenericError error;
    }
}
