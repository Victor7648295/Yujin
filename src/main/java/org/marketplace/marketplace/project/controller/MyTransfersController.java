package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.User;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.marketplace.marketplace.project.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collections;

/**
 * Личный кабинет пользователя (/my-products): показывает все объявления
 * пользователя, отсортированные по статусу.
 */
@Controller
@RequiredArgsConstructor
public class MyTransfersController {

    private final TransferService transferService;
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
            model.addAttribute("userProducts", Collections.emptyList());
        } else {
            model.addAttribute("userProducts", transferService.getProductsByUser(userId));
        }
        return "my-products";
    }
}
