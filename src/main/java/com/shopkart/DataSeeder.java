package com.shopkart;

import com.shopkart.model.*;
import com.shopkart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User customer1 = new User();
            customer1.setName("Test User One");
            customer1.setEmail("user1@test.com");
            customer1.setPassword(passwordEncoder.encode("Password123!"));
            customer1.setRole(Role.CUSTOMER);
            userRepository.save(customer1);

            User customer2 = new User();
            customer2.setName("Test User Two");
            customer2.setEmail("user2@test.com");
            customer2.setPassword(passwordEncoder.encode("Password123!"));
            customer2.setRole(Role.CUSTOMER);
            userRepository.save(customer2);

            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@shopkart.com");
            admin.setPassword(passwordEncoder.encode("AdminPass123!"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (productRepository.count() == 0) {
            String[][] demoProducts = {
                {"Wireless Mouse", "Ergonomic 2.4GHz wireless mouse", "799"},
                {"Mechanical Keyboard", "RGB backlit mechanical keyboard", "2999"},
                {"USB-C Hub", "7-in-1 USB-C hub with HDMI", "1499"},
                {"Laptop Stand", "Adjustable aluminum laptop stand", "1199"},
                {"Webcam 1080p", "Full HD webcam with autofocus", "1899"},
                {"Bluetooth Speaker", "Portable speaker, 12h battery", "2199"},
                {"Desk Lamp", "LED desk lamp with USB charging port", "899"},
                {"Phone Stand", "Adjustable desktop phone stand", "349"}
            };
            for (String[] p : demoProducts) {
                Product product = new Product();
                product.setName(p[0]);
                product.setDescription(p[1]);
                product.setPrice(Double.parseDouble(p[2]));
                product.setImageUrl("");
                productRepository.save(product);
            }
        }
    }
}
