package com.lokalise.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokalise.contract.GenericError;
import com.lokalise.contract.GenericResponse;
import com.lokalise.contract.PageContract;
import com.lokalise.model.BulkInsertPage;
import com.lokalise.model.Page;
import com.lokalise.persistence.repository.PageRepository;
import com.lokalise.persistence.repository.PageTagRepository;
import com.lokalise.persistence.repository.TagRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    private final PageTagRepository pageTagRepository;
    private final TagRepository tagRepository;

    public PageContract.GenericPageResponse create(String pagename, String locale, List<String> tags, String presignUrl) {
        var pageId = pageRepository.createPages(pagename, locale, presignUrl);

        if (pageId < 0) {
            log.error("failed to create page");
            return PageContract.GenericPageResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to create page")
                    .build())
                .build();
        }

        for (var tagName : tags) {
            var pageTagId = pageTagRepository.createPageTag(pageId, tagName);
            if (pageTagId < 0) {
                log.error("failed to create page tag");
                return PageContract.GenericPageResponse.builder()
                    .success(false)
                    .error(GenericError.builder()
                        .code("500")
                        .message("failed to create page tag")
                        .build())
                    .build();
            }
        }

        log.info("success to create page");
        return PageContract.GenericPageResponse.builder()
                .success(true)
                .build();
    }

    public PageContract.GetPagesResponse getPages(PageContract.GetPageRequest request) {
        List<Page> pages;
        if (request.getTags() == null && request.getName() == null) {
            pages = pageRepository.getPages();
        } else if (request.getTags() != null && request.getName() == null) {
            pages = pageRepository.getPagesByTags(request);
        } else if (request.getTags() == null && request.getName() != null) {
            pages = pageRepository.getPagesByName(request);
        } else {
            pages = pageRepository.getPagesByTagsAndName(request);
        }

        if (pages == null) {
            log.error("failed to get pages");
            return PageContract.GetPagesResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to get pages")
                    .build())
                .build();
        }

        log.info("success to get pages");
        return PageContract.GetPagesResponse.builder()
            .success(true)
            .data(pages)
            .build();
    }

    public PageContract.GetPageResponse getPage(String id) {
        var page = pageRepository.getPage(id);

        if (page == null) {
            log.error("failed to get page");
            return PageContract.GetPageResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to get page")
                    .build())
                .build();
        }

        log.info("success to get page");
        return PageContract.GetPageResponse.builder()
            .success(true)
            .data(page)
            .build();
    }

    public GenericResponse updateImageData(String s3Filepath) {
        return new GenericResponse();
    }

    public PageContract.GenericPageResponse updateAnnotatedImageLink(long id, String s3Filepath) {
        var response = pageRepository.updateAnnotatedImageLink(id, s3Filepath);

        if (response < 0) {
            log.error("failed to update annotated link");
            return PageContract.GenericPageResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to get page")
                    .build())
                .build();
        }

        log.info("success to update annotated link");
        return PageContract.GenericPageResponse.builder()
            .success(true)
            .build();
    }

    public PageContract.GenericPageResponse deletePage(long id) {
        var page = pageRepository.deletePage(id);

        if (page < 0) {
            log.error("failed to delete page");
            return PageContract.GenericPageResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to delete page")
                    .build())
                .build();
        }

        log.info("success to delete page");
        return PageContract.GenericPageResponse.builder()
            .success(true)
            .build();
    }

    public PageContract.GenericPageResponse editLocale(Long id, PageContract.EditPageRequest request) {
        var response = pageRepository.editLocale(id, request.getLocale());

        if (response < 0) {
            log.error("failed to edit page");
            return PageContract.GenericPageResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("failed to edit page")
                    .build())
                .build();
        }

        log.info("success to edit page");
        return PageContract.GenericPageResponse.builder()
            .success(true)
            .build();
    }

    public PageContract.GenericPageResponse bulkInsert(InputStream inputStream) {
        try {
            var jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            List<BulkInsertPage> pages = objectMapper.readValue(jsonString, new TypeReference<List<BulkInsertPage>>() {});
            for (BulkInsertPage page : pages) {
                log.info("inserting page: {}", page);
                var locale = objectMapper.writeValueAsString(page.getLocale());
                var pageId = pageRepository.createPages(page.getPageName(), locale, page.getImageUrl());
                if (pageId < 0) {
                    log.error("failed to insert page: {}", page.getPageName());
                    return PageContract.GenericPageResponse.builder()
                        .success(false)
                        .error(GenericError.builder()
                            .code("500")
                            .message("failed to insert page: " + page.getPageName())
                            .build())
                        .build();
                }
                log.info("inserting page success: {}", page.getPageName());
                for (String tag : page.getTags()) {
                    if (!pageTagRepository.tagExists(pageId, tag)) {
                        // Check if the tag exists in the tags table, if not, add it
                        if (!tagRepository.tagExists(tag)) {
                            var tagId = tagRepository.createTag(tag);
                            if (tagId < 0) {
                                log.error("failed to insert tag: {} into tags table", tag);
                                return PageContract.GenericPageResponse.builder()
                                    .success(false)
                                    .error(GenericError.builder()
                                        .code("500")
                                        .message("failed to insert tag: " + tag + " into tags table")
                                        .build())
                                    .build();
                            }
                        }
                        var pageTagId = pageTagRepository.createPageTag(pageId, tag);
                        if (pageTagId < 0) {
                            log.error("failed to insert tag: {} for page: {}", tag, page.getPageName());
                            return PageContract.GenericPageResponse.builder()
                                .success(false)
                                .error(GenericError.builder()
                                    .code("500")
                                    .message("failed to insert tag: " + tag + " for page: " + page.getPageName())
                                    .build())
                                .build();
                        }
                    }
                }
            }
        } catch (Exception e){
            log.error("Exception occurred during bulk insert", e);
            return PageContract.GenericPageResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("Exception occurred during bulk insert: " + e.getMessage())
                    .build())
                    .build();
        }

        log.info("success to bulk insert");
        return PageContract.GenericPageResponse.builder()
            .success(true)
            .build();
    }
}
