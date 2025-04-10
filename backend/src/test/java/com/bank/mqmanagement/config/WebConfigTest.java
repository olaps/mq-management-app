// src/test/java/com/bank/mqmanagement/config/WebConfigTest.java
package com.bank.mqmanagement.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest
@ContextConfiguration(classes = {WebConfig.class})
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCorsConfiguration() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.options("/api/messages")
                                .header("Origin", "http://localhost:4200")
                                .header("Access-Control-Request-Method", "GET")
                )
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"));
    }
}