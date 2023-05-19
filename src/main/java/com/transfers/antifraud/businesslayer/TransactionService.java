package com.transfers.antifraud.businesslayer;

import com.transfers.antifraud.persistence.BlackListRepository;
import com.transfers.antifraud.persistence.StolenCardRepository;
import com.transfers.antifraud.persistence.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Validated
public class TransactionService {
    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    BlackListRepository blackListRepository;
    @Autowired
    TransactionRepository transactionRepository;

    static final int CORRELATION_HOURS = 1;

    public enum ValidationResult {
        ALLOWED,
        MANUAL_PROCESSING,
        PROHIBITED
    }

    private ValidationResponse validateAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount <= 200) {
            return new ValidationResponse(ValidationResult.ALLOWED, ValidationIssue.NONE);
        } else if (amount <= 1500) {
            return new ValidationResponse(ValidationResult.MANUAL_PROCESSING, ValidationIssue.AMOUNT);
        } else { // > 1500
            return new ValidationResponse(ValidationResult.PROHIBITED, ValidationIssue.AMOUNT);
        }
    }

    private ValidationResponse validateCard(String cardNumber) {
        Pattern cardPattern = Pattern.compile(Card.CARD_PATTERN);
        if (!cardPattern.matcher(cardNumber).matches() || // invalid card number
                stolenCardRepository.findByCard_Number(cardNumber).isPresent())// card is in stolen cards table
        {
            return new ValidationResponse(ValidationResult.PROHIBITED, ValidationIssue.CARD_NUMBER);
        } else {
            return new ValidationResponse(ValidationResult.ALLOWED, ValidationIssue.NONE);
        }
    }

    private ValidationResponse validateIP(String ipAddress) {
        Pattern ipPattern = Pattern.compile(IP.IP_PATTERN);
        if (!ipPattern.matcher(ipAddress).matches() || // invalid IP
                blackListRepository.findByIp_Address(ipAddress).isPresent())// IP is blacklisted
        {
            return new ValidationResponse(ValidationResult.PROHIBITED, ValidationIssue.IP);
        } else {
            return new ValidationResponse(ValidationResult.ALLOWED, ValidationIssue.NONE);
        }
    }

    private ValidationResponse validateRegionCorrelation(String cardNumber, LocalDateTime transactionTime,
                                                         Region transactionRegion) {
        List<Transaction> latestTransactions = getLastCardTransactions(cardNumber, transactionTime);
        Set<Region> distinctRegions = latestTransactions.stream()
                .map(Transaction::getRegion)
                .collect(Collectors.toSet());
        distinctRegions.add(transactionRegion);
        if (distinctRegions.size() > 3) {
            return new ValidationResponse(ValidationResult.PROHIBITED, ValidationIssue.REGION_CORRELATION);
        } else if (distinctRegions.size() == 3) {
            return new ValidationResponse(ValidationResult.MANUAL_PROCESSING, ValidationIssue.REGION_CORRELATION);
        } else { // > 2
            return new ValidationResponse(ValidationResult.ALLOWED, ValidationIssue.NONE);
        }
    }

    private ValidationResponse validateIPCorrelation(String cardNumber, LocalDateTime transactionTime,
                                                     String transactionIP) {
        List<Transaction> latestTransactions = getLastCardTransactions(cardNumber, transactionTime);
        Set<String> distinctIPs = latestTransactions.stream()
                .map(Transaction::getIpAddress)
                .collect(Collectors.toSet());
        distinctIPs.add(transactionIP);
        if (distinctIPs.size() > 3) {
            return new ValidationResponse(ValidationResult.PROHIBITED, ValidationIssue.IP_CORRELATION);
        } else if (distinctIPs.size() == 3) {
            return new ValidationResponse(ValidationResult.MANUAL_PROCESSING, ValidationIssue.IP_CORRELATION);
        } else { // > 2
            return new ValidationResponse(ValidationResult.ALLOWED, ValidationIssue.NONE);
        }
    }


    public AggregatedValidationResponse validateTransaction(Transaction transaction) {
        // list of validations to be done
        List<ValidationResponse> validationSet = List.of(
                validateCard(transaction.cardNumber),
                validateIP(transaction.ipAddress),
                validateAmount(transaction.amount),
                validateRegionCorrelation(transaction.cardNumber, transaction.date, transaction.region),
                validateIPCorrelation(transaction.cardNumber, transaction.date, transaction.ipAddress)
        );
        // transaction is prohibited if one validation is prohibited
        boolean isProhibited = validationSet.stream()
                .anyMatch(item -> item.result == ValidationResult.PROHIBITED);
        boolean isManualProcessing = validationSet.stream()
                .anyMatch(item -> item.result == ValidationResult.MANUAL_PROCESSING);
        AggregatedValidationResponse aggregatedResponse = null;
        if (isProhibited) {
            aggregatedResponse = new AggregatedValidationResponse(ValidationResult.PROHIBITED,
                    validationSet.stream()
                            .filter(item -> item.result == ValidationResult.PROHIBITED)
                            .map(ValidationResponse::validationIssue)
                            .collect(Collectors.toList())
            );
        }
        if (!isProhibited && isManualProcessing) {
            aggregatedResponse = new AggregatedValidationResponse(ValidationResult.MANUAL_PROCESSING,
                    validationSet.stream()
                            .filter(item -> item.result == ValidationResult.MANUAL_PROCESSING)
                            .map(ValidationResponse::validationIssue)
                            .collect(Collectors.toList())
            );
        }
        if (aggregatedResponse == null) { // !isProhibited and !isManualProcessing -> Allowed
            aggregatedResponse = new AggregatedValidationResponse(ValidationResult.ALLOWED,
                    List.of(ValidationIssue.NONE));
        }

        transaction.setResult(aggregatedResponse.result());
        transactionRepository.save(transaction);
        return aggregatedResponse;
    }

    private List<Transaction> getLastCardTransactions(String cardNumber, LocalDateTime from) {
        return transactionRepository.findByCardNumberAndDateBetween(cardNumber,
                from.minusHours(CORRELATION_HOURS), from);
    }

    enum ValidationIssue {
        AMOUNT("amount"),
        CARD_NUMBER("card-number"),
        IP("ip"),
        IP_CORRELATION("ip-correlation"),
        REGION_CORRELATION("region-correlation"),
        NONE("none");
        final String text;
        ValidationIssue(String text) {
            this.text = text;
        }
        public String getText() {
            return text;
        }
    }

    record ValidationResponse(ValidationResult result, ValidationIssue validationIssue) {
    }

    public record AggregatedValidationResponse(ValidationResult result, List<ValidationIssue> info) {
        public String getInfo() {
            return info.stream().map(ValidationIssue::getText) // map each enum to the text value
                    .sorted() // sort alphabetically
                    .collect(Collectors.joining(", "));
        }
    }
}

