package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.RegistrationRequest;
import org.marketplace.marketplace.project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationRequest")) {
            model.addAttribute("registrationRequest", new RegistrationRequest());
        }
        return "register";
    }

    @PostMapping
    public String register(@Valid @ModelAttribute("registrationRequest") RegistrationRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Пароли не совпадают");
        }

        // Если есть ошибки валидации или пароли не совпадают
        if (bindingResult.hasErrors()) {
            // ✅ Добавляем объект обратно в модель (Thymeleaf сам подхватит bindingResult)
            model.addAttribute("registrationRequest", request);
            return "register";
        }

        try {
            userService.register(request);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("registrationRequest", request);
            return "register";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка регистрации: " + e.getMessage());
            model.addAttribute("registrationRequest", request);
            return "register";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Регистрация прошла успешно. Войдите в систему.");
        return "redirect:/login";
    }
}
