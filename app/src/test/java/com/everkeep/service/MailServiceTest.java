package com.everkeep.service;

import static org.mockito.Mockito.when;

import java.text.MessageFormat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.everkeep.AbstractTest;
import com.everkeep.config.properties.IntegrationProperties;
import com.everkeep.config.properties.MailProperties;

@SpringBootTest(
        classes = {
                MailService.class,
                MessageSourceAutoConfiguration.class})
class MailServiceTest extends AbstractTest {

    private static final String MAIL_TO = "mailTo@example.com";
    private static final String MAIL_FROM = "mailFrom@example.com";
    private static final String UI_URL = "http://localhost";
    private static final String TOKEN_VALUE = "tokenValue";

    @Autowired
    private MailService mailService;
    @Autowired
    private MessageSource messageSource;
    @MockBean
    private MailProperties mailProperties;
    @MockBean
    private IntegrationProperties integrationProperties;
    @MockBean
    private JavaMailSender javaMailSender;
    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailCaptor;

    @AfterEach
    void tearDown() {
        Mockito.reset(mailProperties, integrationProperties, javaMailSender);
    }

    @Test
    void sendUserConfirmationMail() {
        var mailTo = MAIL_TO;
        var mailFrom = MAIL_FROM;
        var uiUrl = UI_URL;
        var tokenValue = TOKEN_VALUE;

        when(integrationProperties.getUiUrl()).thenReturn(uiUrl);
        when(mailProperties.getUsername()).thenReturn(mailFrom);
        mailService.sendUserConfirmationMail(mailTo, tokenValue);

        Mockito.verify(javaMailSender).send(mailCaptor.capture());
        Assertions.assertAll(() -> {
            var actual = mailCaptor.getValue();
            Assertions.assertEquals(mailFrom, actual.getFrom());
            Assertions.assertEquals("Email confirmation", actual.getSubject());
            Assertions.assertEquals(MessageFormat.format(
                    "{0}/users/confirm?token={1}", uiUrl, tokenValue), actual.getText());
            Assertions.assertArrayEquals(new String[]{mailTo}, actual.getTo());
        });
    }

    @Test
    void sendResetPasswordMail() {
        var mailTo = MAIL_TO;
        var mailFrom = MAIL_FROM;
        var uiUrl = UI_URL;
        var tokenValue = TOKEN_VALUE;

        when(integrationProperties.getUiUrl()).thenReturn(uiUrl);
        when(mailProperties.getUsername()).thenReturn(mailFrom);
        mailService.sendResetPasswordMail(mailTo, tokenValue);

        Mockito.verify(javaMailSender).send(mailCaptor.capture());
        Assertions.assertAll(() -> {
            var actual = mailCaptor.getValue();
            Assertions.assertEquals(mailFrom, actual.getFrom());
            Assertions.assertEquals("Password reset", actual.getSubject());
            Assertions.assertEquals(MessageFormat.format(
                    "{0}/users/password/update?email={1}&token={2}", uiUrl, mailTo, tokenValue), actual.getText());
            Assertions.assertArrayEquals(new String[]{mailTo}, actual.getTo());
        });
    }
}
