package com.ming.game.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        Claims claims = authenticateAndGetClaims(token);
        String username = claims.getSubject();
        return (claims != null) && !isTokenExpired(claims) && username != null;
    }

    public Claims authenticateAndGetClaims(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.warn("Jwt validation failed.", e);
            throw e;
        }
        return claims;
    }

    private boolean isTokenExpired(Claims claims) {
        if (claims == null) return true;

        Date expirationDate = claims.getExpiration();
        return expirationDate.before(new Date());
    }

    public String getUsernameFromClaims(Claims claims) {
        return claims.getSubject();
    }

    public List<String> getAuthoritiesFromClaims(Claims claims) {
        return (List<String>) claims.get("authorities");
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return generateToken(claims);
    }

    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Authentication generateAuthenticationFromToken(String token) {
        Claims claims = authenticateAndGetClaims(token);
        String username = getUsernameFromClaims(claims);
        List<GrantedAuthority> authorities = JwtUtils.mapToGrantedAuthorities(getAuthoritiesFromClaims(claims));
        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
        return auth;
    }

    public String refreshToken(String token) {
        Claims claims = authenticateAndGetClaims(token);
        if (isTokenExpired(claims)) {
            return null;
        }
        return generateToken(claims);
    }
}
