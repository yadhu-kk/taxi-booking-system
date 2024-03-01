package com.edstem.taxibookingandbillingsystem.security;

import com.edstem.taxibookingandbillingsystem.model.User;
import com.edstem.taxibookingandbillingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            return new UserInfoUserDetails(user);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
