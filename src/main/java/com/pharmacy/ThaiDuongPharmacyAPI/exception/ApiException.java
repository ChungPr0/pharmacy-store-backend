package com.pharmacy.ThaiDuongPharmacyAPI.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final int status;
    private final Object data;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
        this.data = null;
    }

    public ApiException(int status, String message, Object data) {
        super(message);
        this.status = status;
        this.data = data;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST.value(), message);
    }

    public static ApiException badRequest(String message, Object data) {
        return new ApiException(HttpStatus.BAD_REQUEST.value(), message, data);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED.value(), message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN.value(), message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND.value(), message);
    }

    public static ApiException internalServerError(String message) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}
