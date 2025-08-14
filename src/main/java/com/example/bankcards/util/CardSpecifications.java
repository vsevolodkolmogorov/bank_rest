package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CardSpecifications {

    public static Specification<Card> belongsToUser(String login) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("login"), login);
    }

    public static Specification<Card> withLastFourDigits(String lastFourDigits) {
        return (root, query, cb) ->
                lastFourDigits == null ? null : cb.equal(root.get("lastFourDigits"), lastFourDigits);
    }

    public static Specification<Card> withStatus(CardStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Card> withBalanceRange(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("balance"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("balance"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("balance"), max);
            }
            return null;
        };
    }
}
