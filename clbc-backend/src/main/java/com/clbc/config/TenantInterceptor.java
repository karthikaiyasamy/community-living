package com.clbc.config;

import com.clbc.util.JwtUtil;
import com.clbc.util.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String tenantId = jwtUtil.extractTenantId(jwt);
            String headerTenantId = request.getHeader("X-Tenant-ID");

            if (tenantId != null) {
                if (headerTenantId != null && !headerTenantId.equals(tenantId)) {
                    throw new RuntimeException("Tenant ID mismatch");
                }
                TenantContext.setTenantId(tenantId);
            }
        } else if (request.getHeader("X-Tenant-ID") != null) {
            TenantContext.setTenantId(request.getHeader("X-Tenant-ID"));
        }
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex) {
        TenantContext.clear();
    }
}
