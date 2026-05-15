package com.pharmacy.ThaiDuongPharmacyAPI.controller;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.ApiResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.notification.NotificationSummaryResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.OrderRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductReviewRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CustomerRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Order;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductReview;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Admin Notification APIs", description = "Các API thông báo cho Admin")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminNotificationController {

    private final ProductReviewRepository productReviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Operation(summary = "Lấy thống kê thông báo", description = "Lấy số lượng đơn hàng chưa xác nhận và bình luận chưa phản hồi.")
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationSummaryResponse>> getNotificationSummary() {
        
        long unrepliedReviewsCount = productReviewRepository.countByAdminReplyIsNull();
        long unconfirmedOrdersCount = orderRepository.countByStatusIn(Arrays.asList(Order.OrderStatus.PENDING, Order.OrderStatus.PAID));

        List<Order> recentOrders = orderRepository.findTop5ByStatusInOrderByCreatedAtDesc(Arrays.asList(Order.OrderStatus.PENDING, Order.OrderStatus.PAID));
        List<NotificationSummaryResponse.OrderNotification> orderNotifications = recentOrders.stream()
            .map(o -> NotificationSummaryResponse.OrderNotification.builder()
                .id(o.getId())
                .orderCode(o.getOrderCode())
                .customerName(o.getReceiverName())
                .createdAt(o.getCreatedAt())
                .build())
            .toList();

        List<ProductReview> recentReviews = productReviewRepository.findTop5ByAdminReplyIsNullOrderByCreatedAtDesc();
        List<NotificationSummaryResponse.ReviewNotification> reviewNotifications = recentReviews.stream()
            .map(r -> {
                String productName = "Sản phẩm đã bị xóa";
                Product product = productRepository.findById(r.getProductId()).orElse(null);
                if (product != null) {
                    productName = product.getName();
                }

                String userName = "Khách hàng";
                Customer customer = customerRepository.findById(r.getUserId()).orElse(null);
                if (customer != null && customer.getFullName() != null) {
                    userName = customer.getFullName();
                }

                return NotificationSummaryResponse.ReviewNotification.builder()
                        .id(r.getId())
                        .productId(r.getProductId())
                        .productName(productName)
                        .userFullName(userName)
                        .createdAt(r.getCreatedAt())
                        .build();
            })
            .toList();

        NotificationSummaryResponse response = NotificationSummaryResponse.builder()
                .unrepliedReviewsCount(unrepliedReviewsCount)
                .unconfirmedOrdersCount(unconfirmedOrdersCount)
                .totalNotifications(unrepliedReviewsCount + unconfirmedOrdersCount)
                .recentUnconfirmedOrders(orderNotifications)
                .recentUnrepliedReviews(reviewNotifications)
                .build();

        return ApiResponse.success("Lấy thông báo thành công", response);
    }
}
