package com.example.product.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.product.dto.request.ProductCreationRequest;
import com.example.product.dto.request.ProductUpdateRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.exception.AppException;
import com.example.product.exception.ErrorCode;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.security.AuthenticatedUser;
import com.example.product.security.AuthorizationUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;

    public List<ProductResponse> getProducts(int pageSize, int pageNumber, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> products = productRepository.findAll(pageable);
        return productMapper.toProductResponseList(products.getContent());
    }

    public ProductResponse getProduct(String productId) {
        Product product =
                productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        return productMapper.toProductResponse(product);
    }

    @PreAuthorize("hasRole('SELLER')")
    public ProductResponse createProduct(ProductCreationRequest request) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_SELLER"));

        // ✅ Lấy sellerId từ JWT
        Object principal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) principal;
        String sellerId = authenticatedUser.getUserId();

        log.info("✅ SellerId from SecurityContext: {}", sellerId);

        if (sellerId == null || sellerId.isEmpty()) {
            log.error("❌ Missing sellerId from JWT");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // ✅ Kiểm tra danh mục sản phẩm có tồn tại không
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // ✅ Tạo sản phẩm mới
        Product product = productMapper.toProduct(request, category);
        product.setSellerId(sellerId);

        product = productRepository.save(product);
        log.info("✅ Product created: ProductId={}, SellerId={}", product.getId(), product.getSellerId());

        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> getProductsBySeller(
            String sellerId, int pageSize, int pageNumber, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> products = productRepository.findBySellerId(sellerId, pageable);
        return productMapper.toProductResponseList(products.getContent());
    }

    public List<ProductResponse> getProductsByCategory(
            String categoryId, int pageSize, int pageNumber, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return productMapper.toProductResponseList(products.getContent());
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_SELLER", "ROLE_ADMIN"));
        Product product =
                productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.updateProduct(product, request);

        return productMapper.toProductResponse(productRepository.save(product));
    }

    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public void deleteProduct(String productId) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_SELLER", "ROLE_ADMIN"));
        productRepository.deleteById(productId);
    }

    @PreAuthorize("hasRole('SELLER')")
    public ProductResponse updateStock(String productId, int stock) {
        AuthorizationUtil.checkAuthorities(Set.of("ROLE_SELLER"));
        Product product =
                productRepository.findById(productId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setStock(stock);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    public List<ProductResponse> getProductsByIds(List<String> productIds) {
        return productRepository.findAllById(productIds).stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock(),
                        product.getCategory().getId(),
                        product.getSellerId(),
                        product.getCreatedAt(),
                        product.getUpdatedAt()))
                .collect(Collectors.toList());
    }
}
