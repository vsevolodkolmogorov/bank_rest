package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_SERVER", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse("ACCESS_DENIED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BlockRequestAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponse> handleBlockRequestAlreadyProcessedException(BlockRequestAlreadyProcessedException ex) {
        ErrorResponse error = new ErrorResponse("BLOCK_REQUEST_ALREADY_PROCESSED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(CardBlockRequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardBlockRequestNotFoundException(CardBlockRequestNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("CARD_BLOCK_REQUEST_NOT_FOUND", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(AuthenticationProcessException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationProcessException(AuthenticationProcessException ex) {
        ErrorResponse error = new ErrorResponse("AUTHENTICATION_PROCESS", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFoundException(CardNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("CARD_NOT_FOUNDED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(StatusCodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStatusCodeNotFoundException(StatusCodeNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("STATUS_CODE_NOT_FOUNDED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(StatusCodeSameException.class)
    public ResponseEntity<ErrorResponse> handleStatusCodeSameException(StatusCodeSameException ex) {
        ErrorResponse error = new ErrorResponse("STATUS_CODE_SAME", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("USER_NOT_FOUNDED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserRoleNotFoundException(UserRoleNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("USER_ROLE_NOT_FOUNDED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserRoleSameException.class)
    public ResponseEntity<ErrorResponse> handleUserRoleSameException(UserRoleSameException ex) {
        ErrorResponse error = new ErrorResponse("USER_ROLE_SAME", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ExpiryDateSameException.class)
    public ResponseEntity<ErrorResponse> handleExpiryDateSameException(ExpiryDateSameException ex) {
        ErrorResponse error = new ErrorResponse("EXPIRE_DATE_SAME", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        ErrorResponse error = new ErrorResponse("INSUFFICIENT_FUNDS", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }



    @ExceptionHandler(DecryptionKeyException.class)
    public ResponseEntity<ErrorResponse> handleDecryptionKeyException(DecryptionKeyException ex) {
        ErrorResponse error = new ErrorResponse("DECRYPTION_FAILED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EncryptionKeyException.class)
    public ResponseEntity<ErrorResponse> handleEncryptionKeyException(EncryptionKeyException ex) {
        ErrorResponse error = new ErrorResponse("ENCRYPTION_FAILED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ParseTokenException.class)
    public ResponseEntity<ErrorResponse> handleParseTokenException(ParseTokenException ex) {
        ErrorResponse error = new ErrorResponse("PARSE_TOKEN", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(LoginAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyRegisteredException(LoginAlreadyRegisteredException ex) {
        ErrorResponse error = new ErrorResponse("EMAIL_ALREADY_REGISTERED", ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse("VALIDATION_FAILED", errors, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
