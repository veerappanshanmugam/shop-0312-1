package com.example.ecommerce;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, String> healthCheck() {
        return Map.of("status", "healthy", "service", "ecommerce-monolith");
    }
}
