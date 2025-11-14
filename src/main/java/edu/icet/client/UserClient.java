package edu.icet.client;

import edu.icet.model.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/users")
public interface UserClient {
    @GetExchange
    List <User> getUsers();

    @GetExchange("/{id}")
    User getUserById(@PathVariable int id);

}
