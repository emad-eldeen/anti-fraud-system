package com.transfers.antifraud.businesslayer.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_entity")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @NotEmpty
    private String name;
    @NotEmpty
    @Column(unique = true)
    private String username;
    @NotEmpty
    private String password;
    private Role role;
    // locked by default until unlocked by an admin
    private boolean locked = true;
    private Operation operation = null;
}

