package com.lokalise.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.lokalise.config.ApplicationConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class S3Client {

    private final ApplicationConfiguration configuration;
    private final AmazonS3 amazonS3;

    public String uploadFile(InputStream content, String filepath) {
        var putObjectRequest = new PutObjectRequest(
            configuration.getValueAsString("AWS_S3_BUCKET"),
            filepath,
            content,
            new ObjectMetadata()
        );

        log.info("Put object {} to S3", filepath);
        var transferManager = constructTransferManager();
        var upload = transferManager.upload(putObjectRequest);
        var checksum = "";

        try {
            var uploadResult = upload.waitForUploadResult();

            log.info("Success put object {} to S3 with checksum {}", filepath, uploadResult.getETag());
            checksum = uploadResult.getETag();
            if (!Objects.equals(checksum, "")) {
                return generatePresignedUrl(filepath, configuration.getValueAsInt("AWS_S3_PRESIGN_URL_EXPIRATION_IN_DAYS", 5));
            }
        } catch (InterruptedException e) {
            log.error("Failed put object {} to S3: {}", filepath, e.getMessage());
            Thread.currentThread().interrupt();
        } catch (AmazonClientException e) {
            log.error("Failed put object {} to S3: {}", filepath, e.getMessage());
        }

        transferManager.shutdownNow();
        return null;
    }

    private TransferManager constructTransferManager() {
        log.info("Transfer Manager builder");
        return TransferManagerBuilder
            .standard()
            .withS3Client(amazonS3)
            .build();
    }

    public String generatePresignedUrl(String filepath, int expirationInDays) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60 * 24 * expirationInDays;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
            new GeneratePresignedUrlRequest(configuration.getValueAsString("AWS_S3_BUCKET"), filepath)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }
}
