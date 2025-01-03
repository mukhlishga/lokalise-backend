package com.lokalise.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GenericResponse {

    private boolean success;
    private GenericError error;
    private Object data;

    public GenericResponse() {
        this.success = true;
    }

    public GenericResponse(Object data) {
        this.success = true;
        this.data = data;
    }

    public GenericResponse(GenericError error) {
        this.success = false;
        this.error = error;
    }
}
