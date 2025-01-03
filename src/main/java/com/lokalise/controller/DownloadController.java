package com.lokalise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokalise.contract.GenericResponse;
import com.lokalise.service.DownloadService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class DownloadController {
    private final DownloadService downloadService;

    public GenericResponse downloadLocale(Request request, Response response) throws ServletException, IOException {
        log.info("download locales by tags");
        if (!request.raw().getContentType().equals("application/json")) {
            throw new ServletException("Content-Type != application/json");
        }
        var jsonBody = request.body();
        var jsonNode = new ObjectMapper().readTree(jsonBody);
        var tag = jsonNode.get("tags");
        boolean checker = tag.isNull() || StringUtils.isEmpty(tag.asText());
        List<String> tags = checker ? new ArrayList<>() : Arrays.asList(tag.asText().split(","));
        var downloadResponse = downloadService.downloadLocale(tags);

        if (!downloadResponse.isSuccess()) {
            log.error("failed to download locale");
            response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return new GenericResponse(downloadResponse.getError());
        }

        log.info("success to download locales");
        return GenericResponse.builder()
            .success(true)
            .data(downloadResponse.getData())
            .build();
    }
}
