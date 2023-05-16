package com.transfers.antifraud.presentation;

import com.transfers.antifraud.businesslayer.user.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid User user) {
        try {
            return new ResponseEntity<>(new UserDTO(userService.createUser(user)),
                    new HttpHeaders(), HttpStatus.CREATED);
        } catch (UserConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/{username}")
    public DeletedUserDTO deleteUser(@PathVariable @NotEmpty String username) {
        try {
            userService.deleteUser(username);
            return new DeletedUserDTO(username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users")
    public List<UserDTO> listUsers() {
        return  userService.listUsers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/users/role")
    public UserDTO changeUserRole(@RequestBody User user) {
        try {
            return new UserDTO(userService.changeUserRole(
                    user.getUsername(), user.getRole()
            ));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (UserConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/users/access")
    public Response changeUserLockStatus(@RequestBody User user) {
        try {
            userService.changeUserLockStatus(user);
            return new Response("User %s %s!".formatted(
                    user.getUsername(),
                    user.getOperation() == Operation.LOCK ? "locked" : "unlocked"
            ));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    record Response(String status) {}
}
