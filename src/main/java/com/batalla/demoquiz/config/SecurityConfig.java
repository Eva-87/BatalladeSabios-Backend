package com.batalla.demoquiz.config;

import com.batalla.demoquiz.security.CustomUserDetailsService;
import com.batalla.demoquiz.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtFilter jwtFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowCredentials(true);
                config.addAllowedOrigin("http://localhost:5173");
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");
                config.addExposedHeader("Authorization");
                return config;
            }))
            .authorizeHttpRequests(auth -> auth
                    // 🔓 ENDPOINTS PÚBLICOS
                    .requestMatchers(
                         "/api/auth/login",
                         "/api/auth/register",

                         "/api/quizzes",
                         "/api/quizzes/**",

                         "/api/questions",
                         "/api/questions/**",

                         "/api/rooms/**",
                         "/api/game/**",

                         "/ws/**"
                    ).permitAll()

                    // 🔒 TODO LO DEMÁS REQUIERE TOKEN (pero ya no usas JWT)
                    .anyRequest().permitAll()
            )
            .userDetailsService(customUserDetailsService)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }
}
