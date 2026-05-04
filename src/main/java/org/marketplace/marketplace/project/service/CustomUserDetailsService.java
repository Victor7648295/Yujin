package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.User;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security {@link UserDetailsService}: ищет пользователя по
 * email и формирует authority {@code ROLE_<ROLE>} на основе поля
 * {@code role} в {@link User}.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с email: " + email));

        String role = user.getRole();
        if (role == null || role.isBlank()) {
            role = "USER";
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
        );
    }
}