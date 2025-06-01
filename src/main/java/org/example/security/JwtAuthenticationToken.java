package org.example.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    // Constructor for the JwtAuthenticationToken
    public JwtAuthenticationToken(String token) {
        super(new ArrayList<>()); // No authorities assigned yet
        this.token = token;
        setAuthenticated(false); // The token is not authenticated until it's validated
    }

    // Constructor when authentication is already confirmed (e.g., after token validation)
    public JwtAuthenticationToken(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        setAuthenticated(true); // Token has been authenticated
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token; // Return token as the principal for now; you could change it to a user object later
    }
}
