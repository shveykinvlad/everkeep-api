package com.everkeep.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.everkeep.annotation.PasswordMatches;
import com.everkeep.controller.dto.RegistrationRequest;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationRequest> {

    @Override
    public boolean isValid(RegistrationRequest user, ConstraintValidatorContext context) {
        return user.password().equals(user.matchingPassword());
    }
}
