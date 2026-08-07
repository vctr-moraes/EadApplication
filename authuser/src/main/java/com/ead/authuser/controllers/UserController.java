package com.ead.authuser.controllers;

import com.ead.authuser.dtos.UserRecordDto;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.UserService;
import com.ead.authuser.specifications.SpecificationTemplate;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    Logger logger = LogManager.getLogger(UserController.class);

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // For pagination on method: @PageableDefault(page = 0, size = 2, sort = "userId", direction = Sort.Direction.ASC)
    @GetMapping
    public ResponseEntity<Page<UserModel>> getAllUsers(SpecificationTemplate.UserSpecification spec,
                                                       Pageable pageable) {

        Page<UserModel> userModelPage = userService.findAll(spec, pageable);

        if (!userModelPage.isEmpty()) {
            for (UserModel user : userModelPage.toList()) {
                user.add(linkTo(methodOn(UserController.class).getOneUser(user.getUserId())).withSelfRel());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(name = "userId") UUID userId) {
        Optional<UserModel> optionalUserModel = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(optionalUserModel.get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID userId) {
        logger.debug("Deleting user with id {}", userId);

        Optional<UserModel> optionalUserModel = userService.findById(userId);
        userService.delete(optionalUserModel.get());

        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable UUID userId,
                                             @RequestBody
                                             @Validated(UserRecordDto.UserView.UserPut.class)
                                             @JsonView(UserRecordDto.UserView.UserPut.class)
                                             UserRecordDto userRecordDto) {

        logger.debug("Updating user with id {}", userId);

        var userModel = userService.findById(userId).get();
        var result = userService.updateUser(userRecordDto, userModel);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Object> updatePassword(@PathVariable UUID userId,
                                                 @RequestBody
                                                 @Validated(UserRecordDto.UserView.PasswordPut.class)
                                                 @JsonView(UserRecordDto.UserView.PasswordPut.class)
                                                 UserRecordDto userRecordDto) {

        logger.debug("Updating password for user with id {}", userId);

        Optional<UserModel> userModelOptional = userService.findById(userId);

        if (!userModelOptional.get().getPassword().equals(userRecordDto.oldPassword())) {
            logger.warn("Old password does not match with new password");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Mismatched old password");
        }

        userService.updatePassword(userRecordDto, userModelOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
    }

    @PutMapping("/{userId}/image")
    public ResponseEntity<Object> updateImage(@PathVariable UUID userId,
                                              @RequestBody
                                              @Validated(UserRecordDto.UserView.ImagePut.class)
                                              @JsonView(UserRecordDto.UserView.ImagePut.class)
                                              UserRecordDto userRecordDto) {

        logger.debug("Updating image for user with id {}", userId);

        var userModel = userService.findById(userId).get();
        userService.updateImage(userRecordDto, userModel);

        return ResponseEntity.status(HttpStatus.OK).body(userModel);
    }
}
