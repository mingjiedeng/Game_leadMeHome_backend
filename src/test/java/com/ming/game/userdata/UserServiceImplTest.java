package com.ming.game.userdata;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService service;

    @Autowired
    private MongoTemplate mongo;
    private String username;

    @Before
    public void setup() {
        username = "userServiceTest";
        mongo.remove(new Query(Criteria.where("username").is(username)), User.class);

        Score score = new Score(1, 3, 7, 1);
        User user = new User(username, "testPwd");
        user.setScores(Arrays.asList(score));
        mongo.save(user, "user");
    }

    @Test
    public void givenANewScore_whenUpdate_thenAddScore() {
        Score score = new Score(2, 4, 9, 1);
        User user = service.updateScore(username, score);

        assertThat(user.getScores().size()).isEqualTo(2);
    }

    @Test
    public void givenAWorseScore_whenUpdate_thenDoNothing() {
        Score score1 = new Score(1, 3, 10, 1);
        Score score2 = new Score(1, 2, 5, 1);
        User user = service.updateScore(username, score1);
        user = service.updateScore(username, score2);

        assertThat(user.getScores().get(0).getSeconds()).isEqualTo(7);
    }

    @Test
    public void givenABetterScore_whenUpdate_thenDoUpdate() {
        Score score = new Score(1, 4, 20, 1);
        User user = service.updateScore(username, score);

        assertThat(user.getScores().get(0).getSeconds()).isEqualTo(20);
    }
}
