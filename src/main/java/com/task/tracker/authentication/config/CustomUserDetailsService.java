package com.task.tracker.authentication.config;

import com.task.tracker.model.User;
import com.task.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> details = userRepository.findByUserName(username);

        if (details.isEmpty()) {
            throw new UsernameNotFoundException("User not found with name: " + username);
        }

        User user = details.get();
        return new MappingUserDetails(user);
    }

}