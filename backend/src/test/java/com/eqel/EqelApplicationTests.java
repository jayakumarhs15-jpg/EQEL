package com.eqel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EqelApplicationTests {

    @Test
    void contextLoads() {
        // Verifies Spring context, JPA repositories, security filter chain, and bootstrap service start cleanly
    }
}
