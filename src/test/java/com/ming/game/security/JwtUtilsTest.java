package com.ming.game.security;

import com.ming.game.userdata.User;
import io.jsonwebtoken.Claims;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class JwtUtilsTest {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JwtUserFactory userFactory;

    private String token;

    @Before
    public void setup() {
        UserDetails user = userFactory.create(
                new User("John", "myPassword", Arrays.asList("ROLE_USER", "ROLE_ADMIN")));

        token = jwtUtils.generateToken(user);
    }

    @Test
    public void validateToken() {
        assertThat(jwtUtils.validateToken(token), is(true));
    }

    @Test
    public void getUsernameFromToken() {
        Claims claims = jwtUtils.authenticateAndGetClaims(token);
        assertThat(jwtUtils.getUsernameFromClaims(claims), is("John"));
    }

    @Test
    public void getAuthoritiesFromToken() {
        Claims claims = jwtUtils.authenticateAndGetClaims(token);
        assertThat(jwtUtils.getAuthoritiesFromClaims(claims), hasSize(2));
    }
}
