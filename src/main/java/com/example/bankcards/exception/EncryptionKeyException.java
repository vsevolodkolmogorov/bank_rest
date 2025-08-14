package com.example.bankcards.exception;

public class EncryptionKeyException extends RuntimeException {
    public EncryptionKeyException(Exception e) {
        super(String.format("Ошибка шифрования %s", e.getMessage()));
    }
}
