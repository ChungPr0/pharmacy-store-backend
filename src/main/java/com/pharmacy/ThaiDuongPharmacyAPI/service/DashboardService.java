package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.dashboard.ChartDataResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    List<ChartDataResponse> getChartData(String type, LocalDate from, LocalDate to);
}
