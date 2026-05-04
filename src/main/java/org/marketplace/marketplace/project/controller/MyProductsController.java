package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.ProductStatus;
import org.marketplace.marketplace.project.model.User;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.marketplace.marketplace.project.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class MyProductsController {

    private final ProductService productService;
    private final UserRepository userRepository;

    @GetMapping("/my-products")
    public String myProducts(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Long userId = userRepository.findByEmail(principal.getName())
                .map(User::getId)
                .orElse(null);

        if (userId == null) {
            model.addAttribute("approvedProducts", Collections.emptyList());
            model.addAttribute("pendingProducts", Collections.emptyList());
        } else {
            model.addAttribute("approvedProducts",
                    productService.getProductsByUserAndStatus(userId, ProductStatus.APPROVED));
            model.addAttribute("pendingProducts",
                    productService.getProductsByUserAndStatus(userId, ProductStatus.PENDING));
        }
        return "my-products";
    }
}
