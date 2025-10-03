package com.example.demo.service;

import com.example.demo.entities.CartItemResponse;
import com.example.demo.entities.CustomerResponse;
import com.example.demo.entities.Order;
import com.example.demo.entities.OrderItem;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.feign.CustomerClient;
import com.example.demo.feign.ProductClient;
import com.example.demo.feign.CartClient;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final CartClient cartClient;

    public OrderService(OrderRepository orderRepo,
                        CustomerClient customerClient,
                        ProductClient productClient,
                        CartClient cartClient) {
        this.orderRepo = orderRepo;
        this.customerClient = customerClient;
        this.productClient = productClient;
        this.cartClient = cartClient;
    }



    public Order saveOrder(Order order) {
        return orderRepo.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
    }

    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        order.setStatus(orderDetails.getStatus());
        return orderRepo.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRepo.delete(order);
    }

//-----------------

    public Order placeOrder(Long customerId) {
        CustomerResponse customer = customerClient.getCustomer(customerId);
        if (customer == null) throw new ResourceNotFoundException("Customer not found with id " + customerId);

        List<CartItemResponse> cartItems = cartClient.getCart(customerId);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty!");

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setStatus("PLACED");

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemResponse item : cartItems) {
            productClient.reduceStock(item.getProductId(), item.getQuantity());

            OrderItem oi = new OrderItem();
            oi.setProductId(item.getProductId());
            oi.setQuantity(item.getQuantity());
            oi.setOrder(order);
            orderItems.add(oi);
        }

        order.setItems(orderItems);
        Order savedOrder = orderRepo.save(order);

        cartClient.clearCart(customerId);

        return savedOrder;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        if (order.getStatus().equals("DELIVERED")) {
            throw new RuntimeException("Cannot cancel a delivered order!");
        }

        order.setStatus("CANCELLED");
        orderRepo.save(order);

       
        for (OrderItem item : order.getItems()) {
            productClient.increaseStock(item.getProductId(), item.getQuantity());
        }
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));

        order.setStatus(status);
        return orderRepo.save(order);
    }
}
