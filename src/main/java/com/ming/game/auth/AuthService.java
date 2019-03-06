package com.ming.game.auth;

import com.ming.game.userdata.User;

public interface AuthService {
    User register(User user);
    String login(String username, String password);
    String refresh(String token);
}
