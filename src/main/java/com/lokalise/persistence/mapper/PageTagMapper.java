package com.lokalise.persistence.mapper;

import com.lokalise.model.PageTag;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PageTagMapper implements ResultSetMapper<PageTag> {

    @Override
    public PageTag map(int index, ResultSet res, StatementContext ctx) throws SQLException {
        return PageTag.builder()
            .id(res.getLong("id"))
            .pageId(res.getLong("page_id"))
            .tagId(res.getLong("tag_id"))
            .createdAt(res.getTimestamp("created_at"))
            .updatedAt(res.getTimestamp("updated_at"))
            .build();
    }
}
