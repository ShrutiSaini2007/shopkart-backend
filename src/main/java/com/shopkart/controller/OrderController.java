package com.shopkart.controller;

import com.shopkart.dto.OrderItemRequest;
import com.shopkart.dto.OrderRequest;
import com.shopkart.model.*;
import com.shopkart.repository.OrderRepository;
import com.shopkart.repository.ProductRepository;
import com.shopkart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User currentUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest req, Authentication auth) {
        User user = currentUser(auth);

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(req.getShippingAddress());

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;
        for (OrderItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            items.add(item);
            total += product.getPrice() * itemReq.getQuantity();
        }
        order.setItems(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    // Returns only the logged-in user's own orders -> correct by design.
    @GetMapping("/mine")
    public List<Order> myOrders(Authentication auth) {
        return orderRepository.findByUser(currentUser(auth));
    }

    // --------------------------------------------------------------------
    // INTENTIONAL TRAINING VULNERABILITY (NS-P07 target: IDOR / A01:2021
    // Broken Access Control). This endpoint checks that the requester is
    // authenticated, but never checks that the requested order actually
    // belongs to them -> any logged-in user can read any order by guessing
    // sequential IDs. This mirrors a real, common Spring Boot mistake and
    // is what your "Broken Object-Level Authorization" finding should
    // demonstrate and then propose fixing (see commented-out fix below).
    // --------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id, Authentication auth) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

        // FIX for your Mitigation section, once the finding is documented:
        //
        // Order order = orderRepository.findById(id)
        //         .orElseThrow(() -> new RuntimeException("Order not found"));
        // if (!order.getUser().getId().equals(currentUser(auth).getId())) {
        //     return ResponseEntity.status(403).build();
        // }
        // return ResponseEntity.ok(order);
    }
}
