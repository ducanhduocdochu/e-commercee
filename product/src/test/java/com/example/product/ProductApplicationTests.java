package com.example.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductApplicationTests {

    @Test
    void contextLoads() {
        // Kiểm tra xem Spring Boot có khởi động thành công không
    }

    @Test
    void main_ShouldRunWithoutExceptions() {
        ProductApplication.main(new String[] {});
    }
}
