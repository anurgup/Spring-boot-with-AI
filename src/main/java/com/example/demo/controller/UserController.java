package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.save(user);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        if (updates.containsKey("age") && updates.get("age") != null) {
            int age;
            try {
                age = (Integer) updates.get("age");
            } catch (ClassCastException e) {
                age = ((Number) updates.get("age")).intValue();
            }
            if (age < 21) {
                return ResponseEntity.badRequest().body("Age cannot be less than 21 years");
            }
        }
        Optional<User> result = userService.patchUser(id, updates);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.get());
    }
}
