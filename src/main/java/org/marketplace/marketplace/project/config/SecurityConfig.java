package org.marketplace.marketplace.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()  // В 2.7.x используется authorizeRequests() вместо authorizeHttpRequests()
                .antMatchers("/login", "/css/**", "/js/**", "/register").permitAll()  // antMatchers вместо requestMatchers
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
                .and()
                .csrf()
                .ignoringAntMatchers("/login");  // Опционально: отключить CSRF для логина

        return http.build();
    }

    // Пример пользователей в памяти (для теста)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("123/*---")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

}
