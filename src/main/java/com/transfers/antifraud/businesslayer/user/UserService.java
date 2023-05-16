package com.transfers.antifraud.businesslayer.user;

import com.transfers.antifraud.persistence.UserRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;

    public User createUser(User user) {
        if (userExists(user.getUsername())) {
            throw new UserConflictException("A user with the same username already exists");
        }
        // convert username to lower case
        user.setUsername(user.getUsername().toLowerCase());
        // encode password
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(assignRole());
        if (user.getRole() == Role.ADMINISTRATOR) {
            // unlock admin role
            user.setLocked(false);
        }
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        userRepository.delete(userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new));
    }

    public List<User> listUsers() {
        List<User> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(user);
        }
        return users;
    }

    // checks if a user with the same username already exists
    private boolean userExists(String username) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        return existingUser.isPresent();
    }

    private Role assignRole() {
        if (listUsers().size() == 0) {
            // The first registered user should receive the ADMINISTRATOR role
            return Role.ADMINISTRATOR;
        } else {
            return Role.MERCHANT;
        }
    }

    private boolean validateRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException();
        }
        if (listUsers().size() == 0) {
            // The first registered user should receive the ADMINISTRATOR role
            return role == Role.ADMINISTRATOR;
        } else {
            // admin already exists. admin is not allowed
            return role != Role.ADMINISTRATOR;
        }
    }

    public User changeUserRole(@NotNull String username, Role role) {
        // TODO check why @NotEmpty not working here
        if (username == null) {
            throw new IllegalArgumentException();
        }
        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        if (!validateRole(role)) {
            throw new IllegalArgumentException("Invalid user role");
        } else {
            // in case the user already has this role
            if (dbUser.getRole() == role) {
                throw new UserConflictException("User already has this role");
            }
            dbUser.setRole(role);
            userRepository.save(dbUser);
            return dbUser;
        }
    }

    public void changeUserLockStatus(User user) {
        @NotEmpty
        String username = user.getUsername();
        @NotEmpty
        Operation operation = user.getOperation();
        User dbUser = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        // for safety reasons, admin cant be blocked
        if (dbUser.getRole() == Role.ADMINISTRATOR) {
            throw new IllegalArgumentException();
        }
        switch (operation) {
            case LOCK -> dbUser.setLocked(true);
            case UNLOCK -> dbUser.setLocked(false);
        }
        userRepository.save(dbUser);
    }

}
