package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.CheckoutRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.OrderSearchRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.OrderStatusUpdateRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderAdminResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderDetailResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.response.OrderHistoryResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.*;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.BadRequestException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ForbiddenException;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ResourceNotFoundException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.*;
import com.pharmacy.ThaiDuongPharmacyAPI.service.OrderService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AuthUtils authUtils;
    private final CartItemRepository cartItemRepository;
    private final ProductBatchRepository productBatchRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void checkout(CheckoutRequest request) {
        Customer currentCustomer = authUtils.getCurrentCustomer();

        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());

        if (cartItems.isEmpty() || cartItems.size() != request.getCartItemIds().size()) {
            throw new ApiException(400, "Một hoặc nhiều sản phẩm trong giỏ hàng không hợp lệ.");
        }

        for (CartItem item : cartItems) {
            if (!item.getCart().getCustomer().getId().equals(currentCustomer.getId())) {
                throw new ApiException(403, "Bạn không có quyền thanh toán sản phẩm này.");
            }
        }

        Order order = new Order();
        order.setCustomer(currentCustomer);
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setShippingAddressText(request.getShippingAddressText());
        order.setReceiverName(request.getReceiverName());
        order.setPhoneNumber(request.getPhone());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentToken(request.getPaymentToken());

        BigDecimal grandTotal = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getIsActive() == null || !product.getIsActive()) {
                throw new ApiException(400, "Sản phẩm " + product.getName() + " đã ngừng kinh doanh.");
            }

            int requiredQuantity = cartItem.getQuantity();

            List<ProductBatch> batches = productBatchRepository.findAvailableBatchesByProductIdOrderByExpiryDateAsc(product.getId());

            int availableStock = batches.stream().mapToInt(ProductBatch::getStockQuantity).sum();

            if (availableStock < requiredQuantity) {
                throw new ApiException(400, "Sản phẩm " + product.getName() + " không đủ số lượng trong kho.");
            }

            int remainingToDeduct = requiredQuantity;
            for (ProductBatch batch : batches) {
                if (remainingToDeduct <= 0) break;

                int deductAmount = Math.min(batch.getStockQuantity(), remainingToDeduct);
                batch.setStockQuantity(batch.getStockQuantity() - deductAmount);
                remainingToDeduct -= deductAmount;
            }

            BigDecimal currentPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requiredQuantity);
            orderItem.setPrice(currentPrice);

            order.getItems().add(orderItem);

            BigDecimal itemTotal = currentPrice.multiply(BigDecimal.valueOf(requiredQuantity));
            grandTotal = grandTotal.add(itemTotal);
        }

        order.setTotalAmount(grandTotal);

        orderRepository.save(order);

        cartItemRepository.deleteAllByIdInBatch(request.getCartItemIds());
    }

    // --- Customer APIs ---

    @Override
    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getMyOrders() {
        Long customerId = authUtils.getCurrentCustomerId();
        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        return orders.stream()
                .map(OrderHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getMyOrderDetail(String orderCode) {
        Long customerId = authUtils.getCurrentCustomerId();
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + orderCode));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("Bạn không có quyền xem thông tin đơn hàng này");
        }

        return OrderDetailResponse.fromEntity(order);
    }

    // --- Admin APIs ---

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderAdminResponse> searchOrders(OrderSearchRequest request) {
        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPageNo(), request.getPageSize(), sort);

        Page<Order> orderPage = orderRepository.findAll(OrderSpecification.getFilterSpecification(request), pageable);

        Page<OrderAdminResponse> dtoPage = orderPage.map(OrderAdminResponse::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getAdminOrderDetail(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        return OrderDetailResponse.fromEntity(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        if (order.getStatus() == Order.OrderStatus.CANCELLED && request.getStatus() != Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Không thể thay đổi trạng thái của đơn hàng đã hủy");
        }
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED && request.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Không thể hủy đơn hàng đã giao thành công");
        }

        order.setStatus(request.getStatus());
        orderRepository.save(order);
    }
}
