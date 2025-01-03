package com.lokalise.contract;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericError {
    private String code;
    private String message;
}
