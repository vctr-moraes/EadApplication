package com.ead.authuser.controllers;

import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> userModels = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(userModels);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(name = "userId") UUID userId) {
        Optional<UserModel> optionalUserModel = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(optionalUserModel.get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(name = "userId") UUID userId) {
        Optional<UserModel> optionalUserModel = userService.findById(userId);
        userService.delete(optionalUserModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }
}
