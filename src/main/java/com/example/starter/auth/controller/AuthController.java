package com.example.starter.auth.controller;

// AuthController.java

import com.example.starter.auth.entity.User;
import com.example.starter.auth.service.CognitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CognitoService cognitoService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User userRequest) {
        return cognitoService.registerUser(
                userRequest.getEmail(),
                userRequest.getPassword()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        return cognitoService.loginUser(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
    }
}

