package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.CheckoutRequestDTO;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.*;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.*;
import com.pharmacy.ThaiDuongPharmacyAPI.service.OrderService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AuthUtils authUtils;
    private final CartItemRepository cartItemRepository;
    private final ProductBatchRepository productBatchRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void checkout(CheckoutRequestDTO request) {
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
}
