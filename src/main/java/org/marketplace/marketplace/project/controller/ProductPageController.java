package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Product;
import org.marketplace.marketplace.project.service.ProductPageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductPageService productPageService;

    @GetMapping("/product/{id}")
    public String productPage(@PathVariable Long id, Model model) {
        Optional<Product> product = productPageService.getProduct(id);
        if (product.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("product", product.get());
        return "product-page";
    }
}
