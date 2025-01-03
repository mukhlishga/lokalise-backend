package com.lokalise.persistence.repository;

import com.lokalise.config.ApplicationConfiguration;
import com.lokalise.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import java.util.List;

@Slf4j
public class TagRepository extends Repository<TagDbInterface> {

    public TagRepository(ApplicationConfiguration config) {
        super(config);
    }

    public long createTag(String name) {
        return withDBInterface(TagDbInterface.class, repository -> repository.createTag(name));
    }

    public List<Tag> getTags() {
        try {
            return withDBInterface(TagDbInterface.class, repository -> repository.getTags());
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return null;
        }
    }

    public boolean tagExists(String tagName) {
        try {
            return withDBInterface(TagDbInterface.class, repository -> repository.tagExists(tagName));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return false;
        }
    }
}
