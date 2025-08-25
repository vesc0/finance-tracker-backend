package com.vesco.financetracker.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends HttpFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            // Try cookie named AUTH_TOKEN
            if (request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie c : request.getCookies()) {
                    if ("AUTH_TOKEN".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }
        if (token != null) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(null, token);
            try {
                var auth = authenticationManager.authenticate(authToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
                // Also set attribute for controllers when principal is AppUserPrincipal
                if (auth != null && auth.getPrincipal() instanceof com.vesco.financetracker.security.AppUserPrincipal) {
                    com.vesco.financetracker.security.AppUserPrincipal p = (com.vesco.financetracker.security.AppUserPrincipal) auth
                            .getPrincipal();
                    request.setAttribute("userId", p.getId());
                }
            } catch (Exception e) {
                // Authentication failed; proceed without setting security context
            }
        }
        chain.doFilter(request, response);
    }
}
