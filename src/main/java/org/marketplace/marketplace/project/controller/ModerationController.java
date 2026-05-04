package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Product;
import org.marketplace.marketplace.project.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Админская модерация объявлений (/admin/moderation): показывает
 * объявления со статусом PENDING, отдаёт страницу детализации и
 * переводит объявление в APPROVED либо REJECTED.
 */
@Controller
@RequestMapping("/admin/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final ProductService productService;

    @GetMapping
    public String listPending(Model model) {
        List<Product> pending = productService.getPendingProducts();
        model.addAttribute("products", pending);
        model.addAttribute("count", pending.size());
        return "admin/moderation";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return "redirect:/admin/moderation";
        }
        model.addAttribute("product", product.get());
        return "product-page";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (productService.approveProduct(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Объявление одобрено");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Объявление не найдено");
        }
        return "redirect:/admin/moderation";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (productService.rejectProduct(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Объявление отклонено");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Объявление не найдено");
        }
        return "redirect:/admin/moderation";
    }
}
