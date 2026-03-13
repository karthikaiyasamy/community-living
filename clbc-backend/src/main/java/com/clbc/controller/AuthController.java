package com.clbc.controller;

import com.clbc.dto.AuthRequest;
import com.clbc.dto.AuthResponse;
import com.clbc.dto.SystemAuthRequest;
import com.clbc.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/system/token")
    public ResponseEntity<AuthResponse> systemLogin(@RequestBody SystemAuthRequest request) {
        return ResponseEntity.ok(authService.loginSystem(request));
    }
}
