package com.lokalise.persistence.mapper;

import com.lokalise.model.Tag;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagMapper implements ResultSetMapper<Tag> {

    @Override
    public Tag map(int index, ResultSet res, StatementContext ctx) throws SQLException {
        return Tag.builder()
            .id(res.getLong("id"))
            .name(res.getString("name"))
            .createdAt(res.getTimestamp("created_at"))
            .updatedAt(res.getTimestamp("updated_at"))
            .build();
    }
}
