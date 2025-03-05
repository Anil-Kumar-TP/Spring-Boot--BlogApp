package com.anil.blog.controllers;

import com.anil.blog.dtos.AuthResponse;
import com.anil.blog.dtos.LoginRequest;
import com.anil.blog.dtos.SignupRequest;
import com.anil.blog.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        UserDetails userDetails = authenticationService.authenticate(loginRequest.getEmail(),loginRequest.getPassword());
        String tokenValue = authenticationService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder().
                token(tokenValue).
                expiresIn(86400)
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest signupRequest) {
        AuthResponse authResponse = authenticationService.signup(signupRequest);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED); // 201 Created
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok(authenticationService.verifyEmail(token));
    }
}
