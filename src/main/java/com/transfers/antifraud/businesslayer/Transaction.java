package com.transfers.antifraud.businesslayer;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.CreditCardNumber;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @NotNull
    int amount;
    @CreditCardNumber @NotEmpty
    String cardNumber;
    @NotEmpty
    String ipAddress;
}
