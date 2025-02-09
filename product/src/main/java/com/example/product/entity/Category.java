package com.example.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
