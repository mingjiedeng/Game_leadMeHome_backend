package com.ming.game.security;

import com.ming.game.userdata.User;
import lombok.Data;

@Data
public class UserWithToken {
    private User user;
    private String token;

    public UserWithToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public UserWithToken() {
    }
}
