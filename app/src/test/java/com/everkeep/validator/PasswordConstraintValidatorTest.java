package com.everkeep.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.everkeep.AbstractTest;

import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PasswordConstraintValidatorTest extends AbstractTest {

    private final PasswordConstraintValidator validator = new PasswordConstraintValidator();

    @ParameterizedTest
    @MethodSource
    void isValid(String password, boolean expected) {
        var context = mock(ConstraintValidatorContext.class);
        var constrainViolationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constrainViolationBuilder);
        when(constrainViolationBuilder.addConstraintViolation()).thenReturn(context);

        Assertions.assertEquals(expected, validator.isValid(password, context));
    }

    private static Stream<Arguments> isValid() {
        return Stream.of(
                Arguments.of("P4$$w0rd", true),
                Arguments.of("P4$$", false),
                Arguments.of("P4ssw0rd", false),
                Arguments.of("Pa$$word", false),
                Arguments.of("p4$$w0rd", false),
                Arguments.of("P4$$W0RD", false),
                Arguments.of("P4$$w0rd ", false)
        );
    }
}
