package com.techacademy;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin(login -> login.loginProcessingUrl("/login")
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error")
                .permitAll()
        ).logout(logout -> logout.logoutSuccessUrl("/login")
        ).authorizeHttpRequests(auth -> auth
                .requestMatchers("/login/createAccount").permitAll()
                .requestMatchers("/password/reset").permitAll()
                .requestMatchers("/password/reset/completion").permitAll()
                .requestMatchers("/login/**", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .anyRequest().authenticated()
        ).oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/cars/list", true)
                .permitAll());



        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
