package com.pharmacy.ThaiDuongPharmacyAPI.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;
}
