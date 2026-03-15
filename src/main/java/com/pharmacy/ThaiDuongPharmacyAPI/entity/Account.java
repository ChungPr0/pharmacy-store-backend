package com.pharmacy.ThaiDuongPharmacyAPI.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "previous_password")
    private String previousPassword;

    @Column(name = "role")
    private String role = "CUSTOMER";

    @Column(name = "status")
    private String status = "ACTIVE";
}