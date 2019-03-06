package com.ming.game;

import com.ming.game.gamedata.GameData;
import com.ming.game.security.UserWithToken;
import com.ming.game.userdata.Score;
import com.ming.game.userdata.User;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GameApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MongoTemplate mongo;

    private String username;
    private String password;

    @Before
    public void setup() {
        username = "testUsr";
        password = "testPwd";
        mongo.remove(new Query(Criteria.where("username").is("newRegUsr")), User.class);
        mongo.remove(new Query(Criteria.where("username").is(username)), User.class);

        User user = new User(username, encoder.encode(password));
        mongo.insert(user, "user");
    }

    @Test
    public void whenRegister_thenGetUserAndToken() {
        User newUser = new User("newRegUsr", "testPwd");

        ResponseEntity<UserWithToken> response = restTemplate.postForEntity("/auth/register", newUser, UserWithToken.class);

        assertThat(response.getBody()).extracting(r -> r.getUser().getUsername()).isEqualTo("newRegUsr");
        assertThat(response.getBody()).extracting(r -> r.getToken()).isNotNull();
    }

    @Test
    public void givenAnAuthorizedToken_whenRequestUserInform_thenReturnUser() {
        HttpHeaders headers = headerWithToken(username, password);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange("/user/"+username, HttpMethod.GET, httpEntity, User.class);

        assertThat(response.getBody()).extracting(r -> r.getUsername()).isEqualTo(username);
    }

    private HttpHeaders headerWithToken(String username, String password) {
        String token = getToken(username, password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    private String getToken(String username, String password) {
        User newUser = new User(username, password);
        ResponseEntity<UserWithToken> response = restTemplate.postForEntity("/auth", newUser, UserWithToken.class);
        UserWithToken userWithToken = response.getBody();
        return userWithToken.getToken();
    }

    @Test
    public void givenAnAuthorizedToken_whenPostScore_thenReturnUserWithUpdatedScore() {
        Score score = new Score(1, 2, 3, 1);
        HttpHeaders headers = headerWithToken(username, password);
        HttpEntity<Score> httpEntity = new HttpEntity<>(score, headers);

        ResponseEntity<User> response = restTemplate.exchange("/user/"+username+"/score", HttpMethod.PATCH, httpEntity, User.class);

        assertThat(response.getBody()).extracting(r -> r.getScores().get(0).getSeconds()).isEqualTo(3);
    }

    @Test
    public void whenLoginSuccess_thenReturnUserAndToken() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(params,headers);

        ResponseEntity<UserWithToken> response = restTemplate.postForEntity("/auth", httpEntity, UserWithToken.class);

        assertThat(response.getBody()).extracting(r -> r.getUser().getUsername()).isEqualTo(username);
        assertThat(response.getBody()).extracting(r -> r.getToken()).isNotNull();
    }
}

