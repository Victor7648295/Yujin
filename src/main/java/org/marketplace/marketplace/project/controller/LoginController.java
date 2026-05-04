package org.marketplace.marketplace.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Отображает страницу входа и переводит технические query-параметры
 * Spring Security (error / logout / registered) в человекочитаемые
 * сообщения для шаблона.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "registered", required = false) String registered,
                        Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Неверный email или пароль");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "Вы вышли из системы");
        }

        if (registered != null) {
            model.addAttribute("successMessage", "Регистрация прошла успешно! Войдите в систему.");
        }

        return "login";
    }
}