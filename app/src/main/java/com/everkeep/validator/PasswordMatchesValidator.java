package com.everkeep.validator;

import com.everkeep.annotation.PasswordMatches;
import com.everkeep.controller.dto.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationRequest> {

    @Override
    public boolean isValid(RegistrationRequest user, ConstraintValidatorContext context) {
        return user.password().equals(user.matchingPassword());
    }
}
