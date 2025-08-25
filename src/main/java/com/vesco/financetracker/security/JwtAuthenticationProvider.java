package com.vesco.financetracker.security;

import com.vesco.financetracker.util.JwtUtil;
import com.vesco.financetracker.entity.User;
import com.vesco.financetracker.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    public JwtAuthenticationProvider(JwtUtil jwtUtil, UserRepository userRepository,
            org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object credentials = authentication.getCredentials();
        if (credentials == null) {
            throw new BadCredentialsException("No JWT token provided");
        }
        String token = credentials.toString();
        if (!jwtUtil.isTokenValid(token)) {
            throw new BadCredentialsException("Invalid JWT token");
        }
        Long userId = jwtUtil.extractUserId(token);
        if (userDetailsService != null) {
            var userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
            return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found for token"));
        var principal = new com.vesco.financetracker.security.AppUserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(principal, token, AuthorityUtils.NO_AUTHORITIES);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
