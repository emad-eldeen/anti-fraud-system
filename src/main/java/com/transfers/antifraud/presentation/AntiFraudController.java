package com.transfers.antifraud.presentation;

import com.transfers.antifraud.businesslayer.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anti-fraud")
public class AntiFraudController {
    @Autowired
    BlackListService blackListService;
    @Autowired
    StolenCardService stolenCardService;
    @PostMapping("/suspicious-ips")
    public Blacklist addSuspiciousIp(@RequestBody @Valid IP ip) {
        return blackListService.addIpToBlackList(ip);
    }

    @DeleteMapping("/suspicious-ips/{ipAddress}")
    public Status deleteSuspiciousIp(@PathVariable @NotEmpty String ipAddress) {
        IP ip = new IP();
        ip.setAddress(ipAddress);
        blackListService.removeIpFromBlackList(ip);
        return new Status("IP " + ipAddress + " successfully removed!");
    }

    @GetMapping("/suspicious-ips")
    public List<Blacklist> listSuspiciousIps() {
        return blackListService.listBlacklistIps();
    }

    @PostMapping("/stolen-cards")
    public StolenCard addStolenCard(@RequestBody @Valid Card card) {
        return stolenCardService.addStolenCard(card);
    }

    @DeleteMapping("/stolen-cards/{cardNumber}")
    public Status deleteStolenCard(@PathVariable String cardNumber) {
        Card card = new Card();
        card.setNumber(cardNumber);
        stolenCardService.removeStolenCard(card);
        return new Status("Card " + cardNumber + " successfully removed!");
    }

    @GetMapping("/stolen-cards")
    public List<StolenCard> listStolenCards() {
        return stolenCardService.listStolenCards();
    }

    record Status(String status) {}
}
