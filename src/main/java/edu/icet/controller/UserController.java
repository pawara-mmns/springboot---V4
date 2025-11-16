package edu.icet.controller;

import edu.icet.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.icet.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") int id){
        return userService.getUserById(id);
    }

}
