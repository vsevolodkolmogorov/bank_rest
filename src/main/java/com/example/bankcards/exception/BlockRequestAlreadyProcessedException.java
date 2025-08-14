package com.example.bankcards.exception;

public class BlockRequestAlreadyProcessedException extends RuntimeException{
    public BlockRequestAlreadyProcessedException(String text) {
        super(text);
    }
}
