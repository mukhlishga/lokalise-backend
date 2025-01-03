package com.lokalise.controller;

import com.lokalise.contract.GenericResponse;
import com.lokalise.contract.TagContract;
import com.lokalise.service.TagService;
import com.lokalise.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

@Slf4j
@AllArgsConstructor
public class TagController {
    private final TagService tagService;

    public GenericResponse createTag(Request request, Response response) {
        var createRequest = JsonUtil.toGson(request.body(), TagContract.CreateTagRequest.class);

        log.info("create tag {}", createRequest.getName());
        var createResponse = tagService.create(createRequest);

        if (!createResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(createResponse.getError());
        }

        return new GenericResponse();
    }

    public GenericResponse getTags(Request request, Response response) {
        log.info("get tags");
        var getTagsResponse = tagService.getTags();

        if (!getTagsResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(getTagsResponse.getError());
        }

        return new GenericResponse(getTagsResponse.getData());
    }
}
