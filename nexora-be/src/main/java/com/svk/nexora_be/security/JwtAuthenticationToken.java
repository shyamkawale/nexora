package com.svk.nexora_be.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwtToken;

    public JwtAuthenticationToken(String jwtToken) {
        super(Collections.emptyList());
        this.jwtToken = jwtToken;
        setAuthenticated(false);
    }

    public String getJwtToken() {
        return jwtToken;
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}

