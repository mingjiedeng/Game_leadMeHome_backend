package com.ming.game.userdata;

import com.ming.game.gamedata.GameDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class UserServiceImpl implements UserService {
    private MongoTemplate mongo;
    private GameDataService gameDataService;

    @Autowired
    public UserServiceImpl(MongoTemplate mongo, GameDataService gameDataService) {
        this.mongo = mongo;
        this.gameDataService = gameDataService;
    }

    @Override
    public List<User> getAllUsers() {
        return mongo.findAll(User.class);
    }

    @Override
    public User getUserByName(String name) {
        return mongo.findOne(new Query(Criteria.where("username").is(name)), User.class);
    }

    @Override
    public User addUser(User user) {
        return mongo.save(user);
    }

    @Override
    public User updateUser(String name, User user) {
        user.setId(getUserByName(name).getId());
        return mongo.save(user);
    }

    @Override
    public User removeUser(String name) {
        User user = getUserByName(name);
        mongo.remove(user);
        return user;
    }

    @Override
    public User updateScore(String name, Score score) {
        assessStars(score);

        Query findUser = new Query(Criteria.where("username").is(name));
        Query findScore = new Query(Criteria.where("username").is(name).and("scores.level").is(score.getLevel()));
        Update insertScore = new Update().push("scores", score);
        Update replaceScore = new Update().set("scores.$", score);

        boolean hasRecord = mongo.exists(findScore, User.class);
        if (hasRecord) {
            if (scoreIsBetter(name, score))
                mongo.updateFirst(findScore, replaceScore, User.class);
        } else {
            mongo.updateFirst(findUser, insertScore, User.class);
        }
        return getUserByName(name);
    }

    @Override
    public List<TopList> getTopLists() {
        List<TopList> topLists = new ArrayList<>();
        int levels = gameDataService.getLevelsAmount();
        List<TopScore> scores;
        for (int i = 1; i <= levels ; i++) {

            /* test in mongo shell
            db.user.aggregate([
                    {$unwind: "$scores"},
                    {$match: {"scores.level": 1}},
                    {$project: {_id: 0, username: 1, "saved": "$scores.saved", "seconds": "$scores.seconds"}},
                    {$sort: {"saved": -1, "seconds": 1}},
                    {$limit: 100} ])
            */
            Aggregation aggregation = newAggregation(
                    unwind("scores"),
                    match(new Criteria("scores.level").is(i)),
                    project().and("username").as("name")
                            .and("scores.saved").as("saved")
                            .and("scores.seconds").as("seconds")
                            .andExclude("_id"),
                    sort(Sort.Direction.DESC, "saved")
                            .and(Sort.Direction.ASC, "seconds"),
                    limit(100)
            );
            AggregationResults<TopScore> result = mongo
                    .aggregate(aggregation, "user", TopScore.class);
            scores= result.getMappedResults();
            if (scores.size() != 0) {
                topLists.add(new TopList(i, scores));
            }
        }
        return topLists;
    }

    private void assessStars(Score score) {
        if (score == null) return;
        int saved = score.getSaved();
        if (saved >= 10) {
            score.setStar(3);
        } else if (saved >= 8) {
            score.setStar(2);
        } else if (saved <= 0) {
            score.setStar(0);
        } else {
            score.setStar(1);
        }
    }

    private boolean scoreIsBetter(String name, Score newScore) {
        Aggregation aggregation = newAggregation(
                match(new Criteria("username").is(name)),
                unwind("scores"),
                match(new Criteria("scores.level").is(newScore.getLevel())),
                replaceRoot("scores")
        );
        AggregationResults<Score> result = mongo
                .aggregate(aggregation, "user", Score.class);
        Score originScore = result.getUniqueMappedResult();
        boolean isBetter = newScore.getSaved() == originScore.getSaved()
                ? newScore.getSeconds() < originScore.getSeconds()
                : newScore.getSaved() > originScore.getSaved();
        return isBetter;
    }
}
