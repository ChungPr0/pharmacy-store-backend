package com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttribute {
    private String name;
    private String value;
}
