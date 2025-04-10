package com.bank.mqmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MqManagementApplicationTests {

    @Test
    void contextLoads() {
        // Ce test vérifie simplement que le contexte Spring se charge correctement
    }
}