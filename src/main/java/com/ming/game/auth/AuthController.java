package com.ming.game.auth;

import com.ming.game.security.UserWithToken;
import com.ming.game.userdata.User;
import com.ming.game.userdata.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService  userService;

    @PostMapping("${jwt.routes.authenticate}")
    public UserWithToken login(@RequestBody User formData) {
        String token = authService.login(formData.getUsername(), formData.getPassword());
        User user = userService.getUserByName(formData.getUsername());
        return new UserWithToken(user, token);
    }

    @PostMapping("${jwt.routes.register}")
    public UserWithToken register(@RequestBody User formData) {
        String username = formData.getUsername();
        String password = formData.getPassword();
        User user = authService.register(formData);
        String token = authService.login(username, password);
        return new UserWithToken(user, token);
    }

    @GetMapping("${jwt.routes.refresh}")
    public User refresh(@RequestBody User user) {
        return authService.register(user);
    }
}
