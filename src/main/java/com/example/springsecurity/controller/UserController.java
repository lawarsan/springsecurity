package com.example.springsecurity.controller;

import com.example.springsecurity.util.RequiresPermission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @RequiresPermission({"VIEW_USERS", "ADMIN_ACCESS"})
    @GetMapping
    public ResponseEntity<String> getUsers() {
        return ResponseEntity.ok("User list");
    }

    @RequiresPermission("CREATE_USER")
    @PostMapping
    public ResponseEntity<String> createUser() {
        return ResponseEntity.ok("User created");
    }

    @RequiresPermission("DELETE_USER")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok("User " + id + " deleted");
    }
}
