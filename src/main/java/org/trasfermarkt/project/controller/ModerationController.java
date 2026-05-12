package org.trasfermarkt.project.controller;

import lombok.RequiredArgsConstructor;
import org.trasfermarkt.project.model.Transfer;
import org.trasfermarkt.project.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/moderation")
@RequiredArgsConstructor
public class ModerationController {

    private final TransferService transferService;

    @GetMapping
    public String listPending(Model model) {
        List<Transfer> pending = transferService.getPendingProducts();
        model.addAttribute("products", pending);
        model.addAttribute("count", pending.size());
        return "admin/moderation";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model) {
        Optional<Transfer> transfer = transferService.getProductById(id);
        if (transfer.isEmpty()) {
            return "redirect:/admin/moderation";
        }
        model.addAttribute("product", transfer.get());
        return "admin/moderation-details";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (transferService.approveProduct(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Объявление одобрено");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Объявление не найдено");
        }
        return "redirect:/admin/moderation";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (transferService.rejectProduct(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Объявление отклонено");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Объявление не найдено");
        }
        return "redirect:/admin/moderation";
    }
}
