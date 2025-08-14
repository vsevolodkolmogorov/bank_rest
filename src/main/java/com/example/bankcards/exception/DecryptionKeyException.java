package com.example.bankcards.exception;

public class DecryptionKeyException extends RuntimeException{
    public DecryptionKeyException(Exception e) {
        super(String.format("Ошибка дешифрования %s", e.getMessage()));
    }
}
