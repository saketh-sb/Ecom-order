package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entities.CustomerResponse;

@FeignClient(
    name = "customer-service",
    contextId = "customerClient"  
)
public interface CustomerClient {
    @GetMapping("/customers/{id}")
    CustomerResponse getCustomer(@PathVariable("id") Long id);
}