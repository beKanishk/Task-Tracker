package com.task.tracker.authentication.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.authentication.dto.AuthRequest;
import com.task.tracker.dto.UserRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addNewUser(@Valid @RequestBody UserRequestDTO user) {
        return authService.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@Valid @RequestBody AuthRequest authRequest) throws Exception {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        if (authenticate.isAuthenticated()) {
            return authService.generateToken(authRequest.getUsername());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        if (authService.validateToken(token))
            return "Token is valid";
        return "Invalid Token";
    }

    @PutMapping("/role")
    public String updateUserRole(@RequestParam("username") String username, @RequestParam("role") String role) {
        return authService.updateUserRole(username, role);
    }

    @PutMapping("/remove/role")
    public ResponseEntity<String> removeUserRole(@RequestParam("username") String username, @RequestParam("role") String role) {
        return new ResponseEntity<>(authService.updateUserRole(username, role), HttpStatus.OK);
    }
}
