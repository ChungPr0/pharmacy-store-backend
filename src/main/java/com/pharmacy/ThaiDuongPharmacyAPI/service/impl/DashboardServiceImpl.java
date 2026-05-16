package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.dashboard.ChartDataResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.AccountRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.OrderRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<ChartDataResponse> getChartData(String type, LocalDate from, LocalDate to) {
        LocalDateTime startDateTime = from.atStartOfDay();
        LocalDateTime endDateTime = to.atTime(23, 59, 59);

        List<Object[]> rawData;

        switch (type.toUpperCase()) {
            case "ORDERS":
                rawData = orderRepository.countOrdersByDate(startDateTime, endDateTime);
                break;
            case "REVENUE":
                rawData = orderRepository.sumRevenueByDate(startDateTime, endDateTime);
                break;
            case "PRODUCTS":
                rawData = orderRepository.sumProductsSoldByDate(startDateTime, endDateTime);
                break;
            case "CUSTOMERS":
                rawData = accountRepository.countCustomersByDate(startDateTime, endDateTime);
                break;
            default:
                throw new IllegalArgumentException("Invalid chart type");
        }

        Map<String, BigDecimal> dataMap = new HashMap<>();
        java.text.SimpleDateFormat dbDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        
        for (Object[] row : rawData) {
            if (row[0] == null) continue;
            
            String dateStr;
            if (row[0] instanceof java.util.Date) {
                dateStr = dbDateFormat.format((java.util.Date) row[0]);
            } else {
                dateStr = row[0].toString();
                if (dateStr.length() > 10) {
                    dateStr = dateStr.substring(0, 10);
                }
            }
            
            BigDecimal value = (row[1] != null) ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            dataMap.put(dateStr, value);
        }

        List<ChartDataResponse> response = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        LocalDate currentDate = from;
        while (!currentDate.isAfter(to)) {
            String dateStr = currentDate.format(formatter);
            BigDecimal value = dataMap.getOrDefault(dateStr, BigDecimal.ZERO);
            response.add(new ChartDataResponse(dateStr, value));
            currentDate = currentDate.plusDays(1);
        }


        return response;
    }
}
