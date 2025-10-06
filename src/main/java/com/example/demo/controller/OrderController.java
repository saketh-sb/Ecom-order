package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.Order;
import com.example.demo.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order API", description = "Order management and order processing operations")
public class OrderController {
	
	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	@Operation(
		summary = "Create a new order",
		description = "Add a new order to the system"
	)
	@ApiResponse(
		responseCode = "201",
		description = "Order created successfully",
		content = @Content(schema = @Schema(implementation = Order.class))
	)
	@ApiResponse(responseCode = "400", description = "Invalid order data")
	@PostMapping
	public ResponseEntity<Order> addOrder(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
				description = "Order object to create",
				required = true,
				content = @Content(
					schema = @Schema(implementation = Order.class),
					examples = @ExampleObject(
						value = "{ \"customerId\": 1, \"status\": \"PLACED\", \"items\": [{ \"productId\": 10, \"quantity\": 2 }] }"
					)
				)
			)
			@Valid @RequestBody Order order) {
		Order savedOrder = service.saveOrder(order);
		return ResponseEntity.status(201).body(savedOrder);
	}

	@Operation(
		summary = "Get all orders",
		description = "Retrieve a list of all orders in the system"
	)
	@ApiResponse(
		responseCode = "200",
		description = "List of orders retrieved successfully"
	)
	@GetMapping
	public List<Order> getAllOrders() {
		return service.getAllOrders();
	}

	@Operation(
		summary = "Get order by ID",
		description = "Retrieve a specific order using its ID"
	)
	@ApiResponse(
		responseCode = "200",
		description = "Order found",
		content = @Content(schema = @Schema(implementation = Order.class))
	)
	@ApiResponse(responseCode = "404", description = "Order not found")
	@GetMapping("/{id}")
	public ResponseEntity<Order> getOrderById(
			@Parameter(description = "ID of the order to retrieve", required = true, example = "1")
			@PathVariable("id") Long id) {
		Order order = service.getOrderById(id);
		return ResponseEntity.ok(order);
	}

	@Operation(
		summary = "Update order",
		description = "Update an existing order's information"
	)
	@ApiResponse(
		responseCode = "200",
		description = "Order updated successfully",
		content = @Content(schema = @Schema(implementation = Order.class))
	)
	@ApiResponse(responseCode = "404", description = "Order not found")
	@ApiResponse(responseCode = "400", description = "Invalid order data")
	@PutMapping("/{id}")
	public ResponseEntity<Order> updateOrder(
			@Parameter(description = "ID of the order to update", required = true, example = "1")
			@PathVariable("id") Long id,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
				description = "Updated order object",
				required = true,
				content = @Content(
					schema = @Schema(implementation = Order.class),
					examples = @ExampleObject(
						value = "{ \"customerId\": 1, \"status\": \"CONFIRMED\", \"items\": [{ \"productId\": 10, \"quantity\": 3 }] }"
					)
				)
			)
			@Valid @RequestBody Order order) {
		Order updatedOrder = service.updateOrder(id, order);
		return ResponseEntity.ok(updatedOrder);
	}

	@Operation(
		summary = "Delete order",
		description = "Delete an order from the system"
	)
	@ApiResponse(responseCode = "200", description = "Order deleted successfully")
	@ApiResponse(responseCode = "404", description = "Order not found")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteOrder(
			@Parameter(description = "ID of the order to delete", required = true, example = "1")
			@PathVariable("id") Long id) {
		service.deleteOrder(id);
		return ResponseEntity.ok("Order deleted successfully!");
	}

	// --------------------- Order Processing Operations ---------------------

	@Operation(
		summary = "Place order from cart",
		description = "Create and place an order from customer's cart items"
	)
	@ApiResponse(
		responseCode = "201",
		description = "Order placed successfully",
		content = @Content(schema = @Schema(implementation = Order.class))
	)
	@ApiResponse(responseCode = "404", description = "Customer not found or cart is empty")
	@ApiResponse(responseCode = "400", description = "Insufficient stock or invalid cart items")
	@PostMapping("/place/{customerId}")
	public ResponseEntity<Order> placeOrder(
			@Parameter(description = "ID of the customer placing the order", required = true, example = "1")
			@PathVariable("customerId") Long customerId) {
		Order placedOrder = service.placeOrder(customerId);
		return ResponseEntity.status(201).body(placedOrder);
	}

	@Operation(
		summary = "Cancel order",
		description = "Cancel an existing order"
	)
	@ApiResponse(responseCode = "200", description = "Order cancelled successfully")
	@ApiResponse(responseCode = "404", description = "Order not found")
	@ApiResponse(responseCode = "400", description = "Order cannot be cancelled (already delivered/cancelled)")
	@DeleteMapping("/cancel/{orderId}")
	public ResponseEntity<String> cancelOrder(
			@Parameter(description = "ID of the order to cancel", required = true, example = "1")
			@PathVariable("orderId") Long orderId) {
		service.cancelOrder(orderId);
		return ResponseEntity.ok("Order cancelled!");
	}

	@Operation(
		summary = "Update order status",
		description = "Update the status of an existing order (PLACED, CONFIRMED, CANCELLED, DELIVERED)"
	)
	@ApiResponse(
		responseCode = "200",
		description = "Order status updated successfully",
		content = @Content(schema = @Schema(implementation = Order.class))
	)
	@ApiResponse(responseCode = "404", description = "Order not found")
	@ApiResponse(responseCode = "400", description = "Invalid status value")
	@PutMapping("/{orderId}/status")
	public ResponseEntity<Order> updateStatus(
			@Parameter(description = "ID of the order to update", required = true, example = "1")
			@PathVariable("orderId") Long orderId,
			@Parameter(
				description = "New status for the order",
				required = true,
				example = "CONFIRMED",
				schema = @Schema(allowableValues = {"PLACED", "CONFIRMED", "CANCELLED", "DELIVERED"})
			)
			@RequestParam("status") String status) {
		Order updatedOrder = service.updateOrderStatus(orderId, status);
		return ResponseEntity.ok(updatedOrder);
	}
}