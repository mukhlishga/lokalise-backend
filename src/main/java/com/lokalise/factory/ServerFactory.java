package com.lokalise.factory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.lokalise.Server;
import com.lokalise.client.S3Client;
import com.lokalise.config.ApplicationConfiguration;
import com.lokalise.controller.DownloadController;
import com.lokalise.controller.PageController;
import com.lokalise.controller.TagController;
import com.lokalise.controller.UploadController;
import com.lokalise.persistence.repository.PageRepository;
import com.lokalise.persistence.repository.PageTagRepository;
import com.lokalise.persistence.repository.TagRepository;
import com.lokalise.service.DownloadService;
import com.lokalise.service.PageService;
import com.lokalise.service.TagService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerFactory {

    public static Server createServer() {
        log.info("Setting up server");
        var configuration = new ApplicationConfiguration();

        var s3Client = new S3Client(configuration, buildAmazonS3ClientBuilder(configuration));

        var pageRepository = new PageRepository(configuration);
        var tagRepository = new TagRepository(configuration);
        var pageTagRepository = new PageTagRepository(configuration);

        var pageService = new PageService(pageRepository, pageTagRepository, tagRepository);
        var tagService = new TagService(tagRepository);
        var downloadService = new DownloadService(pageRepository);

        var pageController = new PageController(pageService, s3Client);
        var tagController = new TagController(tagService);
        var uploadController = new UploadController(s3Client, pageService);
        var downloadController = new DownloadController(downloadService);

        return new Server(configuration, pageController, tagController, uploadController, downloadController);
    }

    private static AmazonS3 buildAmazonS3ClientBuilder(ApplicationConfiguration configuration) {
        return AmazonS3ClientBuilder.standard()
            .withRegion(configuration.getValueAsString("AWS_REGION"))
            .withCredentials(
                new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(
                        configuration.getValueAsString("AWS_ACCESS_KEY"),
                        configuration.getValueAsString("AWS_SECRET_KEY")
                    )
                )
            ).build();
    }
}
