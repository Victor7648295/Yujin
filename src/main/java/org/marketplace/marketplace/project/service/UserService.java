package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.ProfileUpdateRequest;
import org.marketplace.marketplace.project.model.RegistrationRequest;
import org.marketplace.marketplace.project.model.User;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Бизнес-логика по пользователям: регистрация, поиск и листинг,
 * блокировка/разблокировка, обновление профиля самим пользователем
 * и админское редактирование (включая смену роли и сброс пароля).
 * Шифрует пароли через {@link PasswordEncoder} и проверяет уникальность email.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public boolean adminUpdate(Long id, String firstName, String lastName, String email,
                               String role, String newPassword) {
        return userRepository.findById(id).map(user -> {
            if (firstName != null && !firstName.isBlank()) {
                user.setFirstName(firstName);
            }
            if (lastName != null && !lastName.isBlank()) {
                user.setLastName(lastName);
            }
            if (email != null && !email.isBlank() && !email.equalsIgnoreCase(user.getEmail())) {
                if (userRepository.existsByEmail(email)) {
                    throw new IllegalArgumentException("Email уже занят");
                }
                user.setEmail(email);
            }
            if (role != null && !role.isBlank()) {
                user.setRole(role.toUpperCase());
            }
            if (newPassword != null && !newPassword.isEmpty()) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public User register(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setIsActive(true);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchByName(String query) {
        if (query == null || query.isBlank()) {
            return getAllUsers();
        }
        return userRepository.searchByName(query.trim());
    }

    public boolean blockUser(Long id) {
        return setActive(id, false);
    }

    public boolean unblockUser(Long id) {
        return setActive(id, true);
    }

    private boolean setActive(Long id, boolean active) {
        return userRepository.findById(id).map(user -> {
            user.setIsActive(active);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public String updateProfile(String currentEmail, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (request.getCurrentPassword() == null
                || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный текущий пароль");
        }

        String newEmail = request.getEmail();
        if (newEmail != null && !newEmail.equalsIgnoreCase(currentEmail)) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email уже занят");
            }
            user.setEmail(newEmail);
        }

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }

        String newPassword = request.getNewPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Новые пароли не совпадают");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user).getEmail();
    }
}
