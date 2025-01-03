package com.lokalise.persistence.repository;

import com.lokalise.model.Locale;
import com.lokalise.model.Page;
import com.lokalise.persistence.mapper.LocaleMapper;
import com.lokalise.persistence.mapper.PageMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
@RegisterMapper({PageMapper.class})
public interface PageDbInterface {

    @SqlQuery("INSERT INTO pages (name, locale, image_link, created_at, updated_at) VALUES (:name, :locale::jsonb, :image_link, now(), now()) RETURNING id")
    long createPage(@Bind("name") String pagename, @Bind("locale") String locale, @Bind("image_link") String presignUrl);

    @SqlQuery("SELECT pages.id, pages.name, pages.locale, pages.image_link, pages.annotated_image_link, pages.created_at, pages.updated_at, array_agg(tags.name)::text[] as tags " +
        "FROM pages " +
        "INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
        "INNER JOIN tags ON pages_tags.tag_id = tags.id " +
        "GROUP BY pages.id " +
        "ORDER BY pages.id DESC")
    List<Page> getPages();

    @SqlQuery(
        "SELECT pages.id, pages.name, pages.locale, pages.image_link, pages.annotated_image_link, pages.created_at, pages.updated_at, array_agg(tags.name)::text[] AS tags " +
            "FROM pages " +
            "INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
            "INNER JOIN tags ON pages_tags.tag_id = tags.id " +
            "WHERE pages.id IN ( " +
            "    SELECT pages.id " +
            "    FROM pages " +
            "    INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
            "    INNER JOIN tags ON pages_tags.tag_id = tags.id " +
            "    WHERE tags.name IN (<tag_names>) " +
            "    GROUP BY pages.id " +
            ") " +
            "GROUP BY pages.id " +
            "ORDER BY pages.id DESC"
    )
    List<Page> getPagesByTags(@BindIn("tag_names") List<String> tagNames);

    @SqlQuery("SELECT pages.id, pages.name, pages.locale, pages.image_link, pages.annotated_image_link, pages.created_at, pages.updated_at, array_agg(tags.name)::text[] as tags " +
        "FROM pages " +
        "INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
        "INNER JOIN tags ON pages_tags.tag_id = tags.id " +
        "WHERE pages.name ILIKE :name_pattern " +
        "GROUP BY pages.id " +
        "ORDER BY pages.id DESC")
    List<Page> getPagesByName(@Bind("name_pattern") String namePattern);

    @SqlQuery("SELECT pages.id, pages.name, pages.locale, pages.image_link, pages.annotated_image_link, pages.created_at, pages.updated_at, array_agg(tags.name)::text[] as tags " +
        "FROM pages " +
        "INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
        "INNER JOIN tags ON pages_tags.tag_id = tags.id " +
        "WHERE pages.id IN ( " +
        "    SELECT pages.id " +
        "    FROM pages " +
        "    INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
        "    INNER JOIN tags ON pages_tags.tag_id = tags.id " +
        "    WHERE tags.name IN (<tag_names>) " +
        "    GROUP BY pages.id " +
        ") " +
        "AND pages.name ILIKE :name_pattern " +
        "GROUP BY pages.id " +
        "ORDER BY pages.id DESC")
    List<Page> getPagesByTagsAndName(@BindIn("tag_names") List<String> tagNames, @Bind("name_pattern") String namePattern);

    @SqlQuery("SELECT pages.id, pages.name, pages.locale, pages.image_link, pages.annotated_image_link, pages.created_at, pages.updated_at, array_agg(tags.name)::text[] as tags " +
        "FROM pages " +
        "INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
        "INNER JOIN tags ON pages_tags.tag_id = tags.id " +
        "WHERE pages.id = :id " +
        "GROUP BY pages.id ")
    Page getPage(@Bind("id") long id);

    @SqlQuery("UPDATE pages SET annotated_image_link = :link WHERE id = :id RETURNING id")
    Long updateAnnotatedImageLink(@Bind("id") long id, @Bind("link") String s3Filepath);

    @SqlQuery("DELETE FROM pages WHERE id = :id RETURNING id")
    long deletePage(@Bind("id") long id);

    @SqlQuery("UPDATE pages SET locale = :locale::jsonb, updated_at = now() WHERE id = :id RETURNING id;")
    Long updateLocaleByPageId(@Bind("id") Long id, @Bind("locale") String locale);

    @SqlQuery("SELECT pages.locale " +
        "FROM pages " +
        "INNER JOIN pages_tags ON pages.id = pages_tags.page_id " +
        "INNER JOIN tags ON pages_tags.tag_id = tags.id " +
        "WHERE tags.name IN (<tags>) " +
        "GROUP BY pages.id " +
        "ORDER BY pages.id DESC")
    @RegisterMapper(LocaleMapper.class)
    List<List<Locale>> getLocaleByTags(@BindIn("tags") List<String> tags);
}
