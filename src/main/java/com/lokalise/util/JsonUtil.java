package com.lokalise.util;

import com.google.gson.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

public final class JsonUtil {
    private static final Gson GSON = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setLenient()
        .create();

    private JsonUtil() {
    }

    public static <T> T toGson(String object, Class<T> classOfT) {
        try {
            JsonParser.parseString(object);
            return GSON.fromJson(object, classOfT);
        } catch (Exception e) {
            List<NameValuePair> params = URLEncodedUtils.parse(object, StandardCharsets.UTF_8);
            JsonObject json = new JsonObject();
            for (final NameValuePair param : params) {
                json.addProperty(param.getName(), param.getValue());
            }
            return GSON.fromJson(json.toString(), classOfT);
        }
    }

    public static Gson getInstance() {
        return GSON;
    }
}
