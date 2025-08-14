package com.example.bankcards.exception.yearMonth;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.YearMonth;

public class YearMonthFutureOrPresentValidator implements ConstraintValidator<FutureOrPresentYearMonth, YearMonth> {

    @Override
    public boolean isValid(YearMonth value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull обрабатывается отдельно
        }
        return !value.isBefore(YearMonth.now());
    }
}
