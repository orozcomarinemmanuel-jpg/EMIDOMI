package com.emidomi.emidomi_cliente.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:emidomi_secret_key_32_bytes_long_!!}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(String correo, Integer idCliente) {
        return Jwts.builder()
                .subject(correo)
                .claim("idCliente", idCliente)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String getCorreoFromToken(String token) {
        return getClaims(token).getPayload().getSubject();
    }

    public Integer getIdClienteFromToken(String token) {
        return getClaims(token).getPayload().get("idCliente", Integer.class);
    }

    public boolean isTokenValido(String token) {
        try {
            Claims claims = getClaims(token).getPayload();
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }
}
