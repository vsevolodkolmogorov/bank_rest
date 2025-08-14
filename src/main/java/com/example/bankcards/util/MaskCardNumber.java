package com.example.bankcards.util;

import org.springframework.stereotype.Component;

@Component
public class MaskCardNumber {

    private static final int CARD_NUMBER_LENGTH = 16;

    public String makeMask(String lastFourDigits) {
        if (lastFourDigits == null || lastFourDigits.length() > CARD_NUMBER_LENGTH) {
            throw new IllegalArgumentException("Invalid last four digits");
        }
        int maskLength = CARD_NUMBER_LENGTH - lastFourDigits.length();
        return "*".repeat(maskLength) + lastFourDigits;
    }
}
