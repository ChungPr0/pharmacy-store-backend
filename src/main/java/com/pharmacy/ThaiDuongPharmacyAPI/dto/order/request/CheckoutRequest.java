package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddressText;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private String paymentToken;

    @NotEmpty(message = "Danh sách sản phẩm thanh toán không được để trống")
    private List<Long> cartItemIds;
}
