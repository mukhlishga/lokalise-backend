package com.lokalise.persistence.repository;

import com.lokalise.persistence.mapper.PageTagMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@UseStringTemplate3StatementLocator
@RegisterMapper(PageTagMapper.class)
public interface PageTagDbInterface {

    @SqlQuery("WITH temp AS (SELECT id FROM tags where name = :tag_name) " +
            "INSERT INTO pages_tags (page_id, tag_id, created_at, updated_at) VALUES (:page_id, (SELECT id FROM temp), now(), now()) RETURNING id")
    long createPageTag(@Bind("page_id") long pageId, @Bind("tag_name") String tagName);

    @SqlQuery("SELECT EXISTS(SELECT 1 FROM pages_tags WHERE page_id = :pageId AND tag_id = (SELECT id FROM tags WHERE name = :tagName))")
    boolean tagExists(@Bind("pageId") long pageId, @Bind("tagName") String tagName);
}