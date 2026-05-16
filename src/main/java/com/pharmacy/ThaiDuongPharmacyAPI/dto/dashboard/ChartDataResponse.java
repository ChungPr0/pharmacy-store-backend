package com.pharmacy.ThaiDuongPharmacyAPI.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataResponse {
    private String date;
    private BigDecimal value;
}
