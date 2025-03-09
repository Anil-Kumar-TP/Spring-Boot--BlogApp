package com.anil.blog.services.impl;

import com.anil.blog.domain.entities.RefreshToken;
import com.anil.blog.domain.entities.RevokedToken;
import com.anil.blog.domain.entities.User;
import com.anil.blog.domain.entities.VerificationToken;
import com.anil.blog.dtos.AuthResponse;
import com.anil.blog.dtos.SignupRequest;
import com.anil.blog.exceptions.EmailNotVerifiedException;
import com.anil.blog.exceptions.VerificationResendCooldownException;
import com.anil.blog.repositories.RefreshTokenRepository;
import com.anil.blog.repositories.RevokedTokenRepository;
import com.anil.blog.repositories.UserRepository;
import com.anil.blog.repositories.VerificationTokenRepository;
import com.anil.blog.security.BlogUserDetails;
import com.anil.blog.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final Long jwtExpiryMs = 86400000L;
    private final Long refreshExpiryMs = 604800000L; // 7 days
    private final VerificationTokenRepository verificationTokenRepository;

    private static final int RESEND_COOLDOWN_MINUTES = 2; // 2-minute cooldown for resend mail
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    public UserDetails authenticate(String email, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!((BlogUserDetails) userDetails).getUser().isEmailVerified()) {
            throw new EmailNotVerifiedException("Email not verified. Please verify your email before logging in.");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return userDetails;
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claims = new HashMap<>(); //if we had other fields in the User entity
        return Jwts.builder() //apart from the one in UserDetails like phoneNumber,locations.. along with
                .setClaims(claims) //the basic ones in UserDetails like password,authorities..
                .setSubject(userDetails.getUsername())//then casting is needed and add it to claims.
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiryMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        if (revokedTokenRepository.findByToken(token).isPresent()) {
            throw new IllegalArgumentException("Token has been revoked");
        }
        Claims claims = extractClaims(token);
        String username = claims.getSubject();
        return userDetailsService.loadUserByUsername(username);
    }

    private Claims extractClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        if (claims.getExpiration().before(new Date())) {
            throw new ExpiredJwtException(null, claims, "Token has expired");
        }
        return claims;
    }

    @Override
    public AuthResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .posts(new ArrayList<>())
                .emailVerified(false) // Unverified by default
                .build();

        userRepository.save(user);

        // Generate and save verification token
        String verificationToken = UUID.randomUUID().toString();
        VerificationToken tokenEntity = new VerificationToken(user, verificationToken);
        verificationTokenRepository.save(tokenEntity);

        // Send verification email
        sendVerificationEmail(user.getEmail(), verificationToken);

        // Return success message instead of token
        return AuthResponse.builder()
                .token("Verification required") // Placeholder, not a real token
                .expiresIn(0)
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public void revokeToken(String token) {
        if (token != null && revokedTokenRepository.findByToken(token).isEmpty()) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            RevokedToken revokedToken = RevokedToken.builder()
                    .token(token)
                    .revokedAt(LocalDateTime.now())
                    .expiryDate(claims.getExpiration().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                    .build();
            revokedTokenRepository.save(revokedToken);
        }
    }

    @Override
    public String verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken); // Clean up

        return "Email verified successfully. You can now log in.";
    }


    private void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify Your Email");
        message.setText("Please verify your email by clicking this link: " +
                                baseUrl + "/api/v1/auth/verify?token=" + token);
        mailSender.send(message);

    }

    @Override
    public String resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email already verified");
        }

        // Check for existing token and enforce cooldown
        Optional<VerificationToken> existingToken = verificationTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            LocalDateTime lastSent = existingToken.get().getCreatedAt();
            LocalDateTime now = LocalDateTime.now();
            if (lastSent.plusMinutes(RESEND_COOLDOWN_MINUTES).isAfter(now)) {
                long secondsLeft = java.time.Duration.between(now, lastSent.plusMinutes(RESEND_COOLDOWN_MINUTES)).getSeconds();
                throw new VerificationResendCooldownException("Please wait " + secondsLeft + " seconds before requesting a new verification email.");
            }
            verificationTokenRepository.delete(existingToken.get()); // Delete old token
        }

        // Generate and send new token
        String newToken = UUID.randomUUID().toString();
        VerificationToken tokenEntity = new VerificationToken(user, newToken);
        verificationTokenRepository.save(tokenEntity);
        sendVerificationEmail(user.getEmail(), newToken);
        return "Verification email resent successfully.";
    }

//    private String extractUsername(String token){
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.getSubject();
//    } // we were using it before when there was no refresh token present. but now extractClaims take care of it. so this is no longer needed.

    private Key getSigningKey(){
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public AuthResponse generateTokens(UserDetails userDetails) {
        String accessToken = generateToken(userDetails);
        User user = ((BlogUserDetails) userDetails).getUser();
        String refreshToken = generateRefreshToken(user);
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiryMs / 1000)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(tokenEntity);
            throw new IllegalArgumentException("Refresh token has expired");
        }

        User user = tokenEntity.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = generateToken(userDetails);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken) // Reuse existing
                .expiresIn(jwtExpiryMs / 1000)
                .build();
    }

    private String generateRefreshToken(User user) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        existingToken.ifPresent(refreshTokenRepository::delete); // Revoke old token

        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshExpiryMs / 1000))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
