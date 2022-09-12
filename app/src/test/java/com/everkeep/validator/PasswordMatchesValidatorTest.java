package com.everkeep.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.everkeep.AbstractTest;
import com.everkeep.controller.dto.RegistrationRequest;

import java.util.stream.Stream;

class PasswordMatchesValidatorTest extends AbstractTest {

    private final PasswordMatchesValidator validator = new PasswordMatchesValidator();

    @ParameterizedTest
    @MethodSource
    void isValid(RegistrationRequest request, boolean expected) {
        Assertions.assertEquals(expected, validator.isValid(request, null));
    }

    private static Stream<Arguments> isValid() {
        return Stream.of(
                Arguments.of(new RegistrationRequest("pass", "pass", "email@email.com"), true),
                Arguments.of(new RegistrationRequest("pass", "word", "email@email.com"), false)
        );
    }
}
