package com.pharmacy.ThaiDuongPharmacyAPI.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSummaryResponse {
    private long unrepliedReviewsCount;
    private long unconfirmedOrdersCount;
    private long totalNotifications;
    private List<OrderNotification> recentUnconfirmedOrders;
    private List<ReviewNotification> recentUnrepliedReviews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderNotification {
        private Long id;
        private String orderCode;
        private String customerName;
        private java.time.LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewNotification {
        private Long id;
        private Long productId;
        private String productName;
        private String userFullName;
        private java.time.LocalDateTime createdAt;
    }
}
