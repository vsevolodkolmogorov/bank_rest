package com.example.bankcards.service.impl;

import com.example.bankcards.exception.DecryptionKeyException;
import com.example.bankcards.exception.EncryptionKeyException;
import com.example.bankcards.service.EncryptionCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionCardServiceImpl implements EncryptionCardService {

    // KEY 16 BYTES
    private static final byte[] KEY = "1234567890abcdef".getBytes();
    private static final String ALGORITHM = "AES";

    @Override
    public String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            String result = Base64.getEncoder().encodeToString(encrypted);

            log.info("Encrypt called for card ending with ****{}",
                    plainText.substring(Math.max(0, plainText.length() - 4)));
            return result;
        } catch (Exception e) {
            log.error("Encryption failed: {}", e.getMessage(), e);
            throw new EncryptionKeyException(e);
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            String result = new String(decrypted);

            log.info("Decrypt called for encrypted value (masked) hashCode={}",
                    encryptedText.hashCode());
            return result;
        } catch (Exception e) {
            log.error("Decryption failed: {}", e.getMessage(), e);
            throw new DecryptionKeyException(e);
        }
    }
}

