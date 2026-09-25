package com.shopkart.controller;

import com.shopkart.dto.ReviewRequest;
import com.shopkart.model.Product;
import com.shopkart.model.Review;
import com.shopkart.model.User;
import com.shopkart.repository.ProductRepository;
import com.shopkart.repository.ReviewRepository;
import com.shopkart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @GetMapping("/product/{productId}")
    public List<Review> byProduct(@PathVariable Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    // Comment text is stored and returned exactly as submitted, with no
    // server-side sanitization. Combined with a frontend that renders it
    // via dangerouslySetInnerHTML (see ProductDetail.jsx), this is the
    // intentional target for your Stored XSS finding.
    @PostMapping("/product/{productId}")
    public Review addReview(@PathVariable Long productId, @RequestBody ReviewRequest req, Authentication auth) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setComment(req.getComment());
        review.setRating(req.getRating());

        return reviewRepository.save(review);
    }
}
