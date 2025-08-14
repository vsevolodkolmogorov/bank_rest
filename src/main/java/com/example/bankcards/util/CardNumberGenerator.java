package com.example.bankcards.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardNumberGenerator {

    private static final String BIN = "400000";
    private static final int CARD_NUMBER_LENGTH = 16;
    private final Random random = new Random();

    public String generateCardNumber() {
        StringBuilder sb = new StringBuilder(BIN);

        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }

        int checkDigit = calculateLuhnCheckDigit(sb.toString());
        sb.append(checkDigit);

        String cardNumber = sb.toString();
        log.info("Generated card number {} with check digit {}", maskCardNumber(cardNumber), checkDigit);
        return cardNumber;
    }

    private int calculateLuhnCheckDigit(String numberWithoutCheckDigit) {
        int sum = 0;
        boolean alternate = true;
        for (int i = numberWithoutCheckDigit.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numberWithoutCheckDigit.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        int mod = sum % 10;
        int checkDigit = (mod == 0) ? 0 : 10 - mod;
        log.debug("Calculated Luhn check digit {} for number {}", checkDigit, maskCardNumber(numberWithoutCheckDigit));
        return checkDigit;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}

