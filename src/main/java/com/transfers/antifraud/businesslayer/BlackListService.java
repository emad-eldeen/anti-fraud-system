package com.transfers.antifraud.businesslayer;

import com.transfers.antifraud.exceptions.ConflictException;
import com.transfers.antifraud.exceptions.NotFoundException;
import com.transfers.antifraud.persistence.BlackListRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class BlackListService {
    @Autowired
    BlackListRepository blackListRepository;

    public Blacklist addIpToBlackList(IP ip) {
        if (blackListRepository.findByIp_Address(ip.address).isPresent()) {
            throw new ConflictException("IP exists!");
        } else {
            Blacklist blacklistIP = new Blacklist();
            blacklistIP.setIp(ip);
            return blackListRepository.save(blacklistIP);
        }
    }

    public void removeIpFromBlackList(@Valid IP ip) {
        Blacklist blackListIP = blackListRepository.findByIp_Address(ip.address)
                .orElseThrow(() -> new NotFoundException("IP was not found!"));
        blackListRepository.delete(blackListIP);
    }

    public List<Blacklist> listBlacklistIps() {
        List<Blacklist> ips = new ArrayList<>();
        for (Blacklist ip : blackListRepository.findAll()) {
            ips.add(ip);
        }
        return ips;
    }
}
