package com.ming.game.userdata;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserByName(String name);
    User addUser(User user);
    User updateUser(String name, User user);
    User removeUser(String name);
    User updateScore(String name, Score score);
    List<TopList> getTopLists();
}
