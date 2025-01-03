package com.lokalise.util;

import com.lokalise.config.ApplicationConfiguration;
import org.apache.commons.lang3.ArrayUtils;

import static spark.Spark.before;
import static spark.Spark.options;

public class CorsUtil {
    private final ApplicationConfiguration applicationConfiguration;

    public CorsUtil(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public void apply() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            String[] origins = applicationConfiguration.getValueAsString("CORS_ALLOW_ORIGIN").split(",");
            if (ArrayUtils.contains(origins, request.headers("Origin"))) {
                response.header("Access-Control-Allow-Origin", request.headers("Origin"));
            }
            response.header("Access-Control-Allow-Headers", applicationConfiguration.getValueAsString("CORS_ALLOW_HEADERS"));
            response.header("Access-Control-Allow-Credentials", applicationConfiguration.getValueAsString("CORS_ALLOW_CREDENTIALS"));
            response.header("Access-Control-Request-Method", applicationConfiguration.getValueAsString("CORS_REQUEST_METHOD"));
        });
    }
}
