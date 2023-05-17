package com.transfers.antifraud.businesslayer;

import com.transfers.antifraud.exceptions.ConflictException;
import com.transfers.antifraud.exceptions.NotFoundException;
import com.transfers.antifraud.persistence.StolenCardRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class StolenCardService {
    @Autowired
    StolenCardRepository stolenCardRepository;

    public StolenCard addStolenCard(Card card) {
        if (stolenCardRepository.findByCard_Number(card.number).isPresent()) {
            throw new ConflictException("Card exists!");
        } else {
            StolenCard stolenCard = new StolenCard();
            stolenCard.setCard(card);
            return stolenCardRepository.save(stolenCard);
        }
    }

    public void removeStolenCard(@Valid Card card) {
        StolenCard stolenCard = stolenCardRepository.findByCard_Number(card.number)
                .orElseThrow(() -> new NotFoundException("Card was not found!"));
        stolenCardRepository.delete(stolenCard);
    }

    public List<StolenCard> listStolenCards() {
        List<StolenCard> cards = new ArrayList<>();
        for (StolenCard card : stolenCardRepository.findAll()) {
            cards.add(card);
        }
        return cards;
    }
}
