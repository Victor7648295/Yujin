package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.RegistrationRequest;
import org.marketplace.marketplace.project.model.User;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
