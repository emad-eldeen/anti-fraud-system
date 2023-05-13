package com.transfers.antifraud.presentation;

import com.transfers.antifraud.businesslayer.Transaction;
import com.transfers.antifraud.businesslayer.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/anti-fraud")
public class TransactionController {

    @PostMapping("/transactions")
    public Response transactionRequest(@RequestBody @Valid Transaction transaction) {
        try {
            return new Response(TransactionService.validateAmount(transaction).toString());
        } catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    record Response(String result) {}
}
