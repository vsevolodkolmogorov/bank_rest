package com.example.bankcards.config;

import com.example.bankcards.exception.security.CustomAccessDeniedHandler;
import com.example.bankcards.exception.security.CustomAuthenticationEntryPoint;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    private final CustomAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/**",
            "/api/auth/me"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        // - USER -
                        .requestMatchers( "/api/user/**").hasRole("ADMIN")
                        // - CARD -
                        .requestMatchers(HttpMethod.GET, "/api/card/my").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/card/*/my").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/card/transfer").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/card/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/card/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/card/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/card/**").hasRole("ADMIN")
                        // - CARD BLOCK -
                        .requestMatchers(HttpMethod.GET, "/api/cardBlock/my").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/cardBlock/*/my").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cardBlock").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/cardBlock/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cardBlock/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/cardBlock/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling()
                    .authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
