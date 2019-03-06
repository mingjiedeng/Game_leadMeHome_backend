package com.ming.game.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.header}")
    private String headerName;

    @Value("${jwt.tokenSchema}")
    private String schema;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(headerName);
        log.debug("Got Authorization Header: {}", header);

        if(header != null && header.startsWith(schema)) {
            String token = header.substring(schema.length());
            try {
                if (jwtUtils.validateToken(token)) {
                    Authentication auth = jwtUtils.generateAuthenticationFromToken(token);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("Validation passed. Got authentication with username: ", auth.getName());
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                log.warn("Jwt validation failed.", e);
            }
        }

        chain.doFilter(request, response);
    }
}
