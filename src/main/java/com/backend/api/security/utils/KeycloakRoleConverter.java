package com.backend.api.security.utils;

import com.backend.api.security.error.SecurityErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final Logger logger = LoggerFactory.getLogger(SecurityErrorHandler.class);

    @Override
    public Collection<GrantedAuthority> convert(final Jwt jwt) {
        final Map<String, Object> claims = jwt.getClaims();
        logger.debug(claims.toString());
        @SuppressWarnings("unchecked")
        final Map<String, List<String>> resourceAccess = (Map<String, List<String>>) claims.getOrDefault("realm_access", emptyMap());
        return resourceAccess.getOrDefault("roles", emptyList()).stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
    }

    public enum rolesEnum {ADMIN, USER}
}