package com.transfers.antifraud.presentation;

import com.transfers.antifraud.businesslayer.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping("/transactions")
    public TransactionService.ValidationResponse transactionRequest(
            @RequestBody @Valid Transaction transaction) {
        return transactionService.validateTransaction(transaction);
    }

    record Response(String result) {}
}
