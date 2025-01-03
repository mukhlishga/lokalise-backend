package com.lokalise.persistence.repository;

import com.lokalise.config.ApplicationConfiguration;
import com.lokalise.contract.PageContract;
import com.lokalise.model.Locale;
import com.lokalise.model.Page;
import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PageRepository extends Repository<PageDbInterface> {

    public PageRepository(ApplicationConfiguration config) {
        super(config);
    }

    public long createPages(String pagename, String locale, String presignUrl) {
        try {
            return withDBInterface(PageDbInterface.class, repository -> repository.createPage(pagename, locale, presignUrl));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return -1;
        }
    }

    public List<Page> getPages() {
        try {
            return withDBInterface(PageDbInterface.class, repository -> repository.getPages());
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return null;
        }
    }

    public List<Page> getPagesByTags(PageContract.GetPageRequest request) {
        try {
            List<String> tags = Arrays.asList(request.getTags().split(","));
            return withDBInterface(PageDbInterface.class, repository -> repository.getPagesByTags(tags));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return null;
        }
    }

    public List<Page> getPagesByName(PageContract.GetPageRequest request) {
        try {
            String namePattern = "%" + request.getName() + "%";
            return withDBInterface(PageDbInterface.class, repository -> repository.getPagesByName(namePattern));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return null;
        }
    }

    public List<Page> getPagesByTagsAndName(PageContract.GetPageRequest request) {
        try {
            List<String> tags = Arrays.asList(request.getTags().split(","));
            String namePattern = "%" + request.getName() + "%";
            return withDBInterface(PageDbInterface.class, repository -> repository.getPagesByTagsAndName(tags, namePattern));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return null;
        }
    }

    public Page getPage(String id) {
        try {
            return withDBInterface(PageDbInterface.class, repository -> repository.getPage(Long.parseLong(id)));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return null;
        }
    }

    public long updateAnnotatedImageLink(long id, String s3Filepath) {
        try {
            return withDBInterface(PageDbInterface.class, repository -> repository.updateAnnotatedImageLink(id, s3Filepath));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return -1;
        }
    }

    public long deletePage(long id) {
        try {
            return withDBInterface(PageDbInterface.class, repository -> repository.deletePage(id));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return 0;
        }
    }

    public List<List<Locale>> getLocaleByTags(List<String> tags) {
        try {
            return withDBInterface(PageDbInterface.class, repository -> repository.getLocaleByTags(tags));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public long editLocale(Long id, String locale) {
        try {
            return withDBInterface(PageDbInterface.class, repository -> repository.updateLocaleByPageId(id, locale));
        } catch (UnableToExecuteStatementException e) {
            log.error("Unable to execute statement exception : {}", e.getMessage());
            return -1;
        }
    }
}
