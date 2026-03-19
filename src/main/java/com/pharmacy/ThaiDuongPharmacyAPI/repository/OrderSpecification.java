package com.pharmacy.ThaiDuongPharmacyAPI.repository;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.order.request.OrderSearchRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> getFilterSpecification(OrderSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                String pattern = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate codeMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("orderCode")), pattern);
                Predicate nameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("receiverName")), pattern);
                Predicate phoneMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), pattern);
                predicates.add(criteriaBuilder.or(codeMatch, nameMatch, phoneMatch));
            }

            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), Order.OrderStatus.valueOf(request.getStatus())));
            }

            if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
            }

            if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
