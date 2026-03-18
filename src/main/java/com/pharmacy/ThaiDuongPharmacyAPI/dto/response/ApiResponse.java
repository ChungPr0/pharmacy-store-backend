package com.pharmacy.ThaiDuongPharmacyAPI.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), message, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(HttpStatus.CREATED.value(), message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(int status, String message, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(int status, String message) {
        return ResponseEntity.status(status).body(new ApiResponse<>(status, message, null));
    }
}
