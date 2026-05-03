package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(value = "query", required = false) String query,
                            Model model) {
        model.addAttribute("users", userService.searchByName(query));
        model.addAttribute("searchQuery", query);
        return "admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "admin/user-edit";
    }

    @PostMapping("/block/{id}")
    public String blockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (userService.blockUser(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь заблокирован");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/unblock/{id}")
    public String unblockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (userService.unblockUser(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь разблокирован");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
        }
        return "redirect:/admin/users";
    }
}
