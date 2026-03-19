package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.request.CartItemRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.request.UpdateCartItemRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.response.CartInfoResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.cart.response.CartItemResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Cart;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.CartItem;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Customer;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CartItemRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.CartRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.CartService;
import com.pharmacy.ThaiDuongPharmacyAPI.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthUtils authUtils;

    @Override
    @Transactional(readOnly = true)
    public CartInfoResponse getCartInfo() {
        Long customerId = authUtils.getCurrentCustomerId();
        Optional<Cart> cartOpt = cartRepository.findByCustomerId(customerId);

        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            return CartInfoResponse.builder()
                    .cartId(cartOpt.map(Cart::getId).orElse(null))
                    .totalItems(0)
                    .totalPrice(0.0)
                    .items(new ArrayList<>())
                    .build();
        }

        Cart cart = cartOpt.get();
        List<CartItemResponse> itemResponses = new ArrayList<>();
        int totalItems = 0;
        double totalPrice = 0.0;

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getIsActive() != null && product.getIsActive()) {
                double price = product.getPrice() != null ? product.getPrice().doubleValue() : 0.0;
                double itemTotal = price * item.getQuantity();

                totalItems += item.getQuantity();
                totalPrice += itemTotal;

                itemResponses.add(CartItemResponse.builder()
                        .productId(product.getId())
                        .name(product.getName())
                        .slug(product.getSlug())
                        .imageUrl(product.getImageUrl())
                        .price(price)
                        .quantity(item.getQuantity())
                        .itemTotal(itemTotal)
                        .build());
            }
        }

        return CartInfoResponse.builder()
                .cartId(cart.getId())
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .items(itemResponses)
                .build();
    }

    @Override
    @Transactional
    public void addProductToCart(CartItemRequest request) {
        Customer currentCustomer = authUtils.getCurrentCustomer();
        
        Cart cart = cartRepository.findByCustomerId(currentCustomer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(currentCustomer);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ApiException(404, "Sản phẩm không tồn tại!"));

        if (product.getIsActive() == null || !product.getIsActive()) {
            throw new ApiException(400, "Sản phẩm đã ngừng kinh doanh!");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public void updateCartItemQuantity(UpdateCartItemRequest request) {
        Long customerId = authUtils.getCurrentCustomerId();
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ApiException(404, "Giỏ hàng trống!"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new ApiException(404, "Sản phẩm không có trong giỏ hàng!"));

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public void removeProductFromCart(Long productId) {
        Long customerId = authUtils.getCurrentCustomerId();
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ApiException(404, "Giỏ hàng trống!"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ApiException(404, "Sản phẩm không có trong giỏ hàng!"));

        cartItemRepository.delete(cartItem);
    }
}
