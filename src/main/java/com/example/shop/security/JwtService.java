package com.example.shop.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMs = expirationMs;
        log.info("Инициализация JwtService с временем жизни токена {} мс", expirationMs);
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        log.debug("Генерация JWT для пользователя: username={}, expiresAt={}",
                userDetails.getUsername(), expiry);

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        DecodedJWT jwt = decodeToken(token);
        String username = jwt.getSubject();
        log.debug("Из JWT извлечён subject (username)={}", username);
        return username;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        DecodedJWT jwt = decodeToken(token);
        String username = jwt.getSubject();
        Date expiresAt = jwt.getExpiresAt();
        boolean notExpired = (expiresAt == null || expiresAt.after(new Date()));
        boolean matches = username.equals(userDetails.getUsername());

        boolean valid = matches && notExpired;

        log.debug("Проверка валидности JWT: usernameFromToken={}, usernameExpected={}, notExpired={}, valid={}",
                username, userDetails.getUsername(), notExpired, valid);

        return valid;
    }

    private DecodedJWT decodeToken(String token) {
        log.debug("Декодирование и проверка JWT");
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }
}
