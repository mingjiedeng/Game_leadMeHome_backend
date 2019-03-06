package com.ming.game.userdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getAllUser() {
        return service.getAllUsers();
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.principal")
    @GetMapping("/{name}")
    public User getUser(@PathVariable String name) {
        return service.getUserByName(name);
    }

    @GetMapping("/topList")
    public List<TopList> getTopLists() {
        return service.getTopLists();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User addUser(@RequestBody User user) {
        return service.addUser(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{name}")
    public User updateUser(@PathVariable String name, @RequestBody User user) {
        return service.updateUser(name, user);
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.principal")
    @PatchMapping("/{name}/score")
    public User updateScore(@PathVariable String name, @RequestBody Score score) {
        return service.updateScore(name, score);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{name}")
    public User removeUser(@PathVariable String name) {
        return service.removeUser(name);
    }
}
