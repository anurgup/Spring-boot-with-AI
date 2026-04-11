package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerPatchValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testPatchUser_ageLessThan21_returnsBadRequest() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 18);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Age cannot be less than 21 years"));
    }

    @Test
    public void testPatchUser_ageEquals21_returnsOk() throws Exception {
        User user = new User(1, "Alice", 21);
        when(userService.patchUser(anyInt(), anyMap())).thenReturn(Optional.of(user));

        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 21);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(21));
    }

    @Test
    public void testPatchUser_ageGreaterThan21_returnsOk() throws Exception {
        User user = new User(1, "Bob", 30);
        when(userService.patchUser(anyInt(), anyMap())).thenReturn(Optional.of(user));

        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 30);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    public void testPatchUser_noAgeField_returnsOk() throws Exception {
        User user = new User(1, "Charlie", 25);
        when(userService.patchUser(anyInt(), anyMap())).thenReturn(Optional.of(user));

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Charlie");

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }

    @Test
    public void testPatchUser_userNotFound_returnsNotFound() throws Exception {
        when(userService.patchUser(anyInt(), anyMap())).thenReturn(Optional.empty());

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Ghost");

        mockMvc.perform(patch("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPatchUser_ageBoundaryBelow21_returnsBadRequest() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 20);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Age cannot be less than 21 years"));
    }

    @Test
    public void testPatchUser_ageZero_returnsBadRequest() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 0);

        mockMvc.perform(patch("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Age cannot be less than 21 years"));
    }
}
