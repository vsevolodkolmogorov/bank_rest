package com.example.bankcards.service;

public interface EncryptionCardService {
    public String encrypt(String plainText);
    public String decrypt(String encryptedText);
}
