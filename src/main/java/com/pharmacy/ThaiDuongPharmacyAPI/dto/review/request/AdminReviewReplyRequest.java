package com.pharmacy.ThaiDuongPharmacyAPI.dto.review.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminReviewReplyRequest {

    @NotBlank(message = "Nội dung trả lời không được để trống")
    private String replyMessage;
}
