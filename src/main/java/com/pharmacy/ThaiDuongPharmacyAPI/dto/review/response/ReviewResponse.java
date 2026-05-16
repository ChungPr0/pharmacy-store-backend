package com.pharmacy.ThaiDuongPharmacyAPI.dto.review.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long productId;
    private Integer rating;
    private String comment;
    private String adminReply;
    private LocalDateTime createdAt;
}
