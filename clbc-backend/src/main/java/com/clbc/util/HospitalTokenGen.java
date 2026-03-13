package com.clbc.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to generate a long-lived System Integration token for hospital
 * engineers.
 * Run this to get a token that can be used in Postman for FHIR ingestion.
 */
public class HospitalTokenGen {
    public static void main(String[] args) {
        // The secret from application.properties
        String secret = "MzRhYmNkZWYxMjM0NTY3ODkwYWJjZGVmMTIzNDU2Nzg5MGFiY2RlZjEyMzQ1Njc4OTBhYmNkZWYxMjM0NTY3OA==";

        // Settings for System Integration
        String username = "hospital-system-gateway";
        String role = "SYSTEM_INTEGRATION";
        String tenantId = "rose-living"; // The community ID

        // 1 Year Expiration (for dev/sim purposes)
        long expiration = 365L * 24 * 60 * 60 * 1000;

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);
        extraClaims.put("tenantId", tenantId);

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        System.out.println("\n--- CLBC HOSPITAL SYSTEM TOKEN ---");
        System.out.println("Role: " + role);
        System.out.println("Community: " + tenantId);
        System.out.println("\nToken (Bearer):\n" + token);
        System.out.println("\nUse this in Postman Authorization header as 'Bearer <token>'\n");
    }
}
