package com.lokalise.persistence.repository;

import com.lokalise.model.Tag;
import com.lokalise.persistence.mapper.TagMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

@UseStringTemplate3StatementLocator
@RegisterMapper(TagMapper.class)
public interface TagDbInterface {

    @SqlQuery("INSERT INTO tags (name, created_at, updated_at) VALUES (:name, now(), now()) RETURNING id")
    long createTag(@Bind("name") String name);

    @SqlQuery("SELECT * FROM tags")
    List<Tag> getTags();

    @SqlQuery("SELECT EXISTS(SELECT 1 FROM tags WHERE name = :tagName)")
    boolean tagExists(@Bind("tagName") String tagName);
}
