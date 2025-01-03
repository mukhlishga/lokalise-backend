package com.lokalise;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.lokalise.config.ApplicationConfiguration;
import com.lokalise.controller.DownloadController;
import com.lokalise.controller.PageController;
import com.lokalise.controller.TagController;
import com.lokalise.controller.UploadController;
import com.lokalise.util.CorsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static spark.Spark.*;

@Slf4j
@AllArgsConstructor
public class Server {
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";
    private final ApplicationConfiguration configuration;
    private final PageController pageController;
    private final TagController tagController;
    private final UploadController uploadController;
    private final DownloadController downloadController;

    public void startServer() {
        port(configuration.getValueAsInt("APP_PORT"));

        var corsUtils = new CorsUtil(configuration);
        corsUtils.apply();

        var gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

        before("/*", (request, response) -> response.type(CONTENT_TYPE));

        get("/", (req, res) -> "Hello World");
        get("/ping", (req, res) -> "pong");

        path("/v1", () -> {
            path("/page", () -> {
                post("", pageController::createPage, gson::toJson);
                get("", pageController::getPages, gson::toJson);
                get("/:id", pageController::getPage, gson::toJson);
                put("/:id/locale", pageController::editLocale, gson::toJson);
                delete("/:id", pageController::deletePage, gson::toJson);
                post("/save-annotated-image", pageController::addAnnotatedImage, gson::toJson);
                post("/bulk-insert", pageController::bulkInsert, gson::toJson);
            });
            path("/tag", () -> {
                post("", tagController::createTag, gson::toJson);
                get("", tagController::getTags, gson::toJson);
            });
            path("/upload", () -> {
                post("", uploadController::file, gson::toJson);
            });
            path("/download", () -> {
                post("", downloadController::downloadLocale, gson::toJson);
            });
        });

        log.info("Service is started");
    }

    public void stopServer() {
        stop();
    }
}
