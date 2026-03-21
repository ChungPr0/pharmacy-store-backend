package com.pharmacy.ThaiDuongPharmacyAPI.service.impl;

import com.pharmacy.ThaiDuongPharmacyAPI.dto.common.PageResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.ProductBatchImportRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.request.ProductBatchItemRequest;
import com.pharmacy.ThaiDuongPharmacyAPI.dto.product.response.ProductBatchHistoryResponse;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.Product;
import com.pharmacy.ThaiDuongPharmacyAPI.entity.ProductBatch;
import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductBatchRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.repository.ProductRepository;
import com.pharmacy.ThaiDuongPharmacyAPI.service.AdminProductBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductBatchServiceImpl implements AdminProductBatchService {

    private final ProductBatchRepository productBatchRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void importProductBatches(ProductBatchImportRequest request) {
        List<ProductBatch> batchesToSave = new ArrayList<>();

        for (ProductBatchItemRequest item : request.getItems()) {
            if (item.getManufactureDate().isAfter(item.getExpiryDate()) || item.getManufactureDate().isEqual(item.getExpiryDate())) {
                throw ApiException.badRequest("Ngày sản xuất phải trước ngày hết hạn");
            }

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> ApiException.notFound("Không tìm thấy sản phẩm có id " + item.getProductId()));

            ProductBatch batch = new ProductBatch();
            batch.setProduct(product);
            batch.setBatchNumber(item.getBatchNumber());
            batch.setManufactureDate(item.getManufactureDate());
            batch.setExpiryDate(item.getExpiryDate());
            batch.setImportPrice(item.getImportPrice());
            batch.setStockQuantity(item.getStockQuantity());
            batch.setImportNote(request.getImportNote());

            batchesToSave.add(batch);
        }

        productBatchRepository.saveAll(batchesToSave);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductBatchHistoryResponse> getProductBatches(int pageNo, int pageSize, Long productId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<ProductBatch> pageResult = productBatchRepository.findHistoryWithFilters(productId, pageable);

        List<ProductBatchHistoryResponse> content = pageResult.getContent().stream().map(batch -> 
            ProductBatchHistoryResponse.builder()
                .id(batch.getId())
                .productId(batch.getProduct().getId())
                .productName(batch.getProduct().getName())
                .batchNumber(batch.getBatchNumber())
                .importPrice(batch.getImportPrice())
                .manufactureDate(batch.getManufactureDate())
                .expiryDate(batch.getExpiryDate())
                .stockQuantity(batch.getStockQuantity())
                .importNote(batch.getImportNote())
                .build()
        ).collect(Collectors.toList());

        return new PageResponse<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }
}
