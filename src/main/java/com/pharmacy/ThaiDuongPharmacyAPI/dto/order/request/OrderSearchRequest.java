package com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class OrderSearchRequest {
    private String keyword = ""; // for orderCode, receiverName, phone

    @Pattern(regexp = "^(PENDING|PAID|PROCESSING|SHIPPING|DELIVERED|CANCELLED|)$", message = "Trạng thái (status) không hợp lệ")
    private String status;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @Min(value = 0, message = "Số trang (pageNo) không được nhỏ hơn 0")
    private int pageNo = 0;

    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    @Max(value = 100, message = "Tối đa 100 đơn hàng/trang")
    private int pageSize = 10;

    @Pattern(regexp = "^(createdAt|totalAmount|status)$", message = "Chỉ cho phép sort theo: createdAt, totalAmount, status")
    private String sortBy = "createdAt";

    @Pattern(regexp = "^(ASC|DESC)$", message = "Hướng sắp xếp (sortDir) chỉ chấp nhận 'ASC' hoặc 'DESC'")
    private String sortDir = "DESC";
}
