package com.example.cart;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CartApplicationTests {

    @Test
    void contextLoads() {
        // Kiểm tra xem Spring Boot có khởi động thành công không
    }

    @Test
    void main_ShouldRunWithoutExceptions() {
        CartApplication.main(new String[] {});
    }
}
