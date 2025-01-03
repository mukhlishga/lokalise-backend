package com.lokalise.controller;

import com.lokalise.client.S3Client;
import com.lokalise.contract.GenericError;
import com.lokalise.contract.GenericResponse;
import com.lokalise.service.PageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class UploadController {
    private final S3Client s3Client;
    private final PageService pageService;

    public GenericResponse file(Request request, Response response) throws ServletException, IOException {
        log.info("upload file");

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp/"));
        var part = request.raw().getPart("file");
        var pagename = request.raw().getParameter("pagename");

        try (var inputStream = part.getInputStream()) {
            log.info("Construct filename and s3filepath");
            var s3Filepath = constructS3Filepath(pagename,true);
            var presignUrl = s3Client.uploadFile(inputStream, s3Filepath);
            log.info("Presign url: {}", presignUrl);
            if (StringUtils.isBlank(presignUrl)) {
                log.error("Failed to save object to s3");
                response.status(HttpStatus.SC_BAD_REQUEST);
                return new GenericResponse(
                    GenericError.builder()
                        .code("500")
                        .message("failed to save object to s3")
                        .build()
                );
            }

            var updateResponse = pageService.updateImageData(s3Filepath);
            if (!updateResponse.isSuccess()) {
                response.status(HttpStatus.SC_BAD_REQUEST);
                return new GenericResponse(updateResponse.getError());
            }

            return new GenericResponse();
        }
    }

    private String constructS3Filepath(String pagename, boolean isOriginal) {
        return String.format("ID/hackathon/%s/%s", isOriginal ? "original" : "annotated", pagename);
    }
}
