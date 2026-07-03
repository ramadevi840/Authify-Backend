package com.authify.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tbl_users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String userId;

    private String name;

    @Column(unique=true)
    private String email;

    private String password;

    private String verifyOtp;   // ✅ fixed typo

    private Boolean isAccountVerified;

    private Long verifyOtpExpiredAt;

    private String resetOtp;

    private Long resetOtpExpireAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;   // ✅ use LocalDateTime

    @UpdateTimestamp
    private LocalDateTime updatedAt;   // ✅ use LocalDateTime
}

