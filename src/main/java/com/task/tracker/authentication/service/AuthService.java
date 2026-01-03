package com.task.tracker.authentication.service;

import com.task.tracker.dto.UserRequestDTO;
import com.task.tracker.dto.UserResponseDTO;
import com.task.tracker.model.User;
import com.task.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public String saveUser(UserRequestDTO dto){
        User user = User.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles(List.of("USER"))
                .build();

//        user.setRoles(List.of("MESSAGE_READER"));
        userRepository.save(user);
        return "User added to the system";
    }

    public String updateUserRole(String username, String newRole) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles();
        if (!roles.contains(newRole)) {
            roles.add(newRole);
            user.setRoles(roles);
            userRepository.save(user);
        }

        return "Role " + newRole + " assigned to user " + username;
    }

    public String removeUserRole(String username, String role) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = user.getRoles();
        if (roles.contains(role)) {
            roles.remove(role);
            user.setRoles(roles);
            userRepository.save(user);
        }
        return "Role " + role + " removed from user " + username;
    }

    public boolean validateToken(String token){
        if(jwtService.validateToken(token)){
            return true;
        }
        return false;
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String username = jwtService.extractUsername(token);
//        if(jwtService.validateToken(token)){
//            return true;
//        }
        return username.equals(userDetails.getUsername()) && jwtService.validateToken(token);
    }

    public String generateToken(String username) throws Exception {
        return jwtService.generateToken(username);
    }

    public UserResponseDTO getUserFromToken(String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponseDTO.builder()
                .userName(user.getUserName())
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .maxStreak(user.getMaxStreak())
                .build();
    }
}

