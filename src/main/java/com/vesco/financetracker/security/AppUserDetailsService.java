package com.vesco.financetracker.security;

import com.vesco.financetracker.entity.User;
import com.vesco.financetracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.parseLong(username);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user id: " + username);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUserPrincipal(user);
    }
}
