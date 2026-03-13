package com.clbc.service;

import com.clbc.dto.AuthRequest;
import com.clbc.dto.AuthResponse;
import com.clbc.dto.SystemAuthRequest;
import com.clbc.model.SystemClient;
import com.clbc.model.User;
import com.clbc.repository.SystemClientRepository;
import com.clbc.repository.UserRepository;
import com.clbc.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final SystemClientRepository systemClientRepository;
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;
        private final AuditService auditService;
        private final PasswordEncoder passwordEncoder;

        public AuthResponse login(AuthRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getEmail(),
                                                        request.getPassword()));

                        User user = userRepository.findByEmail(request.getEmail())
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                        auditService.logLogin(user.getEmail(), true, null, null);

                        String jwtToken = jwtUtil.generateToken(user.getUsername(), user.getRole().name(),
                                        user.getCommunityId());

                        return AuthResponse.builder()
                                        .accessToken(jwtToken)
                                        .user(AuthResponse.UserData.builder()
                                                        .id(user.getId())
                                                        .email(user.getEmail())
                                                        .role(user.getRole().name())
                                                        .communityId(user.getCommunityId())
                                                        .build())
                                        .build();
                } catch (AuthenticationException e) {
                        auditService.logLogin(request.getEmail(), false, null, e.getMessage());
                        throw e;
                }
        }

        public AuthResponse loginSystem(SystemAuthRequest request) {
                SystemClient client = systemClientRepository.findById(request.getClientId())
                                .orElseThrow(() -> new RuntimeException("System client not found"));

                if (!passwordEncoder.matches(request.getClientSecret(), client.getClientSecretHash())) {
                        auditService.logLogin(request.getClientId(), false, null, "Invalid client secret");
                        throw new RuntimeException("Invalid credentials");
                }

                auditService.logLogin(client.getId(), true, null, "System login success");

                String jwtToken = jwtUtil.generateToken(client.getId(), "SYSTEM_INTEGRATION", client.getCommunityId());

                return AuthResponse.builder()
                                .accessToken(jwtToken)
                                .user(AuthResponse.UserData.builder()
                                                .id(client.getId())
                                                .email(client.getClientName())
                                                .role("SYSTEM_INTEGRATION")
                                                .communityId(client.getCommunityId())
                                                .build())
                                .build();
        }
}
