package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Transfer;
import org.marketplace.marketplace.project.service.TransferPageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * Публичная страница детального просмотра объявления (/product/{id}).
 * Если объявления нет — переадресует на главную.
 */
@Controller
@RequiredArgsConstructor
public class TransferPageController {

    private final TransferPageService transferPageService;

    @GetMapping("/product/{id}")
    public String productPage(@PathVariable Long id, Model model) {
        Optional<Transfer> transfer = transferPageService.getProduct(id);
        if (transfer.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("product", transfer.get());
        return "product-page";
    }
}
