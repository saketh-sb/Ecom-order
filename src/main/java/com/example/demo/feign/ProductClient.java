package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(
    name = "product-service",
    contextId = "productClient" 
)
public interface ProductClient {
    @PutMapping("/products/{id}/reduceStock/{qty}")
    String reduceStock(@PathVariable("id") Long id, @PathVariable("qty") int qty);

    @PutMapping("/products/{id}/increaseStock/{qty}")
    String increaseStock(@PathVariable("id") Long id, @PathVariable("qty") int qty);
}