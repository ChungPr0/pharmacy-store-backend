package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.request.ReviewRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.response.ReviewResponse;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.request.AdminReviewReplyRequest;
import java.util.Map;

public interface ProductReviewService {

    PageResponse<ReviewResponse> getReviewsByProductId(Long productId, int page, int size);

    Map<String, Long> createReview(ReviewRequest request);

    void replyToReview(Long reviewId, AdminReviewReplyRequest request);

    void deleteReview(Long reviewId);

}
