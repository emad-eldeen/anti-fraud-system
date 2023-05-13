package com.transfers.antifraud.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.transfers.antifraud.businesslayer.user.User;
import java.util.Optional;

@Component
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String userName);
}
