package com.ming.game.userdata;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Arrays;
import java.util.List;

@Data
public class User {
    @Id
    private String id;

    @Indexed(unique=true)
    private String username;
    private String password;
    private List<String> roles;
    private List<Score> scores;

    public User(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String password) {
        this(username, password, Arrays.asList("ROLE_USER"));
    }

    public User() {
        this.roles = Arrays.asList("ROLE_USER");
    }
}
