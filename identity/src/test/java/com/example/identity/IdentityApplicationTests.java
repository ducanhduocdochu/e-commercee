package com.example.identity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IdentityApplicationTests {

    @Test
    void contextLoads() {
        // Kiểm tra xem Spring Boot có khởi động thành công không
    }

    @Test
    void main_ShouldRunWithoutExceptions() {
        IdentityApplication.main(new String[] {});
    }
}
