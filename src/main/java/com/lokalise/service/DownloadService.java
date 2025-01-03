package com.lokalise.service;

import com.lokalise.contract.DownloadContract;
import com.lokalise.contract.GenericError;
import com.lokalise.model.Locale;
import com.lokalise.persistence.repository.PageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class DownloadService {
    private final PageRepository pageRepository;

    public DownloadContract.DownloadResponse downloadLocale(List<String> tags) {
        if (tags.isEmpty()) {
            log.error("tags is empty");
            return DownloadContract.DownloadResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("tags is mandatory field")
                    .build())
                .build();
        }

        var nestedLocales = pageRepository.getLocaleByTags(tags);
        if (nestedLocales.isEmpty()) {
            log.error("no locale found by tags");
            return DownloadContract.DownloadResponse.builder()
                .success(false)
                .error(GenericError.builder()
                    .code("500")
                    .message("no locale found by tags")
                    .build())
                .build();
        }

        List<Locale> flattenedLocales = nestedLocales.stream()
            .flatMap(List::stream) // Flatten the nested lists
            .collect(Collectors.toList());

        return DownloadContract.DownloadResponse.builder()
            .success(true)
            .data(flattenedLocales)
            .build();
    }
}
