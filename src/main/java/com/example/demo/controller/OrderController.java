package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.entities.Order;
import com.example.demo.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }



    @PostMapping
    public Order addOrder(@RequestBody Order order) {
        return service.saveOrder(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable("id") Long id) {
        return service.getOrderById(id);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable("id") Long id, @RequestBody Order order) {
        return service.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {
        service.deleteOrder(id);
        return "Order deleted successfully!";
    }

//  ---------------------
    
    @PostMapping("/place/{customerId}")
    public Order placeOrder(@PathVariable("customerId") Long customerId) {
        return service.placeOrder(customerId);
    }

    
    @DeleteMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        service.cancelOrder(orderId);
        return "Order cancelled!";
    }

    
    @PutMapping("/{orderId}/status")
    public Order updateStatus(@PathVariable("orderId") Long orderId, @RequestParam("status") String status) {
        return service.updateOrderStatus(orderId, status);
    }
}
