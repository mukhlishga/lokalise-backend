package com.lokalise.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokalise.model.Locale;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LocaleMapper implements ResultSetMapper<List<Locale>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Locale> map(int index, ResultSet res, StatementContext ctx) throws SQLException {
        String localeJson = res.getString("locale");
        try {
            return objectMapper.readValue(localeJson, new TypeReference<List<Locale>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize JSON: " + localeJson, e);
        }
    }
}