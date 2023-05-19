package com.transfers.antifraud.persistence;

import com.transfers.antifraud.businesslayer.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findByCardNumberAndDateBetween(String cardNumber, LocalDateTime fromDateTime,
                                                     LocalDateTime toDateTime);
}
