package com.transfers.antifraud.businesslayer.user;

import lombok.Getter;

// Data Transfer Object. For sending deleted user info
@Getter
public class DeletedUserDTO {
    String username;
    final String status = "Deleted successfully!";
    public DeletedUserDTO(String username) {
        this.username = username;
    }
}
