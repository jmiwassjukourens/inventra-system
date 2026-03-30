package com.infraestructure.keycloakadapter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.config.import=",
        "spring.cloud.config.enabled=false"
})
@ActiveProfiles("test")
class KeycloakAdapterApplicationTests {

    @Test
    void contextLoads() {
    }
}
