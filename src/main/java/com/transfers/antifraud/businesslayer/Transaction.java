package com.transfers.antifraud.businesslayer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @NotNull
    int amount;
}
