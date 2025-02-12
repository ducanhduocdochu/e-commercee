package com.example.product.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    // ✅ Tự động lưu thời gian tạo
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ✅ Tự động lưu thời gian cập nhật
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
