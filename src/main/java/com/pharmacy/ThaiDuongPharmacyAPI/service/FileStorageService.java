package com.pharmacy.ThaiDuongPharmacyAPI.service;

import com.pharmacy.ThaiDuongPharmacyAPI.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "uploads/products";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    /**
     * Lưu file ảnh sản phẩm vào thư mục uploads/products/
     * @return đường dẫn tương đối: /uploads/products/{filename}
     */
    public String storeProductImage(MultipartFile file) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("Vui lòng chọn file ảnh hợp lệ.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw ApiException.badRequest("Dung lượng ảnh không được vượt quá 5MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw ApiException.badRequest("Chỉ hỗ trợ file ảnh định dạng: JPEG, PNG, WEBP, GIF.");
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file unique
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Lưu file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối
            return "/uploads/products/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file ảnh: " + e.getMessage(), e);
        }
    }
}
