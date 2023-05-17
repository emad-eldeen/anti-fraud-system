package com.transfers.antifraud.persistence;

import com.transfers.antifraud.businesslayer.Blacklist;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlackListRepository extends CrudRepository<Blacklist, Long> {
    Optional<Blacklist> findByIp_Address(String ipAddress);
}
