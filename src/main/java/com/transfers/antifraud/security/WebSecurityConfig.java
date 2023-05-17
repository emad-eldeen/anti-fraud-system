package com.transfers.antifraud.security;

import com.transfers.antifraud.businesslayer.user.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/error").permitAll() // needed to send errors
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // creating new user is a public
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority(Role.ADMINISTRATOR.name(), Role.SUPPORT.name())
                        .requestMatchers("/api/users/*").hasAnyAuthority(Role.ADMINISTRATOR.name())
                        .requestMatchers("/api/anti-fraud/transactions").hasAnyAuthority(Role.MERCHANT.name())
                        .requestMatchers("/api/anti-fraud/suspicious-ips").hasAnyAuthority(Role.SUPPORT.name())
                        .requestMatchers(HttpMethod.DELETE,"/api/anti-fraud/suspicious-ips/**").hasAnyAuthority(Role.SUPPORT.name())
                        .requestMatchers("/api/anti-fraud/stolen-cards").hasAnyAuthority(Role.SUPPORT.name())
                        .requestMatchers(HttpMethod.DELETE,"/api/anti-fraud/stolen-cards/**").hasAnyAuthority(Role.SUPPORT.name())
                        .anyRequest().authenticated() // for any other request, authentication is needed
                )
                .csrf().disable(); // for postman to work
        return http.build();
    }
    // encoder used to check if password matching
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}

