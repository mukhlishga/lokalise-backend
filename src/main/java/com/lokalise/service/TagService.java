package com.lokalise.service;

import com.lokalise.contract.GenericError;
import com.lokalise.contract.TagContract;
import com.lokalise.persistence.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public TagContract.GenericTagResponse create(TagContract.CreateTagRequest request) {
        long tagId = tagRepository.createTag(request.getName());

        if (tagId < 0) {
            log.error("failed to insert upload data");
            return TagContract.GenericTagResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to insert upload data")
                    .build())
                .build();
        }

        log.info("success to create tags");
        return TagContract.GenericTagResponse.builder()
            .success(true)
            .build();
    }

    public TagContract.GetTagsResponse getTags() {
        var tags = tagRepository.getTags();

        if (tags == null) {
            log.error("failed to get tags");
            return TagContract.GetTagsResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to get tags")
                    .build())
                .build();
        }

        log.info("success to get tags");
        return TagContract.GetTagsResponse.builder()
            .success(true)
            .data(tags)
            .build();
    }
}
