package com.everkeep.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.WhitespaceRule;

import com.everkeep.annotation.ValidPassword;

import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 16;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        var validator = new PasswordValidator(createRules());
        var result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.join(",", validator.getMessages(result)))
                .addConstraintViolation();

        return false;
    }

    private List<Rule> createRules() {
        return List.of(
                new LengthRule(PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH),
                new WhitespaceRule(),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1));
    }
}
