package edu.icet.service;

import edu.icet.client.UserClient;
import edu.icet.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserClient userClient;

    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public List<User> getAllUsers() {
        return userClient.getUsers();
    }
    public User getUserById(int id) {
        return userClient.getUserById(id);
    }
}
