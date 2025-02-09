package com.example.product.repository;

import com.example.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
    List<Product> findBySellerId(String sellerId);
    boolean existsByCategoryId(String categoryId);
    Page<Product> findByCategoryId(String categoryId, Pageable pageable);
    Page<Product> findBySellerId(String sellerId, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
}
