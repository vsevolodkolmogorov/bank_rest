package com.example.bankcards.security;

import com.example.bankcards.exception.ParseTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;
    private final long EXPIRATION_MS = 86400000;

    private SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Generated token for user: {}", userDetails.getUsername());
        return token;
    }

    @Override
    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            log.info("Extracting username from token {}", username);
            return username;
        } catch (JwtException e) {
            throw new ParseTokenException("Не удалось извлечь имя пользователя из токена");
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        boolean valid = username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        log.info("Token is {} valid for user: {}", valid, userDetails.getUsername());
        return valid;
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            boolean expired = expiration.before(new Date());

            log.info("Token is {} expired", expired);
            return expired;
        } catch (JwtException e) {;
            throw new ParseTokenException("Не удалось извлечь имя пользователя из токена");
        }
    }
}
