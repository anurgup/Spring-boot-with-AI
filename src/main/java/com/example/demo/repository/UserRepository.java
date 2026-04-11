package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final Map<Integer, User> store = new HashMap<>();
    private int nextId = 1;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        store.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsById(Integer id) {
        return store.containsKey(id);
    }
}
