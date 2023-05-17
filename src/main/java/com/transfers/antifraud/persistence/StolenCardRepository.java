package com.transfers.antifraud.persistence;

import com.transfers.antifraud.businesslayer.StolenCard;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    Optional<StolenCard> findByCard_Number(String cardNumber);
}
