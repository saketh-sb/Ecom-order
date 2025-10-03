package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.entities.CartItemResponse;

import java.util.List;

@FeignClient(
    name = "customer-service",
    contextId = "cartClient"  
)
public interface CartClient {
    @GetMapping("/customers/{customerId}/cart")
    List<CartItemResponse> getCart(@PathVariable("customerId") Long customerId);

    @DeleteMapping("/customers/{customerId}/cart/clear")
    String clearCart(@PathVariable("customerId") Long customerId);
}