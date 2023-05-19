package com.transfers.antifraud.businesslayer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    int amount;
    @CreditCardNumber @NotEmpty
    String cardNumber;
    @NotEmpty
    String ipAddress;
    @DateTimeFormat(pattern="yyyy-MM-ddThh:mm:ss")
    LocalDateTime date;
    @Enumerated(EnumType.STRING)
    Region region;
    @Enumerated(EnumType.STRING)
    TransactionService.ValidationResult result;
}
