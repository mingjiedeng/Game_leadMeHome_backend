package com.ming.game.auth;

import com.ming.game.exception.UserAlreadyExistAuthenticationException;
import com.ming.game.security.JwtUtils;
import com.ming.game.userdata.Score;
import com.ming.game.userdata.User;
import com.ming.game.userdata.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static java.util.Arrays.asList;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    @Qualifier("jwtUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder encoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public User register(User user) {
        String name = user.getUsername();
        if (userService.getUserByName(name) != null) {
            throw new UserAlreadyExistAuthenticationException("Username " + name + " has been registered.");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRoles(asList("ROLE_USER"));
        user.setScores(new ArrayList<Score>());
        return userService.addUser(user);
    }

    @Override
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken toAuthToken = new UsernamePasswordAuthenticationToken(username, password);

        // A sort of AuthenticationException will be thrown if authentication fails
        Authentication authentication = authenticationManager.authenticate(toAuthToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtUtils.generateToken(userDetails);
        return token;
    }

    @Override
    public String refresh(String token) {
        return jwtUtils.refreshToken(token);
    }
}
