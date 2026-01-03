package com.task.tracker.authentication.controller;

import com.task.tracker.authentication.service.AuthService;
import com.task.tracker.authentication.dto.AuthRequest;
import com.task.tracker.dto.UserRequestDTO;
import com.task.tracker.model.User;
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
    public String addNewUser(@RequestBody UserRequestDTO user) {
        return authService.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) throws Exception {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            return authService.generateToken(authRequest.getUsername());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

//    @PostMapping("/token")
//    public String getToken(@RequestBody AuthRequest authRequest) throws Exception {
//
//        try {
//            Authentication auth =
//                    authenticationManager.authenticate(
//                            new UsernamePasswordAuthenticationToken(
//                                    authRequest.getUsername(),
//                                    authRequest.getPassword()
//                            )
//                    );
//
//            if (auth.isAuthenticated()) {
//                return authService.generateToken(authRequest.getUsername());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();   // TEMP logging
//        }
//
//        throw new RuntimeException("Invalid login credentials");
//    }


    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        if(authService.validateToken(token))
            return "Token is valid";

        return "Invalid Token";
    }

    @PutMapping("/role")
    public String updateUserRole(@RequestParam("username") String username, @RequestParam("role") String role) {
        return authService.updateUserRole(username, role);
    }

    @PutMapping("/remove/role")
    public ResponseEntity<String> removeUserRole(@RequestParam("username") String username, @RequestParam("role") String role) {
        String response = authService.updateUserRole(username, role);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
