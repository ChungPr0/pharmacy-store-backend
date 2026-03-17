# 🏥 Thái Dương Pharmacy - E-Commerce API

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg?style=for-the-badge)
![Build](https://img.shields.io/badge/build-passing-success.svg?style=for-the-badge)
![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg?style=for-the-badge)

### 🚀 Tech Stack
![Java](https://img.shields.io/badge/java_17-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring_boot_3-%236DB33F.svg?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![MySQL](https://img.shields.io/badge/mysql-%234479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

Backend RESTful API cho hệ thống Nhà thuốc Thái Dương. Hệ thống được thiết kế theo chuẩn kiến trúc Microservices (Monolithic for now), tối ưu hóa trải nghiệm người dùng, bảo mật cao và sẵn sàng mở rộng.

## 🗄️ Database Schema (Current)

Hệ thống cơ sở dữ liệu hiện tại bao gồm các module chính:
* **Auth & User:** `accounts`, `customers`, `otps`, `refresh_tokens`.
* **Product Catalog:** `categories`, `products`, `product_batches` (quản lý lô/hạn sử dụng), `product_images`, `product_attributes`.

## 📦 Các Module Đã Hoàn Thiện

### 1. Authentication (Xác thực & Bảo mật)
- [x] Đăng ký / Đăng nhập bằng Số điện thoại & Mật khẩu.
- [x] Quản lý JWT Token & Refresh Token.
- [x] Luồng Quên mật khẩu 3 bước (Bảo mật 2 lớp OTP + Mật khẩu).
- [x] Phân quyền cơ bản (Role-based access).

### 2. Product Catalog (Trưng bày sản phẩm)
- [x] Lấy cây danh mục sản phẩm (Category Tree).
- [x] Lấy danh sách sản phẩm (Bán chạy, Mới nhất).
- [x] **Tìm kiếm & Lọc sản phẩm nâng cao (Search, Sort, Pagination).**
- [x] Chi tiết sản phẩm (Gộp thông số, mảng ảnh phụ, tính toán tổng tồn kho realtime từ các lô hàng).
- [x] Lọc sản phẩm thông minh: Chỉ hiển thị sản phẩm `is_active = true`, tồn kho > 0 và Hạn sử dụng (Expiry Date) > 90 ngày.

## 🚧 Road map (Sắp triển khai)
- [ ] Module Giỏ hàng (Cart & Cart Items).
- [ ] Module Khuyến mãi (Voucher / Coupon).
- [ ] Module Đặt hàng & Thanh toán (Orders & Payment Gateway).
- [ ] Tích hợp gửi SMS OTP thực tế.

## 🛠️ Hướng dẫn Setup Local

1.  Clone repository này về máy.
2.  Tạo database trong MySQL: `CREATE DATABASE ThaiDuongPharmacy;`
3.  Cấu hình lại username/password database trong file `application-local.properties`.
4.  Chạy project. Spring Boot sẽ tự động tạo các bảng (DDL Auto) dựa trên Entity.
5.  Import file script `data.sql` (nếu có) để tạo dữ liệu mẫu.

---
*Developed with ❤️ by Chung & Team*
