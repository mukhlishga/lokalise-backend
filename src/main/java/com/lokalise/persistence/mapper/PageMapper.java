package com.lokalise.persistence.mapper;

import com.lokalise.model.Page;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PageMapper implements ResultSetMapper<Page> {

    @Override
    public Page map(int index, ResultSet res, StatementContext ctx) throws SQLException {
        return Page.builder()
            .id(res.getLong("id"))
            .name(res.getString("name"))
            .locale(res.getString("locale"))
            .imageLink(res.getString("image_link"))
            .annotatedImageLink(res.getString("annotated_image_link"))
            .createdAt(res.getTimestamp("created_at"))
            .updatedAt(res.getTimestamp("updated_at"))
            .tags(List.of(res.getArray("tags") != null ? (String[]) res.getArray("tags").getArray() : new String[0]))
            .build();
    }
}