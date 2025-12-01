package com.example.kromannreumert.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/status/healthz",
                                "/h2-console/**")
                        .permitAll()

                        .requestMatchers("/api/v1/client/**").hasAnyRole("ADMIN", "PARTNER", "SAGSBEHANDLER") // <--- 2) This
                        .requestMatchers("/api/v1/cases/**").hasAnyRole("PARTNER", "SAGSBEHANDLER")
                        .requestMatchers("/api/v1/todos/**").hasAnyRole("PARTNER", "SAGSBEHANDLER", "JURIST")
                        .requestMatchers("/api/v1/role/**", "/api/v1/user/**").hasRole("ADMIN")

                        // Had to change the order for it to work dont know why

                        .requestMatchers(HttpMethod.GET, "/api/v1/client/**").hasRole("SAGSBEHANDLER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/cases/**").hasRole("JURIST")

                        .requestMatchers("/api/v1/**").hasRole("ADMIN") // <--- 1) This doesn't work without

                        .anyRequest().authenticated())


                .oauth2ResourceServer(
                        oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        
    return http.build();
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return authenticationConverter;
    }
}
