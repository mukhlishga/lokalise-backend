package com.lokalise.config;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

@Slf4j
public class ApplicationConfiguration {
    private static Map<String, Object> configuration;

    public ApplicationConfiguration() {
        try {
            configure();
        } catch (FileNotFoundException e) {
            log.error("Failed to load configuration", e);
        }
    }

    private Map<String, Object> configure() throws FileNotFoundException {
        URL resource = getClass().getResource("/application.yml");
        Yaml yaml = new Yaml();
        try (InputStream inputStream = resource.openStream()) {
            configuration = yaml.load(inputStream);
        } catch (IOException e) {
            log.error("IO Exception", e);
        }
        return configuration;
    }

    public Object getValue(String configurationName) {
        return configuration.get(configurationName);
    }

    public String getValueAsString(String name) {
        Object value = getValue(name);
        if (value == null) {
            log.warn(format("Config with key was null: %s", name));
            return "";
        }
        else {
            return value.toString();
        }
    }

    public String getValueAsString(String name, String defaultValue) {
        Object value = getValue(name);
        return value != null? getValueAsString(name) : defaultValue;
    }

    public Integer getValueAsInt(String name) {
        String value = this.getValueAsString(name);
        return Objects.equals(value, "") ? 0 : Integer.valueOf(value);
    }

    public Integer getValueAsInt(String name, int defaultValue) {
        Object value = getValue(name);
        return value != null? getValueAsInt(name) : defaultValue;
    }

    public Double getValueAsDouble(String name) {
        String value = this.getValueAsString(name);
        return Objects.equals(value, "") ? 0 : Double.valueOf( value);
    }

    public Double getValueAsDouble(String name, double defaultValue) {
        Object value = getValue(name);
        return value != null? getValueAsDouble(name) : defaultValue;
    }

    public Long getValueAsLong(String name) {
        String value = this.getValueAsString(name);
        return Objects.equals(value, "") ? 0 : Long.decode(value);
    }

    public Long getValueAsLong(String name, long defaultValue) {
        Object value = getValue(name);
        return value != null? getValueAsLong(name) : defaultValue;
    }

    public Float getValueAsFloat(String name) {
        String value = this.getValueAsString(name);
        return Objects.equals(value, "") ? 0 : Float.parseFloat(value);
    }

    public Float getValueAsFloat(String name, float defaultValue) {
        Object value = getValue(name);
        return value != null? getValueAsFloat(name) : defaultValue;
    }

    public boolean getValueAsBoolean(String name) {
        String value = this.getValueAsString(name);
        return !Objects.equals(value, "") && Boolean.parseBoolean(value);
    }

    public boolean getValueAsBoolean(String name, boolean defaultValue) {
        Object value = getValue(name);
        return value != null? getValueAsBoolean(name) : defaultValue;
    }
}
