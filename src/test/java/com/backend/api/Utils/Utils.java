package com.backend.api.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private Utils() {

    }

    public static JwtAuthenticationToken getMockJwtToken(String role, String subject){
        final var roles = new JSONArray();
        roles.put(role);

        final Map<String, JSONArray> jsonMap = new HashMap<>();
        jsonMap.put("roles", roles);

        final var claimObject = new JSONObject(jsonMap);
        final var jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", claimObject)
                .subject(subject)
                .build();
        final  java.util.Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        return new JwtAuthenticationToken(jwt, authorities);
    }
}