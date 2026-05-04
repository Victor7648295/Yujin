package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.User;
import org.marketplace.marketplace.project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Админка пользователей (/admin/users): список с поиском по имени,
 * редактирование данных и роли, смена пароля, блокировка и
 * разблокировка учётных записей.
 */
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
    public String editUserForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.getById(id);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user.get());
        return "admin/user-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String email,
                             @RequestParam String role,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                             RedirectAttributes redirectAttributes) {
        if (newPassword != null && !newPassword.isEmpty()
                && !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пароли не совпадают");
            return "redirect:/admin/users/edit/" + id;
        }
        try {
            if (userService.adminUpdate(id, firstName, lastName, email, role, newPassword)) {
                redirectAttributes.addFlashAttribute("successMessage", "Пользователь обновлён");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
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
