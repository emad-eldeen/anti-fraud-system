package com.transfers.antifraud.businesslayer.user;

import lombok.Getter;

// Data Transfer Object. For sending user info (exclude password)
@Getter
public class UserDTO {
    int id;
    String username;
    String name;

    public UserDTO(User user) {
        id = user.getId();
        username = user.getUsername();
        name = user.getName();
    }
}
