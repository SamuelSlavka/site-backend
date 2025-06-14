package com.backend.api.security.config;

import com.backend.api.security.error.SecurityErrorHandler;
import com.backend.api.security.utils.KeycloakRoleConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final SecurityErrorHandler errorHandler;

    @Bean
    protected SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(r -> r.requestMatchers(HttpMethod.GET, "/api/v1/articles**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/articles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/sections**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/sections/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/measurements**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/measurements/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/devices**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/devices/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/scheduled**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/scheduled/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/measurements**").permitAll()
                        .requestMatchers("/game/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/measurements/**").permitAll().anyRequest()
                        .authenticated()).exceptionHandling(e -> e.authenticationEntryPoint(errorHandler))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtConverter())));


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://samsla.org"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtConverter() {
        final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return jwtAuthenticationConverter;
    }
}