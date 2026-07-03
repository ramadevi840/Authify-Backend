package com.authify.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authify.entity.UserEntity;
import com.authify.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity existingUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));

        // ✅ Return Spring Security User object
        return new User(
            existingUser.getEmail(),
            existingUser.getPassword(),
            new ArrayList<>() // empty authorities for now
        );
    }
}

