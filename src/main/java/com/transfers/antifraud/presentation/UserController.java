package com.transfers.antifraud.presentation;

import com.transfers.antifraud.businesslayer.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user) {
        return new ResponseEntity<>(new UserDTO(userService.createUser(user)),
                new HttpHeaders(), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{username}")
    public DeletedUserDTO deleteUser(@PathVariable @NotEmpty String username) {
        userService.deleteUser(username);
        return new DeletedUserDTO(username);
    }

    @GetMapping("/users")
    public List<UserDTO> listUsers() {
        return userService.listUsers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/users/role")
    public UserDTO changeUserRole(@RequestBody User user) {
        return new UserDTO(userService.changeUserRole(
                user.getUsername(), user.getRole()
        ));
    }

    @PutMapping("/users/access")
    public Response changeUserLockStatus(@RequestBody User user) {
        userService.changeUserLockStatus(user);
        return new Response("User %s %s!".formatted(
                user.getUsername(),
                user.getOperation() == Operation.LOCK ? "locked" : "unlocked"
        ));
    }

    record Response(String status) {
    }
}
