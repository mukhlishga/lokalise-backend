package com.lokalise.persistence.repository;

import com.lokalise.config.ApplicationConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

@Slf4j
public class PageTagRepository extends Repository<PageTagDbInterface> {

    public PageTagRepository(ApplicationConfiguration config) {
        super(config);
    }

    public long createPageTag(long pageId, String tagName) {
        try {
            return withDBInterface(PageTagDbInterface.class, repository -> repository.createPageTag(pageId, tagName));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return -1;
        }
    }

    public boolean tagExists(long pageId, String tagName) {
        try {
            return withDBInterface(PageTagDbInterface.class, repository -> repository.tagExists(pageId, tagName));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return false;
        }
    }
}
