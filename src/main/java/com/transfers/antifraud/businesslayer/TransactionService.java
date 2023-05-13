package com.transfers.antifraud.businesslayer;

public class TransactionService {

    public enum ValidationResult {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }
    public static ValidationResult validateAmount(Transaction transaction) {
        if (transaction.amount <= 0) {
            throw new IllegalArgumentException();
        } else if (transaction.amount <= 200) {
            return ValidationResult.ALLOWED;
        } else if (transaction.amount <= 1500) {
            return ValidationResult.MANUAL_PROCESSING;
        } else { // > 1500
            return ValidationResult.PROHIBITED;
        }
    }
}
