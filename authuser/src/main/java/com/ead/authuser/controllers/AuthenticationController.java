package com.ead.authuser.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ead.authuser.dtos.UserRecordDto;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@RequestBody
                                               @Validated(UserRecordDto.UserView.RegistrationPost.class)
                                               @JsonView(UserRecordDto.UserView.RegistrationPost.class)
                                               UserRecordDto userRecordDto) {

        if (userService.existsByUsername(userRecordDto.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already in use");
        }

        if (userService.existsByEmail(userRecordDto.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Email is already in use");
        }

        var newUser = userService.registerUser(userRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
}
