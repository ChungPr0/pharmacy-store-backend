package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.CheckoutRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.OrderSearchRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.OrderStatusUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderAdminResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderHistoryResponse;

import java.util.List;

public interface OrderService {

    void checkout(CheckoutRequest request);

    List<OrderHistoryResponse> getMyOrders();

    OrderDetailResponse getMyOrderDetail(String orderCode);

    PageResponse<OrderAdminResponse> searchOrders(OrderSearchRequest request);

    OrderDetailResponse getAdminOrderDetail(Long id);

    void updateOrderStatus(Long id, OrderStatusUpdateRequest request);
}
