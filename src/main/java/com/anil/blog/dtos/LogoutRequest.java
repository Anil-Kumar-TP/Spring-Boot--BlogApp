package com.anil.blog.dtos;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
    private String token;
}