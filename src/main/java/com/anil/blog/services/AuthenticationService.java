package com.anil.blog.services;

import com.anil.blog.dtos.AuthResponse;
import com.anil.blog.dtos.SignupRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {

    UserDetails authenticate(String email,String password);

    String generateToken(UserDetails userDetails);

    UserDetails validateToken(String token);

    AuthResponse signup(SignupRequest signupRequest);

    String verifyEmail(String token);

}
