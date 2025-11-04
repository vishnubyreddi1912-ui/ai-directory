package com.aihub.directory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private static final List<String> ALLOWED_EMAILS = List.of(
            "vishnu@gmail.com",
            "vishnubyreddi11@gmail.com",
            "Vudathupavankalyan@gmail.com",
            "vudathupavankalyan@gmail.com",
            "Gopalvudathu3012@gmail.com",
            "gopalvudathu3012@gmail.com",
            "byreddi.sricharan@gmail.com",
            "Byreddi.sricharan@gmail.com"
    );

    /**
     * ✅ Main Spring Security configuration.
     * Adds ActivityLoggingFilter after JWT authentication has completed.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ActivityLoggingFilter activityLoggingFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/ai-tools/import/**",
                                "/api/ai/import/**",
                                "/api/proscons/import/**"
                        ).hasAuthority("AUTHORIZED_USER")
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                // ✅ Register your ActivityLoggingFilter AFTER JWT authentication
                .addFilterAfter(activityLoggingFilter, AnonymousAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ Google public key JWT decoder.
     * Verifies tokens issued by https://accounts.google.com
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = "https://www.googleapis.com/oauth2/v3/certs";
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("https://accounts.google.com"));
        return decoder;
    }

    /**
     * ✅ Converts JWT claims into Spring Security authorities.
     * Gives "AUTHORIZED_USER" role to whitelisted emails.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String email = jwt.getClaimAsString("email");
            if (email != null && ALLOWED_EMAILS.contains(email)) {
                return List.of(new SimpleGrantedAuthority("AUTHORIZED_USER"));
            }
            return List.of();
        });

        return converter;
    }
}
