package com.example.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String sellerId;  // Chỉ lưu ID của Seller, không cần quan hệ với User

    // ✅ Tự động lưu thời gian tạo
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ✅ Tự động lưu thời gian cập nhật
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
