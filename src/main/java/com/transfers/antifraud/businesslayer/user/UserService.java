package com.transfers.antifraud.businesslayer.user;

import com.transfers.antifraud.persistence.UserRepository;
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
}
