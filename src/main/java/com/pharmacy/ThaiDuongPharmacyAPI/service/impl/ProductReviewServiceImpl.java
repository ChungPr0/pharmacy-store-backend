package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.request.ReviewRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.review.response.ReviewResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductReview;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductReviewRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.ProductReviewService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final com.pharmacy.ThaiDuongPharmacyAPI.repository.OrderRepository orderRepository;
    private final AuthUtils authUtils;

    @Override
    public PageResponse<ReviewResponse> getReviewsByProductId(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductReview> reviewPage = productReviewRepository.findByProductIdAndStatus(productId, "APPROVED", pageable);

        Page<ReviewResponse> responsePage = reviewPage.map(review -> {
            String userFullName = "Khách hàng ẩn danh";
            Customer customer = customerRepository.findById(review.getUserId()).orElse(null);
            if (customer != null && customer.getFullName() != null) {
                userFullName = customer.getFullName();
            }
            
            return ReviewResponse.builder()
                    .id(review.getId())
                    .userId(review.getUserId())
                    .userFullName(userFullName)
                    .productId(review.getProductId())
                    .rating(review.getRating())
                    .comment(review.getComment())
                    .adminReply(review.getAdminReply())
                    .createdAt(review.getCreatedAt())
                    .build();
        });

        return PageResponse.of(responsePage);
    }

    @Override
    public java.util.Map<String, Long> createReview(ReviewRequest request) {
        Long customerId = authUtils.getCurrentCustomerId();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy sản phẩm"));

        boolean hasPurchased = orderRepository.hasCustomerPurchasedProduct(customerId, request.getProductId());
        if (!hasPurchased) {
            throw ApiException.badRequest("Bạn cần mua sản phẩm này và đơn hàng đã được giao thành công trước khi đánh giá.");
        }

        ProductReview review = ProductReview.builder()
                .userId(customerId)
                .productId(product.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .status("APPROVED")
                .build();

        ProductReview savedReview = productReviewRepository.save(review);

        return java.util.Map.of("id", savedReview.getId());
    }

    @Override
    public void replyToReview(Long reviewId, com.pharmacy.ThaiDuongPharmacyAPI.dto.review.request.AdminReviewReplyRequest request) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy bình luận"));

        review.setAdminReply(request.getReplyMessage());
        productReviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy bình luận"));
        productReviewRepository.delete(review);
    }
}
