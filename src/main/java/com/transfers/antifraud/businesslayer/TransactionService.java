package com.transfers.antifraud.businesslayer;

import com.transfers.antifraud.exceptions.BadRequestException;
import com.transfers.antifraud.persistence.BlackListRepository;
import com.transfers.antifraud.persistence.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Validated
public class TransactionService {
    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    BlackListRepository blackListRepository;

    public enum ValidationResult {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }
    private ValidationResult validateAmount(long amount) {
        if (amount <= 0) {
            throw new BadRequestException("Invalid amount");
        } else if (amount <= 200) {
            return ValidationResult.ALLOWED;
        } else if (amount <= 1500) {
            return ValidationResult.MANUAL_PROCESSING;
        } else { // > 1500
            return ValidationResult.PROHIBITED;
        }
    }

    private boolean validateCard(String cardNumber) {
        Pattern cardPattern = Pattern.compile(Card.CARD_PATTERN);
        return !cardPattern.matcher(cardNumber).matches() || // invalid card number
                stolenCardRepository.findByCard_Number(cardNumber).isEmpty(); // card is in stolen cards table

    }

    private boolean validateIP(String ipAddress) {
        Pattern ipPattern = Pattern.compile(IP.IP_PATTERN);
        return !ipPattern.matcher(ipAddress).matches() || // invalid IP
                blackListRepository.findByIp_Address(ipAddress).isEmpty(); // IP is blacklisted
    }

    public ValidationResponse validateTransaction(Transaction transaction) {
        List<ValidationIssue> validationIssues = new ArrayList<>();
        ValidationResult validationResult = ValidationResult.ALLOWED;
        // card issue
        if (!validateCard(transaction.cardNumber))  {
            validationIssues.add(ValidationIssue.CARD_NUMBER);
            validationResult = ValidationResult.PROHIBITED;
        }
        // ip issue
        if (!validateIP(transaction.ipAddress)) {
            validationIssues.add(ValidationIssue.IP);
            validationResult = ValidationResult.PROHIBITED;
        }
        ValidationResult amountValidationResult = validateAmount(transaction.amount);
        // amount validation will override the validation result only if it is ALLOWED
        if (validationResult == ValidationResult.ALLOWED) {
            validationResult = amountValidationResult;
        }
        // in case the amount was not allowed
        if (amountValidationResult == ValidationResult.PROHIBITED) {
            validationIssues.add(ValidationIssue.AMOUNT);
        }
        if (validationResult == ValidationResult.MANUAL_PROCESSING) {
            validationIssues.add(ValidationIssue.AMOUNT);
        }
        if (validationIssues.isEmpty()) {
            validationIssues.add(ValidationIssue.NONE);
        }
        return new ValidationResponse(validationResult,
                validationIssues.stream()
                        .map(ValidationIssue::getText) // map each enum to the text value
                        .sorted() // sort alphabetically
                        .collect(Collectors.joining(", "))
        );
    }

    enum ValidationIssue {
        AMOUNT("amount"),
        CARD_NUMBER("card-number"),
        IP("ip"),
        NONE("none");
        final String text;
        ValidationIssue(String text) {
            this.text = text;
        }
        public String getText() {
            return text;
        }
    }
    public record ValidationResponse(ValidationResult result, String info){}
}

