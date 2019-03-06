package com.ming.game.security;

import com.ming.game.userdata.User;
import org.springframework.stereotype.Component;

@Component
public class JwtUserFactory {
    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                JwtUtils.mapToGrantedAuthorities(user.getRoles())
        );
    }
}
