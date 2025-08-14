package com.example.bankcards.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.bankcards.exception.DecryptionKeyException;
import com.example.bankcards.exception.EncryptionKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncryptionCardServiceImplTest {

    private EncryptionCardServiceImpl encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionCardServiceImpl();
    }

    @Test
    void testEncryptNotNullAndDifferentFromPlainText() {
        String plainText = "1234567890123456";
        String encrypted = encryptionService.encrypt(plainText);

        assertNotNull(encrypted, "Encrypted value should not be null");
        assertNotEquals(plainText, encrypted, "Encrypted value should differ from plain text");
    }

    @Test
    void testEncryptDecryptCycleReturnsOriginal() {
        String plainText = "9876543210987654";
        String encrypted = encryptionService.encrypt(plainText);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(plainText, decrypted, "Decrypted value should match the original plain text");
    }

    @Test
    void testEncryptEmptyString() {
        String encrypted = encryptionService.encrypt("");
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals("", decrypted, "Decrypting an empty string should return empty string");
    }

    @Test
    void testEncryptDecryptWithNullThrowsException() {
        assertThrows(EncryptionKeyException.class, () -> encryptionService.encrypt(null));
        assertThrows(DecryptionKeyException.class, () -> encryptionService.decrypt(null));
    }
}
