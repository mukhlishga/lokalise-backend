package com.lokalise.controller;

import com.lokalise.client.S3Client;
import com.lokalise.contract.GenericError;
import com.lokalise.contract.GenericResponse;
import com.lokalise.contract.PageContract;
import com.lokalise.service.PageService;
import com.lokalise.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class PageController {
    private final PageService pageService;
    private final S3Client s3Client;

    public GenericResponse createPage(Request request, Response response) throws ServletException, IOException {
        log.info("create page");
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp/"));
        var part = request.raw().getPart("file");
        var pagename = request.raw().getParameter("pagename");

        var presignUrl = "";
        try (var inputStream = part.getInputStream()) {
            log.info("Construct filename and s3filepath");
            var s3Filepath = constructS3Filepath(pagename, true);
            presignUrl = s3Client.uploadFile(inputStream, s3Filepath);
            log.info("Presign url: {}", presignUrl);
            if (StringUtils.isBlank(presignUrl)) {
                log.error("Failed to save object to s3");
                response.status(org.apache.http.HttpStatus.SC_BAD_REQUEST);
                return new GenericResponse(
                    GenericError.builder()
                        .code("500")
                        .message("failed to save object to s3")
                        .build()
                );
            }
        }

        String locale = request.raw().getParameter("locale");
        List<String> tags = Arrays.asList(request.raw().getParameter("tags").split(","));
        var createResponse = pageService.create(pagename, locale, tags, presignUrl);

        if (!createResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(createResponse.getError());
        }

        return new GenericResponse();
    }

    public GenericResponse bulkInsert(Request request, Response response) throws ServletException, IOException {
        log.info("bulk insert pages");
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp/"));
        var part = request.raw().getPart("file");

        try (var inputStream = part.getInputStream()) {
            var bulkInsertResponse = pageService.bulkInsert(inputStream);
            if (!bulkInsertResponse.isSuccess()) {
                response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                return new GenericResponse(
                    GenericError.builder()
                        .code("500")
                        .message("failed to bulk insert pages")
                        .build()
                );
            }
        }

        return new GenericResponse();
    }

    public GenericResponse getPages(Request request, Response response) {
        var getPagesRequest = JsonUtil.toGson(request.queryString(), PageContract.GetPageRequest.class);

        log.info("get pages with name {} and tags {}", getPagesRequest.getName(), getPagesRequest.getTags());
        var getPagesResponse = pageService.getPages(getPagesRequest);

        if (!getPagesResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(getPagesResponse.getError());
        }

        return new GenericResponse(getPagesResponse.getData());
    }

    public GenericResponse getPage(Request request, Response response) {
        var id = request.params("id");

        log.info("get page with id {}", id);
        var getPageResponse = pageService.getPage(id);

        if (!getPageResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(getPageResponse.getError());
        }

        return new GenericResponse(getPageResponse.getData());
    }

    public GenericResponse addAnnotatedImage(Request request, Response response) throws ServletException, IOException {
        log.info("add annotated image");
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp/"));
        var part = request.raw().getPart("file");
        var pagename = request.raw().getParameter("pagename");

        var presignUrl = "";
        try (var inputStream = part.getInputStream()) {
            log.info("Construct filename and s3filepath");
            var s3Filepath = constructS3Filepath(pagename, false);
            presignUrl = s3Client.uploadFile(inputStream, s3Filepath);
            log.info("Presign url: {}", presignUrl);
            if (StringUtils.isBlank(presignUrl)) {
                log.error("Failed to save object to s3");
                response.status(org.apache.http.HttpStatus.SC_BAD_REQUEST);
                return new GenericResponse(
                    GenericError.builder()
                        .code("500")
                        .message("failed to save object to s3")
                        .build()
                );
            }
        }

        var id = request.raw().getParameter("id");
        var createResponse = pageService.updateAnnotatedImageLink(Long.parseLong(id), presignUrl);

        if (!createResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(createResponse.getError());
        }

        return new GenericResponse();
    }

    public GenericResponse editLocale(Request request, Response response) {
        var editRequest = JsonUtil.toGson(request.body(), PageContract.EditPageRequest.class);
        var id = Long.parseLong(request.params("id"));

        log.info("edit page with id {}", id);
        var editResponse = pageService.editLocale(id, editRequest);

        if (!editResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(editResponse.getError());
        }

        return new GenericResponse();
    }

    public GenericResponse deletePage(Request request, Response response) {
        var id = Long.parseLong(request.params("id"));

        log.info("delete page with id {}", id);
        var deletePageResponse = pageService.deletePage(id);

        if (!deletePageResponse.isSuccess()) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return new GenericResponse(deletePageResponse.getError());
        }

        return new GenericResponse();
    }

    private String constructS3Filepath(String pagename, boolean isOriginal) {
        return String.format("ID/hackathon/%s/%s", isOriginal ? "original" : "annotated", pagename);
    }
}
