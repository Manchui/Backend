package com.manchui.domain.auth.service;

import com.manchui.domain.auth.dto.CustomUserDetails;
import com.manchui.domain.user.entity.User;
import com.manchui.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User findUser = userRepository.findByEmail(email);

        if (findUser != null) {
            return new CustomUserDetails(findUser);
        }

        return null;
    }
}
