package org.trasfermarkt.project.controller;

import lombok.RequiredArgsConstructor;
import org.trasfermarkt.project.model.ProfileUpdateRequest;
import org.trasfermarkt.project.model.User;
import org.trasfermarkt.project.repository.UserRepository;
import org.trasfermarkt.project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/edit")
    public String editForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Текущий пользователь не найден"));

        ProfileUpdateRequest profile = new ProfileUpdateRequest();
        profile.setFirstName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        model.addAttribute("profile", profile);
        return "edit-profile";
    }

    @PostMapping("/edit")
    public String submit(@ModelAttribute("profile") ProfileUpdateRequest profile,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            String newEmail = userService.updateProfile(principal.getName(), profile);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль обновлён");
            if (!newEmail.equalsIgnoreCase(principal.getName())) {
                return "redirect:/logout";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }
}
