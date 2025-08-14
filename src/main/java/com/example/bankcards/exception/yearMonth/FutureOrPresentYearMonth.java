package com.example.bankcards.exception.yearMonth;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = YearMonthFutureOrPresentValidator.class)
public @interface FutureOrPresentYearMonth {
    String message() default "Срок годности должен быть в будущем или в текущем месяце";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
