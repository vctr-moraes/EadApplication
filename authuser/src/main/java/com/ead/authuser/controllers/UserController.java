package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserRecordDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
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
    public ResponseEntity<Object> deleteUser(@PathVariable UUID userId) {
        Optional<UserModel> optionalUserModel = userService.findById(userId);
        userService.delete(optionalUserModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId,
                                             @RequestBody
                                             @JsonView(UserRecordDto.UserView.UserPut.class)
                                             UserRecordDto userRecordDto) {

        var userModel = userService.findById(userId).get();
        var result = userService.updateUser(userRecordDto, userModel);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable UUID userId,
                                                 @RequestBody
                                                 @JsonView(UserRecordDto.UserView.PasswordPut.class)
                                                 UserRecordDto userRecordDto) {

        Optional<UserModel> userModelOptional = userService.findById(userId);

        if (!userModelOptional.get().getPassword().equals(userRecordDto.oldPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password");
        }

        userService.updatePassword(userRecordDto, userModelOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable UUID userId,
                                              @RequestBody
                                              @JsonView(UserRecordDto.UserView.ImagePut.class)
                                              UserRecordDto userRecordDto) {

        var userModel = userService.findById(userId).get();
        userService.updateImage(userRecordDto, userModel);

        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }
}
